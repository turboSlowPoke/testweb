package servlets;

import entitys.User;
import exceptions.NoUserInSessionException;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LogoutServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(LogoutServlet.class);
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session!=null
                &&session.getAttribute("authorized")!=null
                &&(boolean)session.getAttribute("authorized")){
            try {
                User user = (User) session.getAttribute("user");
                if (user == null)
                    throw new NoUserInSessionException();
                session.invalidate();
                log.info("Рзлогинился юзер "+user);
                resp.sendRedirect("/login");
            }catch (NoUserInSessionException e){
                log.error("В авторизованной сессии нет юзера");
                session.invalidate();
            }
        } else {
            resp.sendRedirect("/login");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req,resp);
    }
}
