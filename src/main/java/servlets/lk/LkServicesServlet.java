package servlets.lk;

import configs.TagsEnum;
import entitys.User;
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

public class LkServicesServlet extends HttpServlet {
    SessionValidator validator = new SessionValidator();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (validator.isAuthorized(session)){
            User user = (User) session.getAttribute("user");
            String endDateOfSubscription = "";
            String onetimeConsultation = "";
            if (user.getServices().getUnlimit()){
                endDateOfSubscription = "Вы VIP, безлимитная подписка";
                onetimeConsultation = "количество консультаций не ограничено";
            } else {
                endDateOfSubscription = user.getServices().getEndDateOfSubscription().toLocalDate().toString();
                onetimeConsultation = user.getServices().getOnetimeConsultation()?"не оплачна":"оплачена";
            }
            Map<String,Object> dataMap = new HashMap<>();
            dataMap.put("userName",user.getPersonalData().getUserNameTelegram());
            dataMap.put("adminTag",user.getTypeUser().equals("manager")? "adminTag":null);
            dataMap.put("endDateOfSubscription",endDateOfSubscription);
            dataMap.put("onetimeConsultation",onetimeConsultation);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().append(PageGenerator.instance().getStaticPage("userslk-serv.html", dataMap));
        }else {
            resp.sendRedirect("/login");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req,resp);
    }
}
