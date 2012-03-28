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
public class TopicOverview extends Page {

    private Page previous;
    private Topic topic;

    private TopicDao dao = new TopicDao();

    public TopicOverview(Page previous, Topic topic) {
        this.previous = previous;
        this.topic = topic;
    }

    @Override
    public Page processRequest(HttpServletRequest request) {
        Page result = this;

        if ("new".equals(request.getParameter("action"))) {
            result = new TopicEdit(this, new Topic());
        } else if ("comment".equals(request.getParameter("action"))) {
            long id = Long.parseLong(request.getParameter("actionValue"));

//            result = new TopicComment(this, dao.find(id));
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

        result.put("topic", topic);
        
        return result;
    }


}
