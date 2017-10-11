package db_services;

import calculates.CalculateOfPayment;
import configs.TypeOfPurchase;
import entitys.*;
import org.apache.log4j.Logger;
import org.eclipse.persistence.config.CacheUsage;
import org.eclipse.persistence.config.QueryHints;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DbService {
    private static final Logger log = Logger.getLogger(DbService.class);
    private static DbService db_service;
    private List<Long> usersWithoutRef;
    private final EntityManagerFactory managerFactory;

    private DbService() {
        this.managerFactory = Persistence.createEntityManagerFactory("eclipsMysql");
        usersWithoutRef = new ArrayList<>(Arrays.asList(125897635l,309672610l,172219948l,95800325l,154393865l,245480645l));
    }

    public static synchronized DbService getInstance(){
        if (db_service==null)
            db_service = new DbService();
        return db_service;
    }

    public synchronized User getUserFromDb(String username) throws NoLoginInDbException {
        EntityManager em = managerFactory.createEntityManager();
        Query query = em.createQuery("SELECT u FROM User u JOIN u.personalData p  WHERE p.userNameTelegram=:n").setParameter("n",username);
        List<User> users = query.getResultList();
        em.close();
        if (users==null||users.size()!=1)
            throw new NoLoginInDbException();
        return users.get(0);
    }

    public synchronized User getUserFromDb(long userId) throws NoUserInDbException {
        EntityManager em = managerFactory.createEntityManager();
        EntityTransaction tr = em.getTransaction();
        tr.begin();
        User user = em.find(User.class,userId);
        em.refresh(user);
        tr.commit();
        em.close();
        if (user==null)
            throw new NoUserInDbException(userId);
        return  user;
    }

    public synchronized void transactionHandler(long userId, AdvcashTransaction acTransaction, String typeOfParchase){
        EntityManager em = managerFactory.createEntityManager();
        EntityTransaction tr = em.getTransaction();

        tr.begin();
        log.info("начало транзакции");
        User paidUser = em.find(User.class,userId);
        em.refresh(paidUser);
        int paidUserLevel = paidUser.getLevel();
        int lk = paidUser.getLeftKey();
        int rk = paidUser.getRightKey();
        //вычисляем размеры выплат для 3 линий
        BigDecimal paymentForFirstLine = CalculateOfPayment.calcForFirstLine(acTransaction.getAc_amount());
        BigDecimal paymentForSecondLine = CalculateOfPayment.calcForSecondLine(acTransaction.getAc_amount());
        BigDecimal paymentForThirdLine = CalculateOfPayment.calcForThirdLine(acTransaction.getAc_amount());

        //находим пригластителей, если они еть то добавляем тразакции и пополняем кошелёк
        Query query = em.createQuery("SELECT u FROM User u WHERE u.leftKey<=:lk AND u.rightKey>=:rk AND u.level<:l AND u.level>:l-4")
                .setParameter("l",paidUserLevel)
                .setParameter("lk",lk)
                .setParameter("rk",rk)
                .setHint(QueryHints.CACHE_USAGE, CacheUsage.NoCache);
        List<User> parentUsers = query.getResultList();
        if (parentUsers!=null&&parentUsers.size()>0) {
            for (User u : parentUsers) {
                //проверим доступны ли юзеру начисления
                if (withoutRef(u.getUserID()))
                    continue;
                //создаем транзакции для истрии выплат
                LocalTransaction localTransaction2 = new LocalTransaction(LocalDateTime.now(), paymentForSecondLine, paidUser);
                LocalTransaction localTransaction3 = new LocalTransaction(LocalDateTime.now(), paymentForThirdLine, paidUser);
               // em.refresh(u.getPersonalData());
                if (u.getServices().getEndDateOfSubscription().toLocalDate().isAfter(LocalDate.now())
                        &&u.getAdvcashTransactions()!=null
                        &&u.getAdvcashTransactions().size()>0
                        ||u.getServices().getUnlimit()) {
                    int parentLevel = u.getLevel();
                    switch (paidUserLevel - parentLevel) {
                        case 1:
                            LocalTransaction localTransaction1 = new LocalTransaction(LocalDateTime.now(), paymentForFirstLine, u);
                            u.addLocalTransactions(localTransaction1);
                            u.getPersonalData().setLocalWallet(u.getPersonalData().getLocalWallet().add(paymentForFirstLine));
                            log.info("Проценты начислены сумма="+paymentForFirstLine+" для пользователя " +u+" итого в кошельке "+u.getPersonalData().getLocalWallet());
                            if (!u.getPersonalData().getReferalsForPrize().contains(paidUser.getUserID())){
                                System.out.println(u.getPersonalData().getReferalsForPrize());
                                u.getPersonalData().addReferalForPrize(paidUser.getUserID());
                                log.info("пользователю "+u+"добавлен реферал для премии"+paidUser);
                                //если набрал очередные 10 клиентов то выплачиваем премию
                                if (u.getPersonalData().getReferalsForPrize().size()>=u.getPersonalData().getCountPrize()){
                                    LocalTransaction prize = new LocalTransaction(LocalDateTime.now(),new BigDecimal("1000.00"),u);
                                    u.addLocalTransactions(prize);
                                    u.getPersonalData().setLocalWallet(u.getPersonalData().getLocalWallet().add(new BigDecimal("1000.00")));
                                    u.getPersonalData().addCountPrize(10);
                                    u.getPersonalData().setPrize(1);
                                    log.info("пользователю "+u+" начислена премия за 10 клиентов");
                                }
                            }
                            break;
                        case 2:
                                u.addLocalTransactions(localTransaction2);
                                u.setLocalWallet(u.getLocalWallet().add(paymentForSecondLine));
                                log.info("Проценты начислены сумма="+paymentForSecondLine+" для пользователя " +u+" итого в кошельке "+u.getPersonalData().getLocalWallet());
                            break;
                        case 3:
                                u.addLocalTransactions(localTransaction3);
                                u.setLocalWallet(u.getLocalWallet().add(paymentForThirdLine));
                                log.info("Проценты начислены сумма="+paymentForThirdLine+" для пользователя " +u+" итого в кошельке "+u.getPersonalData().getLocalWallet());
                            break;
                        default:
                            log.error("проценты не начислены, не правильный уровень родителя:\n" + u);
                    }
                }
            }

        } else {
            log.info("пригласители для userId="+userId+" не найдены");
        }
        paidUser.addAcTransaction(acTransaction);
        if (paidUser.getPersonalData().getAdvcashWallet()==null)
            paidUser.setAdvcashWallet(acTransaction.getAc_src_wallet());
        log.info("транзакция добавлена для userId="+userId);
        //продляем подписку
        if (paidUser.getServices().getEndDateOfSubscription()==null||paidUser.getServices().getEndDateOfSubscription().isBefore(LocalDateTime.now()))
            paidUser.setEndDateOfSubscription(LocalDateTime.now());
        if (typeOfParchase.equals(TypeOfPurchase.ONE_MONTH))
            paidUser.setEndDateOfSubscription(paidUser.getEndDateOfSubscription().plusMonths(1));//plusMonths(1));
        else if (typeOfParchase.equals(TypeOfPurchase.TWO_MONTH))
            paidUser.setEndDateOfSubscription(paidUser.getEndDateOfSubscription().plusMonths(2));//plusMonths(2));
        else if (typeOfParchase.equals(TypeOfPurchase.THREE_MONTH))
            paidUser.setEndDateOfSubscription(paidUser.getEndDateOfSubscription().plusMonths(3));//plusMonths(3));
        else if (typeOfParchase.equals(TypeOfPurchase.PRIVATE_CHAT))
            paidUser.getServices().setOnetimeConsultation(true);
        else if (typeOfParchase.equals(TypeOfPurchase.UNLIMIT))
            paidUser.getServices().setUnlimit(true);
        else 
            log.error("!!!подписка не продлена для userid "+userId);

        tr.commit();
        em.close();
    }
    private boolean withoutRef(long userID) {
        boolean check = false;
        for (Long id : usersWithoutRef) {
            if (id == userID) {
                check = true;
                break;
            }
        }
        return check;
    }

    public synchronized List<User> getReferals(User user, int levelReferals) {
        EntityManager em = managerFactory.createEntityManager();
        Query query = em.createQuery("SELECT u FROM User u WHERE u.leftKey>:lk AND u.rightKey<:rk AND u.level=:l")
                .setParameter("lk",user.getLeftKey())
                .setParameter("rk",user.getRightKey())
                .setParameter("l",user.getLevel()+levelReferals);
        List<User> users = query.getResultList();
        em.close();
        return users;
    }

    public synchronized Integer getSumAllUsers() {
        EntityManager em = managerFactory.createEntityManager();
        Query query = em.createQuery("SELECT u FROM User u");
        List<User> users = query.getResultList();
        em.clear();
        em.close();
        return users==null?0:users.size();
    }

    public synchronized Integer getSumSubscribers() {
        EntityManager em = managerFactory.createEntityManager();
        Query query = em.createQuery("SELECT u FROM User u JOIN u.services s  WHERE s.endDateOfSubscription>:d OR s.unlimitSubscription=:b")
                .setParameter("d",LocalDateTime.now())
                .setParameter("b",true);
        List<User> users = query.getResultList();
        em.clear();
        em.close();
        return users!=null?users.size():0;
    }

    public synchronized Integer getSumVip() {
        EntityManager em = managerFactory.createEntityManager();
        Query query = em.createQuery("SELECT u FROM User u JOIN u.services s  WHERE s.unlimitSubscription=:b")
                .setParameter("b",true);
        List<User> users = query.getResultList();
        em.clear();
        em.close();
        return users!=null?users.size():0;
    }

    public List<AdvcashTransaction> getAcTransacrions() {
        EntityManager em = managerFactory.createEntityManager();
        Query query = em.createQuery("SELECT tr FROM AdvcashTransaction tr WHERE tr.ac_start_date>:t" )
                .setParameter("t",LocalDateTime.of(2017,8,27,00,00,00));
        List<AdvcashTransaction> acTransacrions = query.getResultList();
        System.out.println("actr="+acTransacrions.size());
        em.clear();
        em.close();
        return acTransacrions;
    }

    public List<LocalTransaction> getLocalTransactions() {
        EntityManager em = managerFactory.createEntityManager();
        Query query = em.createQuery("SELECT tr FROM LocalTransaction tr WHERE tr.dateTime>:t" )
                .setParameter("t",LocalDateTime.of(2017,8,28,00,00,00));
        List<LocalTransaction> localTransactions  = query.getResultList();
        em.clear();
        em.close();
        return localTransactions;
    }

    public List<Task> getTasks(String taskStatus) {
        EntityManager em = managerFactory.createEntityManager();
        Query query = em.createQuery("SELECT t FROM Task t WHERE t.status=:s" )
                .setParameter("s",taskStatus);
        List<Task> tasks  = query.getResultList();
        em.clear();
        em.close();
        return tasks;
    }

    public synchronized void updateUser(PersonalData personalData) {
        EntityManager em = managerFactory.createEntityManager();
        EntityTransaction tr = em.getTransaction();
        tr.begin();
        try {
            em.merge(personalData);
            tr.commit();
        }catch (Exception e){
            log.error("Ошибка при слиянии сущностей");
            tr.rollback();
        }
        em.clear();
        em.close();
    }

    public synchronized void addNews(News news) {
        EntityManager em = managerFactory.createEntityManager();
        EntityTransaction tr = em.getTransaction();
        tr.begin();
        try {
            em.persist(news);
            tr.commit();
        }catch (Exception e){
            log.error("Ошибка при сохранении новости "+news);
            tr.rollback();
        }
        em.clear();
        em.close();
    }

    public synchronized List<News> getNews() {
        EntityManager em = managerFactory.createEntityManager();
        Query query = em.createQuery("SELECT n FROM News n ORDER BY n.dateTime DESC" );
        List<News> newsList  = query.getResultList();
        em.clear();
        em.close();
        return newsList;
    }

    public synchronized News getNews(Integer id){
        EntityManager em = managerFactory.createEntityManager();
        News news = em.find(News.class,id);
        em.clear();
        em.close();
        return news;
    }

    public synchronized void deleteNews(Integer id) {
        EntityManager em = managerFactory.createEntityManager();
        EntityTransaction tr = em.getTransaction();
        News news = em.find(News.class,id);
        tr.begin();
        try {
            em.remove(news);
            tr.commit();
            System.out.println("Удалил новость");
        }catch (Exception e){
            System.out.println("не сог удалить новость");
            e.printStackTrace();
            tr.rollback();
        }
        em.clear();
        em.close();
    }
}
