package nl.astraeus.prevayler.example.forum.model;

import nl.astraeus.prevayler.Filter;
import nl.astraeus.prevayler.PrevaylerDao;
import org.prevayler.Prevayler;

import java.util.Collection;

/**
 * User: rnentjes
 * Date: 3/28/12
 * Time: 8:42 PM
 */
public class MemberDao extends PrevaylerDao<Member> {
    
    public Member login(final String nickName, String password) {
        Member result = null;

        Collection<Member> members = filter(new Filter<Member>() {
            @Override
            public boolean include(Member model) {
                return nickName.equals(model.getNickName());
            }
        });
        
        if (members.size() == 1) {
            result = members.iterator().next();

            if (!result.checkPassword(password)) {
                result = null;
            }
        }

        return result;
    }
}
