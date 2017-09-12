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
        if (validator.isAuthorized(session)){
            User user = (User) session.getAttribute("user");
            String authForm = "<p class=\"pi-draggable\">Вы вошли как:</p>\n" +
                        "          <p class=\"pi-draggable lead\">"+user.getLogin()+"</p>\n" +
                        "          <a href=\"/logout?pagePath=/\" class=\"btn btn-outline-primary pi-draggable\">Выйти</a>";

            Map<String,Object> dataMap = new HashMap<>();
            dataMap.put("authForm",authForm);
            dataMap.put("adminTag",user.getTypeUser().equals("manager")?TagsEnum.adminTag:"");

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().append(PageGenerator.instance().getStaticPage("root01.html", dataMap));

        } else {
            String authForm = " <form class=\"\" action=\"/login\" method=\"post\">\n" +
                    "            <div class=\"form-group\"> <label>Login</label>\n" +
                    "              <input type=\"text\" class=\"form-control\" placeholder=\"@login \" name=\"username\"> </div>\n" +
                    "            <div class=\"form-group\"> <label>Password</label>\n" +
                    "              <input type=\"password\" class=\"form-control\" placeholder=\"password\" name=\"password\"> </div>\n" +
                    "              <input type=\"hidden\" name=\"pagePath\" value=\"/\" />" +
                    "            <button type=\"submit\" class=\"btn btn-primary\">Login</button>\n" +
                    "          </form>";
            Map<String,Object> dataMap = new HashMap<>();
            dataMap.put("authForm",authForm);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().append(PageGenerator.instance().getStaticPage("root01.html", dataMap));
        }
    }
}
