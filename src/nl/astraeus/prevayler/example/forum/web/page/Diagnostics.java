package nl.astraeus.prevayler.example.forum.web.page;

import nl.astraeus.prevayler.PrevaylerStore;
import nl.astraeus.prevayler.example.forum.model.Member;
import nl.astraeus.prevayler.example.forum.model.MemberDao;
import nl.astraeus.prevayler.example.forum.model.Topic;

import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * User: rnentjes
 * Date: 3/28/12
 * Time: 3:23 PM
 */
public class Diagnostics extends Page {

    @Override
    public Page processRequest(HttpServletRequest request) {
        Page result = this;

        if ("gc".equals(request.getParameter("action"))) {
            System.gc();
        }

        return result;
    }

    @Override
    public Map<String, Object> defineModel(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put("objectTypes", PrevaylerStore.get().getObjectTypeList());

        result.put("userMemory", ((Runtime.getRuntime().totalMemory() / (1024 * 1024)) - (Runtime.getRuntime().freeMemory() / (1024 * 1024))));
        result.put("freeMemory", (Runtime.getRuntime().freeMemory() / (1024 * 1024)));
        result.put("totalMemory", (Runtime.getRuntime().totalMemory() / (1024 * 1024)));
        result.put("maxMemory", (Runtime.getRuntime().maxMemory() / (1024 * 1024)));
        
        
        return result;
    }


}
