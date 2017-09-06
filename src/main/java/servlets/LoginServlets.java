package servlets;

import db_services.DbService;
import db_services.NoLoginInDbException;
import entitys.User;
import exceptions.NoUserInSessionException;
import exceptions.UncorrectLoginException;
import exceptions.UncorrectPasswordException;
import exceptions.WrongPasswordException;
import org.apache.log4j.Logger;
import templayter.PageGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class LoginServlets extends HttpServlet {
    private static final Logger log = Logger.getLogger(LoginServlets.class);
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Map<String,String[]> parameters = req.getParameterMap();
        //если сессия не авторизована и содержит форму с логином и паролем то пытаемся авторизовать,
        // в случае неправильных или некорректных логин пароль возвращаем форму добавив тег об ошибке
        if (    session != null
                &&(session.getAttribute("authorized")==null||!(boolean)session.getAttribute("authorized"))
                &&parameters!=null
                &&parameters.containsKey("username")
                &&parameters.containsKey("password")) {

            String username = req.getParameter("username");
            String password = req.getParameter("password");
            String pagePath = req.getParameter("pagePath");
            try {
                checkLogin(username); //кинем исключение если логин некорректен
                String md5Password = getMd5Password(password); //кинем исключение если пароль не корректен

                User user = DbService.getInstance().getUserFromDb(username); //исключение если юзера нет

                if (user.getPassword()==null||!user.getPassword().equals(md5Password)) //если у юзера нет пароля или пароли не совпадают, исключение о неправильном пароле
                    throw new WrongPasswordException();

                session = req.getSession();
                session.setAttribute("user", user);
                session.setAttribute("authorized",true);
                session.setMaxInactiveInterval(30*60);
                log.info("авторизовался " +user);
                if (pagePath!=null){
                    if (pagePath.equals("/"))
                        resp.sendRedirect("/");
                }
                resp.sendRedirect("lk");

            } catch (NoLoginInDbException e) {
                log.info("попытка зайти с неверным логином " + username);
                sendLoginPageWithFailtag(session,resp);
            } catch (WrongPasswordException e) {
                log.info("неверный пароль для " + username);
                sendLoginPageWithFailtag(session,resp);
            } catch (UncorrectPasswordException uncorrectPasswordException) {
                log.info("некорректный пароль "+password);
                sendLoginPageWithFailtag(session,resp);
            } catch (UncorrectLoginException e) {
                log.info("некорректный логин "+username);
                sendLoginPageWithFailtag(session,resp);
            }
        } else{
            doGet(req,resp);
        }
    }

    private void sendLoginPageWithFailtag(HttpSession session, HttpServletResponse resp) throws IOException {
        int attempt = 0;
        if (session.getAttribute("attempt")!=null)
            attempt = (int)session.getAttribute("attempt");
        attempt++;
        String failTag = "<p class=\"lead\"><b class=\"text-danger\">Неправильный Login/Password!</b></p>";
        Map<String,Object> dataMap = new HashMap<>();
        dataMap.put("failTag",failTag);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("text/html;charset=UTF-8");
        resp.getWriter().append(PageGenerator.instance().getStaticPage("login.html",dataMap));
    }

    private String getMd5Password(String password) throws UncorrectPasswordException {
        String hashPassword=null;
        if (password==null)
            throw new UncorrectPasswordException();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] byteOfPassword = password.getBytes("UTF-8");
            byte[] digest = md.digest(byteOfPassword);
            BigInteger bigInteger = new BigInteger(1,digest);
            hashPassword = bigInteger.toString(16);
            while (hashPassword.length()<32){
                hashPassword = "0"+hashPassword;
            }
        } catch (NoSuchAlgorithmException e) {
            log.error("несуществующий алгоритм хеширования");
        } catch (UnsupportedEncodingException e) {
            log.error("несуществующая кодировка");
        }
        if (hashPassword==null)
            throw  new UncorrectPasswordException();
        return hashPassword;
    }

    private void checkLogin(String username) throws UncorrectLoginException {
        if (username==null)
            throw new UncorrectLoginException();

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        //если сессия авторизована то переадресуем на страницу logout
        if (session!=null&&session.getAttribute("authorized")!=null&&(boolean)session.getAttribute("authorized")){
         resp.sendRedirect("/logout");
        } else {
            sendLoginPage(resp);
        }
    }

    private void sendLoginPage(HttpServletResponse resp) throws IOException {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("failTag", "");
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("text/html;charset=UTF-8");
        resp.getWriter().append(PageGenerator.instance().getStaticPage("login.html", dataMap));
    }
}
