package servlets.lk;

import entitys.User;
import validarors.SessionValidator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

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
        }else {
            resp.sendRedirect("/login");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req,resp);
    }
}
