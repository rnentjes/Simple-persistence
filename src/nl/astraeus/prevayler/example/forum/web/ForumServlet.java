package nl.astraeus.prevayler.example.forum.web;

import nl.astraeus.prevayler.PrevaylerStore;
import nl.astraeus.prevayler.example.forum.web.page.DiscussionOverview;
import nl.astraeus.prevayler.example.forum.web.page.ForumStartPage;
import nl.astraeus.prevayler.example.forum.web.page.Page;
import org.apache.commons.io.IOUtils;
import org.stringtemplate.v4.ST;

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

    ST head;
    ST bottom;

    @Override
    public void init() throws ServletException {
        super.init();
        
        head = Page.getTemplate(this.getClass(), "head.html");
        bottom = Page.getTemplate(this.getClass(), "bottom.html");

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
        } else if (uri.equals("/")) {
            doPost(req, resp);
        } else {
            resp.setStatus(404);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session =  req.getSession();

        Page page = (Page)req.getSession().getAttribute("page");

        if (page == null || "main".equals(req.getParameter("action"))) {
            page = new DiscussionOverview();
        }

        PrevaylerStore.begin();

        page = page.processRequest(req);

        session.setAttribute("page", page);

        PrevaylerStore.commit();

        resp.getWriter().print(head.render());

        resp.getWriter().print(page.render());

        resp.getWriter().print(bottom.render());
    }

}
