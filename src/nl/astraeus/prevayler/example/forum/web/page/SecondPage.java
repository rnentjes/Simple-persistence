package nl.astraeus.prevayler.example.forum.web.page;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * User: rnentjes
 * Date: 3/28/12
 * Time: 3:23 PM
 */
public class SecondPage extends Page {


    @Override
    public Page processRequest(HttpServletRequest request) {
        Page result = this;

        if ("back".equals(request.getParameter("action"))) {
            result = new ForumStartPage();
        }

        return result;
    }

    @Override
    public Map<String, Object> defineModel() {
        Map<String, Object> result = new HashMap<String, Object>();
        
        result.put("name", "Test 2 !!!");
        
        return result;
    }


}
