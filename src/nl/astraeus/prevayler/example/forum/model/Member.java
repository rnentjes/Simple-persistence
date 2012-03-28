package nl.astraeus.prevayler.example.forum.model;

import nl.astraeus.prevayler.PrevaylerModel;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * User: rnentjes
 * Date: 3/28/12
 * Time: 3:16 PM
 */
public class Member extends PrevaylerModel {
    public final static long serialVersionUID = 1L;

    private String nickName = "";
    private String email = "";
    private String password = "";

    public String getNickName() {
        return nickName;
    }

    public String getEscapedNickName() {
        return StringEscapeUtils.escapeHtml(nickName);
    }
    
    public String getEscapedEmail() {
        return StringEscapeUtils.escapeHtml(email);
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean checkPassword(String password) {
        return password.equals(this.password);
    }
}
