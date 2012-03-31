package nl.astraeus.prevayler.example.forum.web.page;

import nl.astraeus.prevayler.example.forum.model.Member;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * User: rnentjes
 * Date: 3/28/12
 * Time: 9:07 PM
 */
public class Menu extends Page {
    private HttpSession session;

    @Override
    public Page processRequest(HttpServletRequest request) {
        this.session = request.getSession();

        return this;
    }

    @Override
    public Map<String, Object> defineModel() {
        Map<String, Object> result = new HashMap<String, Object>();
        
        if (session != null) {
            result.put("user", session.getAttribute("user"));
        }

        return result;
    }
}
