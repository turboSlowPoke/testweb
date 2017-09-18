package main;

import db_services.DbService;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import servlets.*;
import servlets.adminlk.*;
import servlets.lk.ChangeDataServlet;
import servlets.lk.LkReferalsServlet;
import servlets.lk.LkServicesServlet;
import servlets.lk.LkServlet;

import javax.servlet.MultipartConfigElement;


public class Main {
    private static final Logger log = Logger.getLogger(Main.class);
    public static void main(String[] args) throws Exception {


        DbService.getInstance();
        System.out.println("dbservice started");
        Server server = new Server(80);
        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        contextHandler.setContextPath("/");
        ServletHolder staticHolder = new ServletHolder(new DefaultServlet());
        staticHolder.setInitParameter("resourceBase", "../resources/main/webcontent/static/");
        staticHolder.setInitParameter("pathInfoOnly", "true");
        contextHandler.addServlet(staticHolder, "/static/*");

        contextHandler.addServlet(SendToAdvcashServlet.class,"/redirectToAdvcash");
        contextHandler.addServlet(SuccessfulPayServlet.class,"/successful");
        contextHandler.addServlet(UnsuccessfulPayServlet.class,"/unsuccessful");
        contextHandler.addServlet(StatusPayServlet.class,"/432757270:AAH-WlbhHAjVRah4VyqqOEKmR-1dR2zeMhs");
        contextHandler.addServlet(StatusPayServlet.class,"/status");

        contextHandler.addServlet(RootServlet.class,"/");
        contextHandler.addServlet(InfoAboutServlet.class,"/info-about");
        contextHandler.addServlet(InfoFAQServlet.class,"/info-faq");
        contextHandler.addServlet(InfoContactsServlet.class,"/info-contacts");
        contextHandler.addServlet(NewsServlet.class,"/news");

        contextHandler.addServlet(LoginServlets.class,"/login");
        contextHandler.addServlet(LogoutServlet.class,"/logout");

        contextHandler.addServlet(LkServlet.class,"/lk");
        contextHandler.addServlet(LkReferalsServlet.class,"/lkreferals");
        contextHandler.addServlet(LkServicesServlet.class,"/lkservices");
        contextHandler.addServlet(ChangeDataServlet.class,"/changedata");


        contextHandler.addServlet(AdminLkServlet.class,"/admin");
        contextHandler.addServlet(AdminLkPaimendsServlet.class,"/admin-payments");
        contextHandler.addServlet(AdminLkTasksServlet.class,"/admin-tasks");
        //включим multipart для загрузки картинок
        ServletHolder upLoadFilesHolder = new ServletHolder(AddNewsServlet.class);
        upLoadFilesHolder.getRegistration().setMultipartConfig(new MultipartConfigElement("./", 1048576, 1048576, 262144));
        contextHandler.addServlet(upLoadFilesHolder,"/addnews");
        contextHandler.addServlet(DeleteNews.class,"/deletenews");


        server.setHandler(contextHandler);
        server.start();
        log.info("*******Server started*********");
        server.join();
    }
}
