package servlets;


import configs.TagsEnum;
import entitys.User;
import exceptions.NoUserInSessionException;
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

public class RootServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(RootServlet.class);
    private SessionValidator validator = new SessionValidator();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      doGet(req,resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Map<String,Object> dataMap = new HashMap<>();

        if (validator.isAuthorized(session)) {
            User user = (User) session.getAttribute("user");
            dataMap.put("userName", user.getLogin());
            dataMap.put("adminTag", user.getTypeUser().equals("manager") ? "adminTrue" : null);
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("text/html;charset=UTF-8");
        resp.getWriter().append(PageGenerator.instance().getStaticPage("root01.html", dataMap));
    }
}
