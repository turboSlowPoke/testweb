package servlets.lk;

import configs.TagsEnum;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LkReferalsServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(LkReferalsServlet.class);
    SessionValidator validator = new SessionValidator();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (validator.isAuthorized(session)){
            User user = (User) session.getAttribute("user");

            String sumReferals1 = "У вас нет рефералов";
            String sumReferals2 = "нет рефералов";
            String sumReferals3 = "нет рефералов";
            String sumPaidReferals = "";
            if (user.getLeftKey()+1<user.getRightKey()) {
                List<User> referals1 = DbService.getInstance().getReferals(user, 1);
                List<User> referals2 = DbService.getInstance().getReferals(user, 2);
                List<User> referals3 = DbService.getInstance().getReferals(user, 3);

                sumReferals1 = referals1!=null||referals1.size()>0?""+referals1.size():"У вас нет рефералов";
                sumReferals2 = referals2!=null||referals2.size()>0?""+referals2.size():"нет рефералов";
                sumReferals3 = referals3!=null||referals3.size()>0?""+referals3.size():"нет рефералов";

                List<User> paidReferals1 =new ArrayList<>();
                if (referals1 != null && referals1.size() > 0) {
                    for (User u : referals1){
                        if (u.getAdvcashTransactions()!=null&&u.getAdvcashTransactions().size()>0)
                            paidReferals1.add(u);
                    }
                }
                sumPaidReferals = ""+paidReferals1.size();

            }

            Map<String,Object> dataMap = new HashMap<>();
            dataMap.put("userName",user.getLogin());
            dataMap.put("adminTag",user.getTypeUser().equals("manager")? "adminTag":null);
            dataMap.put("amount",user.getPersonalData().getLocalWallet().toString());
            dataMap.put("referals1",sumReferals1);
            dataMap.put("referals2",sumReferals2);
            dataMap.put("referals3",sumReferals3);
            dataMap.put("paidReferals",sumPaidReferals);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().append(PageGenerator.instance().getStaticPage("userslk-ref.html", dataMap));

        }else {
            resp.sendRedirect("/login");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req,resp);
    }
}
