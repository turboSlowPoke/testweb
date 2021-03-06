package servlets.lk;

import configs.TagsEnum;
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

public class LkServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(LkServlet.class);
    private SessionValidator validator = new SessionValidator();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        if (validator.isAuthorized(session)) {
            User user = (User) session.getAttribute("user");
            PersonalData userData = user.getPersonalData();

            Map<String,Object> dataMap = new HashMap<>();
            dataMap.put("userName",user.getPersonalData().getUserNameTelegram());
            dataMap.put("firstName",userData.getFirstName()==null?"-":userData.getFirstName());
            dataMap.put("lastName",userData.getLastName()==null?"-":userData.getLastName());
            dataMap.put("userNameTelegram",userData.getUserNameTelegram()==null?"-":userData.getUserNameTelegram());
            dataMap.put("advcashWallet",userData.getAdvcashWallet()==null?"-":userData.getAdvcashWallet());
            dataMap.put("accountCryptoCompare",userData.getAccountCryptoCompare()==null?"-":userData.getAccountCryptoCompare());
            dataMap.put("email",userData.getEmail()==null?"-":userData.getEmail());
            dataMap.put("adminTag",user.getTypeUser().equals("manager")? "adminTag":null);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().append(PageGenerator.instance().getStaticPage("userlk.html",dataMap));

        } else {
            resp.sendRedirect("/login");
        }
    }



    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req,resp);
    }
}
