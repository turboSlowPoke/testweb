package main;

import db_services.DbService;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import servlets.*;
import servlets.adminlk.AdminLkPaimendsServlet;
import servlets.adminlk.AdminLkServlet;
import servlets.lk.LkReferalsServlet;
import servlets.lk.LkServicesServlet;
import servlets.lk.LkServlet;


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
        contextHandler.addServlet(RootServlet.class,"/");
        contextHandler.addServlet(SendToAdvcashServlet.class,"/redirectToAdvcash");
        contextHandler.addServlet(SuccessfulPayServlet.class,"/successful");
        contextHandler.addServlet(UnsuccessfulPayServlet.class,"/unsuccessful");
        contextHandler.addServlet(StatusPayServlet.class,"/432757270:AAH-WlbhHAjVRah4VyqqOEKmR-1dR2zeMhs");
        contextHandler.addServlet(StatusPayServlet.class,"/status");
        contextHandler.addServlet(LoginServlets.class,"/login");
        contextHandler.addServlet(LkServlet.class,"/lk");
        contextHandler.addServlet(LkReferalsServlet.class,"/lkreferals");
        contextHandler.addServlet(LkServicesServlet.class,"/lkservices");
        contextHandler.addServlet(AdminLkServlet.class,"/admin");
        contextHandler.addServlet(AdminLkPaimendsServlet.class,"/admin-payments");
        contextHandler.addServlet(LogoutServlet.class,"/logout");
        contextHandler.addServlet(faqServlet.class,"/FAQ");
        contextHandler.addServlet(aboutServlet.class,"/about");
        server.setHandler(contextHandler);
        server.start();
        log.info("*******Server started*********");
        server.join();
    }
}
