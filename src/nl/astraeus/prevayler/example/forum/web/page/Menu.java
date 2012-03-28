package nl.astraeus.prevayler.example.forum.web.page;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * User: rnentjes
 * Date: 3/28/12
 * Time: 9:07 PM
 */
public class Menu extends Page {
    private HttpServletRequest request;

    @Override
    public Page processRequest(HttpServletRequest request) {
        this.request = request;

        return this;
    }

    @Override
    public Map<String, Object> defineModel() {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put("user", request.getSession().getAttribute("user"));
        
        return result;
    }
}
