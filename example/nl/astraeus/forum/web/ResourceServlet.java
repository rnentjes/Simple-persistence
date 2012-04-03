package nl.astraeus.forum.web;

import nl.astraeus.forum.model.Member;
import nl.astraeus.forum.model.MemberDao;
import nl.astraeus.forum.web.page.*;
import nl.astraeus.prevayler.PrevaylerStore;
import nl.astraeus.prevayler.Transaction;
import nl.astraeus.util.Util;
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
public class ResourceServlet extends HttpServlet {

    private String head;
    private String bottom;


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();

        uri = "nl/astraeus/forum/web" + uri;
        //nl.astraeus.forum.web
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(uri);

        if (in == null) {
            resp.setStatus(404);
            throw new IllegalStateException("Cannot find resource '" + uri + "'.");
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
    }

}
