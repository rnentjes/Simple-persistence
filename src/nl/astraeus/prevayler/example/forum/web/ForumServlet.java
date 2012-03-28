package nl.astraeus.prevayler.example.forum.web;

import nl.astraeus.prevayler.PrevaylerStore;
import nl.astraeus.prevayler.Transaction;
import nl.astraeus.prevayler.example.forum.web.page.*;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;

/**
 * User: rnentjes
 * Date: 3/28/12
 * Time: 3:05 PM
 */
public class ForumServlet extends HttpServlet {

    private String head;
    private String bottom;

    @Override
    public void init() throws ServletException {
        super.init();

        try {
            head = IOUtils.toString(getClass().getResourceAsStream("head.html"));
            bottom = IOUtils.toString(getClass().getResourceAsStream("bottom.html"));
        } catch (IOException e) {
            throw new ServletException(e);
        }

        PrevaylerStore.setAutocommit(false);
        PrevaylerStore.setSafemode(true);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();

        if (uri.startsWith("/resources/")) {
            uri = "nl/astraeus/prevayler/example/forum/web" + uri;
            //nl.astraeus.prevayler.example.forum.web
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(uri);

            if (in == null) {
                throw new IllegalStateException("Cannot find resource '" + uri + "'");
            }

            if (uri.endsWith("js")) {
                resp.setContentType("text/javascript");
            } else if (uri.endsWith("css")) {
                resp.setContentType("text/css");
            } else if (uri.endsWith("png")) {
                resp.setContentType("image/png");
            } else if (uri.endsWith("jpg")) {
                resp.setContentType("image/jpeg");
            }

            IOUtils.copy(in, resp.getOutputStream());
        } else if (uri.equals("/") || uri.equals("/#")) {
            doPost(req, resp);
        } else {
            resp.setStatus(404);
        }
    }

    @Override
    protected void doPost(final HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session =  req.getSession();
        boolean ajax = "true".equals(req.getParameter("ajax"));

        Page page = (Page)session.getAttribute("page");
        Page menu = (Page)session.getAttribute("menu");
        
        System.out.println("Request start, page="+page);
        
        if (menu == null) {
            menu = new Menu();

            session.setAttribute("menu", menu);
        }

        if (page == null || "menumain".equals(req.getParameter("action"))) {
            page = new ForumOverview();
        } else if ("menulogin".equals(req.getParameter("action"))) {
            page = new Login(page);
        } else {
            final Page myPage = page;

            Transaction<Page> t = new Transaction<Page>() {
                @Override
                public void execute() {
                    setResult(myPage.processRequest(req));
                }
            };
            
            page = t.getResult();
        }

        menu.processRequest(req);

        session.setAttribute("page", page);

        if (!ajax) {
            resp.getWriter().print(head);
        }

        resp.getWriter().print(menu.render());
        resp.getWriter().print(page.render());

        if (!ajax) {
            resp.getWriter().print(bottom);
        }

        System.out.println("Request ends, page="+page);
    }

}
