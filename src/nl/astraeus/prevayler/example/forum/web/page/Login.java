package nl.astraeus.prevayler.example.forum.web.page;

import nl.astraeus.prevayler.example.forum.model.Member;
import nl.astraeus.prevayler.example.forum.model.MemberDao;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * User: rnentjes
 * Date: 3/28/12
 * Time: 8:13 PM
 */
public class Login extends Page {

    private Page previous;
    private MemberDao dao = new MemberDao();
    
    public Login(Page previous) {
        this.previous = previous;   
    }

    @Override
    public Page processRequest(HttpServletRequest request) {
        Page result = this;
        
        if ("login".equals(request.getParameter("action"))) {
            String nickName = request.getParameter("nickName");
            String password = request.getParameter("password");
                    
            Member member = dao.login(nickName, password);

            if (member != null) {
                request.getSession().setAttribute("user", member);
                result = previous;
            } else {
                // warn
            }

        } else if ("register".equals(request.getParameter("action"))) {
            result = new Registration(this, previous);
        }
        
        return result;
    }

    @Override
    public Map<String, Object> defineModel() {
        Map<String, Object> result = new HashMap<String, Object>();
        
        result.put("member", null);
        
        return result;
    }
}
