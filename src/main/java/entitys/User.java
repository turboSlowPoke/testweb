package entitys;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "botusers")
@NamedQueries({
        @NamedQuery(name = "User.getParent",
                query = "SELECT u FROM User u WHERE u.leftKey<=:lk AND u.rightKey>=:rk AND u.level<:l AND u.level>:l-4")
})
public class User implements Serializable {
    @Id
    private  long userID;
    private int level;
    private int rightKey;
    private int leftKey;
    private String typeUser = "customer";
    private String login;
    private String password;

    @ManyToMany(mappedBy = "users",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<Task> tasks;

    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private Services services;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private PersonalData personalData;

    @ManyToMany(mappedBy = "users",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<AdvcashTransaction> advcashTransactions;
    @ManyToMany(mappedBy = "childrenUsers",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<LocalTransaction> localTransactions;



    public long getUserID() {
        return userID;
    }

    public int getLevel() {
        return level;
    }


    public int getRightKey() {
        return rightKey;
    }


    public int getLeftKey() {
        return leftKey;
    }


    public String getTypeUser() {
        return typeUser;
    }

    public Services getServices() {
        if (this.services==null)
            this.services= new Services();
        return services;
    }

    public PersonalData getPersonalData() {
        if (this.personalData==null)
            this.personalData=new PersonalData();
        return this.personalData;
    }


    public void setEndDateOfSubscription(LocalDateTime endDateOfSubscription) {
        getServices().setEndDateOfSubscription(endDateOfSubscription);
    }

    public BigDecimal getLocalWallet() {
        PersonalData personalData = getPersonalData();
        return personalData.getLocalWallet();
    }

    public LocalDateTime getEndDateOfSubscription() {
        return getServices().getEndDateOfSubscription();
    }

    @Override
    public String toString() {
        return " UserID: "+getUserID()
                +"| Login: "+login
                +"| Тип: " +typeUser;
    }

    public void addLocalTransactions(LocalTransaction localTransaction1) {
        if (this.localTransactions==null)
            this.localTransactions=new ArrayList<>();
        this.localTransactions.add(localTransaction1);
    }

    public void setLocalWallet(BigDecimal cash) {
        getPersonalData().setLocalWallet(cash);
    }

    public void setAdvcashWallet(String numberWallet){
        getPersonalData().setAdvcashWallet(numberWallet);
    }

    public List<AdvcashTransaction> getAdvcashTransactions() {
        return advcashTransactions;
    }

    public void addAcTransaction(AdvcashTransaction acTransaction) {
        if (this.advcashTransactions==null)
            this.advcashTransactions=new ArrayList<>();
        this.advcashTransactions.add(acTransaction);
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
