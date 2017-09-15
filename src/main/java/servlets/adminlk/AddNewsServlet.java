package servlets.adminlk;


import templayter.PageGenerator;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;;
import java.io.IOException;
import java.util.Collection;

@MultipartConfig(
        fileSizeThreshold = 16768,
        maxRequestSize = 10L * 1024 * 1024,
        maxFileSize = 10L * 1024 * 1024)
public class AddNewsServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Collection<Part> parts = req.getParts();
        for (Part part : parts){
            Long filesize = part.getSize();
            String fileName = part.getSubmittedFileName();

            if (filesize==0&&(fileName==null||fileName.isEmpty()))
                continue;

            System.out.println(fileName);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("text/html;charset=UTF-8");
        resp.getWriter().append(PageGenerator.instance().getStaticPage("add-news.html",null));
    }
}
