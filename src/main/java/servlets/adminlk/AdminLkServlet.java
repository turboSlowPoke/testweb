package servlets.adminlk;

import db_services.DbService;
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

public class AdminLkServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(AdminLkServlet.class);
    SessionValidator validator = new SessionValidator();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (validator.isAuthorizedAsAdmin(session)){
            User user = (User) session.getAttribute("user");
            log.info("В админский кабинет вошёл "+user);

            Integer sumAllUsers = DbService.getInstance().getSumAllUsers();
            Integer sumUsersWithActiveSubscription = DbService.getInstance().getSumSubscribers();
            Integer sumVipUsers = DbService.getInstance().getSumVip();

            Map<String,String> dataMap = new HashMap<>();
            dataMap.put("sumAllUsers",sumAllUsers.toString());
            dataMap.put("sumSubscribers",sumUsersWithActiveSubscription.toString());
            dataMap.put("sumVipUsers",sumVipUsers.toString());

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().append(PageGenerator.instance().getStaticPage("adminlk.html",dataMap));
        }else {
            resp.sendRedirect("/login");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       doGet(req,resp);
    }
}
