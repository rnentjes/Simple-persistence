package nl.astraeus.forum.web.page;

import nl.astraeus.forum.model.Member;
import nl.astraeus.forum.model.Topic;
import nl.astraeus.forum.model.TopicDao;

import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
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
            Member member = (Member)request.getSession().getAttribute("user");

            result = new TopicEdit(this, new Topic(member));
        } else if ("comment".equals(request.getParameter("action"))) {
            long id = Long.parseLong(request.getParameter("actionValue"));

            Topic topic = dao.find(id);
            
            topic.addView();
            
            dao.store(topic);

            result = new TopicOverview(this, topic);
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
    public Map<String, Object> defineModel(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put("topics", dao.findAll(new Comparator<Topic>() {
            public int compare(Topic o1, Topic o2) {
                if (o2.getLastPostMilli() > o1.getLastPostMilli()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        }));

        result.put("member", request.getSession().getAttribute("user"));

        return result;
    }


}
