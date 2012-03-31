package nl.astraeus.forum.web;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * User: rnentjes
 * Date: 3/25/12
 * Time: 1:09 PM
 */
public class HttpServer {
    
    public static void main(String [] args) throws Exception {
        HttpServer server = new HttpServer();

        server.startServer(8080);
    }

    public void startServer(int port) throws Exception {
        Server server = new Server(port);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);

        context.setMaxFormContentSize(10000000);
        context.setContextPath("/");

        server.setHandler(context);

        ServletHolder sh = new ServletHolder(new ForumServlet());

        context.addServlet(sh, "/*");


        server.start();
        server.join();
    }
}
