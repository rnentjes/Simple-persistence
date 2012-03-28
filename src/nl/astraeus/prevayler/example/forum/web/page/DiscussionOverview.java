package nl.astraeus.prevayler.example.forum.web.page;

import nl.astraeus.prevayler.example.forum.model.Discussion;
import nl.astraeus.prevayler.example.forum.model.DiscussionDao;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * User: rnentjes
 * Date: 3/28/12
 * Time: 3:23 PM
 */
public class DiscussionOverview extends Page {

    private DiscussionDao dao = new DiscussionDao();

    @Override
    public Page processRequest(HttpServletRequest request) {
        Page result = this;

        if ("new".equals(request.getParameter("action"))) {
            result = new DiscussionEdit(this, new Discussion());
        } else if (request.getParameter("selectDiscussion") != null) {
            long id = Long.parseLong(request.getParameter("selectDiscussion"));

            result = new DiscussionEdit(this, dao.find(id));
        }

        return result;
    }

    @Override
    public Map<String, Object> defineModel() {
        Map<String, Object> result = new HashMap<String, Object>();
        DiscussionDao dao = new DiscussionDao();

        result.put("discussions", dao.findAll());
        
        return result;
    }


}
