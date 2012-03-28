package nl.astraeus.prevayler.example.forum.web;

import nl.astraeus.prevayler.PrevaylerStore;
import nl.astraeus.prevayler.example.forum.web.page.DiscussionOverview;
import nl.astraeus.prevayler.example.forum.web.page.ForumStartPage;
import nl.astraeus.prevayler.example.forum.web.page.Page;
import org.stringtemplate.v4.ST;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

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
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session =  req.getSession();

        Page page = (Page)req.getSession().getAttribute("page");
        
        if (page == null) {
            page = new DiscussionOverview();
        }

        page = page.processRequest(req);

        session.setAttribute("page", page);

        resp.getWriter().print(head.render());

        resp.getWriter().print(page.render());
        
        resp.getWriter().print(bottom.render());
    }

}
