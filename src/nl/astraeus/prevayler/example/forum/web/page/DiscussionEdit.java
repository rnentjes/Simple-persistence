package nl.astraeus.prevayler.example.forum.web.page;

import nl.astraeus.prevayler.Transaction;
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
public class DiscussionEdit extends Page {

    private Page previous;
    private Discussion discussion;

    public DiscussionEdit(Page previous, Discussion discussion) {
        this.previous = previous;
        this.discussion = discussion;
    }

    @Override
    public Page processRequest(HttpServletRequest request) {
        Page result = this;

        if ("save".equals(request.getParameter("action"))) {
            discussion.setTitle(request.getParameter("title"));

            new Transaction() {
                @Override
                public void execute() {
                    DiscussionDao dao = new DiscussionDao();

                    dao.store(discussion);
                }
            };

            result = previous;
        }

        return result;
    }

    @Override
    public Map<String, Object> defineModel() {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put("discussion", discussion);
        
        return result;
    }


}
