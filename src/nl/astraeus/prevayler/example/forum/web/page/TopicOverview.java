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
    private long topicId;
    private Boolean editing = null;
    private String description = null;
    private boolean first = true;

    private TopicDao dao = new TopicDao();

    public TopicOverview(Page previous, Topic topic) {
        this.previous = previous;
        this.topicId = topic.getId();
    }

    @Override
    public Page processRequest(HttpServletRequest request) {
        Page result = this;

        if ("new".equals(request.getParameter("action"))) {
            result = new TopicEdit(this, new Topic());
        } else if ("comment".equals(request.getParameter("action"))) {
            long id = Long.parseLong(request.getParameter("actionValue"));
            
            editing = true;
            description = "";
        } else if ("save".equals(request.getParameter("action"))) {
            editing = null;
            String description = request.getParameter("description");
            
            if (description != null && description.length() != 0) {
                Topic topic = dao.find(topicId);

                topic.addComment(request.getParameter("description"));

                dao.store(topic);
            }
        } else if ("cancel".equals(request.getParameter("action"))) {
            editing = null;
        } else if ("edit".equals(request.getParameter("action"))) {
            long id = Long.parseLong(request.getParameter("actionValue"));

            result = new TopicEdit(this, dao.find(id));
        } else if ("remove".equals(request.getParameter("action"))) {
            long id = Long.parseLong(request.getParameter("actionValue"));

            dao.remove(id);
        } else if ("back".equals(request.getParameter("action"))) {
            result = previous;
        }
        
        first = true;

        return result;
    }

    @Override
    public Map<String, Object> defineModel() {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put("topic", dao.find(topicId));
        result.put("editing", editing);
        result.put("description", description);
        result.put("controller", this);
        
        return result;
    }
    
    public String getBackground() {
        String result = "#edfffc";

        if (!first) {
            result = "#e9fdf7";
        }
        
        first = !first;

        return result;
    }

}
