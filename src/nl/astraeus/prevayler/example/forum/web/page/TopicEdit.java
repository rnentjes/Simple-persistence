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
public class TopicEdit extends Page {

    private Page previous;
    private Topic topic;
    private String description = "";

    public TopicEdit(Page previous, Topic topic) {
        this.previous = previous;
        this.topic = topic;
    }

    @Override
    public Page processRequest(HttpServletRequest request) {
        Page result = this;

        if ("save".equals(request.getParameter("action"))) {
            topic.setTitle(request.getParameter("title"));
            description = request.getParameter("description");

            // todo validation
            TopicDao dao = new TopicDao();

            topic.addComment(description);
            dao.store(topic);

            result = previous;
        } else if ("cancel".equals(request.getParameter("action"))) {
            result = previous;
        }

        return result;
    }

    @Override
    public Map<String, Object> defineModel() {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put("topic", topic);
        result.put("description", description);
        
        return result;
    }


}
