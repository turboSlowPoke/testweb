package servlets;

import entitys.User;
import templayter.PageGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AdminLkServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (checkSession(session)){
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().append(PageGenerator.instance().getStaticPage("adminlk.html",null));
        }else {
            resp.sendRedirect("/login");
        }
    }

    private boolean checkSession(HttpSession session) {
        boolean check=false;
        if (session!=null && session.getAttribute("authorized")!=null && (boolean)session.getAttribute("authorized"))
        {
            User user = (User) session.getAttribute("user");
            if (user!=null&&user.getTypeUser().equals("manager")){
                check=true;
            }
        }
        return check;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       doGet(req,resp);
    }
}
