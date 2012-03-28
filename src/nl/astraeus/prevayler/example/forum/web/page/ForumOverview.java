package nl.astraeus.prevayler.example.forum.web.page;

import nl.astraeus.prevayler.example.forum.model.Topic;
import nl.astraeus.prevayler.example.forum.model.TopicDao;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * User: rnentjes
 * Date: 3/28/12
 * Time: 3:23 PM
 */
public class ForumOverview extends Page {

    private TopicDao dao = new TopicDao();

    @Override
    public Page processRequest(HttpServletRequest request) {
        Page result = this;

        if ("new".equals(request.getParameter("action"))) {
            result = new TopicEdit(this, new Topic());
        } else if ("comment".equals(request.getParameter("action"))) {
            long id = Long.parseLong(request.getParameter("actionValue"));

            result = new TopicOverview(this, dao.find(id));
        } else if ("edit".equals(request.getParameter("action"))) {
            long id = Long.parseLong(request.getParameter("actionValue"));

            result = new TopicEdit(this, dao.find(id));
        } else if ("remove".equals(request.getParameter("action"))) {
            long id = Long.parseLong(request.getParameter("actionValue"));

            dao.remove(id);
        }

        return result;
    }

    @Override
    public Map<String, Object> defineModel() {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put("topics", dao.findAll());
        
        return result;
    }


}
