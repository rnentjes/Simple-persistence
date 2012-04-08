package nl.astraeus.forum.web.page;

import nl.astraeus.forum.model.Member;
import nl.astraeus.forum.model.MemberDao;
import nl.astraeus.forum.model.Topic;

import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * User: rnentjes
 * Date: 3/28/12
 * Time: 3:23 PM
 */
public class MemberOverview extends TemplatePage {

    private MemberDao dao = new MemberDao();

    @Override
    public Page processRequest(HttpServletRequest request) {
        Page result = this;

        if ("new".equals(request.getParameter("action"))) {
            Member member = (Member)request.getSession().getAttribute("user");

            result = new TopicEdit(this, new Topic(member));
        }

        return result;
    }

    @Override
    public Map<String, Object> defineModel(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put("members", dao.findAll(new Comparator<Member>() {
            public int compare(Member m1, Member m2) {
                return m1.getNickName().compareTo(m2.getNickName());
            }
        }));
        
        return result;
    }


}
