package servlets;

import configs.TagsEnum;
import entitys.User;
import templayter.PageGenerator;
import validarors.SessionValidator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class faqServlet extends HttpServlet {
    SessionValidator validator = new SessionValidator();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String,Object> dataMap = new HashMap<>();
        if (validator.isAuthorized(req.getSession(false))){
            User user = (User)req.getSession().getAttribute("user");
            dataMap.put("adminTag",user.getTypeUser().equals("manager")? TagsEnum.adminTag:"");
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("text/html;charset=UTF-8");
        resp.getWriter().append(PageGenerator.instance().getStaticPage("FAQ.html", dataMap));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req,resp);
    }
}
