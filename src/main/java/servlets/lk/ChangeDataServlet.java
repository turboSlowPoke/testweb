package servlets.lk;

import entitys.PersonalData;
import entitys.User;
import org.apache.log4j.Logger;
import templayter.PageGenerator;
import validarors.SessionValidator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangeDataServlet extends HttpServlet{
    private static final Logger log = Logger.getLogger(ChangeDataServlet.class);
    private SessionValidator validator = new SessionValidator();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (validator.isAuthorized(session)) {
            User user = (User) session.getAttribute("user");
            PersonalData userData = user.getPersonalData();

            Map<String,Object> dataMap = new HashMap<>();
            dataMap.put("firstName",userData.getFirstName()==null?"-":userData.getFirstName());
            dataMap.put("lastName",userData.getLastName()==null?"-":userData.getLastName());
            dataMap.put("acWallet",userData.getAdvcashWallet()==null?"-":userData.getAdvcashWallet());
            dataMap.put("cryptoCompare",userData.getAccountCryptoCompare()==null?"-":userData.getAccountCryptoCompare());
            dataMap.put("email",userData.getEmail()!=null?userData.getEmail():"-");

            dataMap.put("adminTag",user.getTypeUser().equals("manager")? "adminTag":null);
            dataMap.put("userName",user.getLogin());

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().append(PageGenerator.instance().getStaticPage("change-data.html",dataMap));

        } else {
            resp.sendRedirect("/login");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (validator.isAuthorized(session)){
            User user = (User) session.getAttribute("user");
            log.info("Пришла форма для смены данных для юзера"+user.getLogin());

            String firstName= req.getParameter("firstName");
            String lastName = req.getParameter("lastName");
            String acWallet = req.getParameter("acWallet");
            String cryptoCompare = req.getParameter("cryptoCompare");
            String email = req.getParameter("email");


        }

    }

    private Boolean stringIsValid(String string){
        Boolean check = false;
        if (string!=null){
            String p = "[\\w]*";
            Pattern pattern = Pattern.compile(p,Pattern.UNICODE_CHARACTER_CLASS);
            try {
                Matcher matcher = pattern.matcher(string);
                if (matcher.matches()) {
                    check=true;
                }
            }catch (Exception e){
                log.warn("В string какаято фигня"+string);
            }
        }
        return check;
    }
}
