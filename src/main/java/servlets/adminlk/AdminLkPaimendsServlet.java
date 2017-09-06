package servlets.adminlk;

import db_services.DbService;
import entitys.AdvcashTransaction;
import org.apache.log4j.Logger;
import templayter.PageGenerator;
import validarors.SessionValidator;
import wrappers.Payment;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminLkPaimendsServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(AdminLkPaimendsServlet.class);
    SessionValidator validator = new SessionValidator();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (validator.isAuthorizedAsAdmin(session)){
            List<AdvcashTransaction> acTransactions = DbService.getInstance().getAcTransacrions();
            List<Payment> payments = new ArrayList<>();
            Map<String,Object> dataMap = new HashMap<>();
            for (AdvcashTransaction t : acTransactions)
                    payments.add(new Payment(t));
            dataMap.put("payments",payments);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().append(PageGenerator.instance().getStaticPage("adminlk-payments.html", dataMap));

        }else {
            resp.sendRedirect("/login");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req,resp);
    }
}