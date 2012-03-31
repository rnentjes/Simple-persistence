package nl.astraeus.forum.model;

import nl.astraeus.prevayler.PrevaylerList;
import nl.astraeus.prevayler.PrevaylerModel;
import org.apache.commons.lang.StringEscapeUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: rnentjes
 * Date: 3/28/12
 * Time: 3:16 PM
 */
public class Member extends PrevaylerModel {
    public final static long serialVersionUID = 1L;

    private String nickName;
    private String email;
    private String password;
    private boolean superuser;
    private Date lastPost;
    private PrevaylerList<Comment> comments;
    private PrevaylerList<Topic> topics;

    public Member() {
        this("","","");
    }

    public Member(String nickName, String password, String email) {
        this.nickName = nickName;
        this.password = password;
        this.email = email;
    }

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

    public boolean isSuperuser() {
        return superuser;
    }

    public void setSuperuser(boolean superuser) {
        this.superuser = superuser;
    }

    public String toString() {
        return getEscapedNickName();
    }

    public PrevaylerList<Comment> getComments() {
        if (comments == null) {
            comments = new PrevaylerList<Comment>(Comment.class);
        }
        
        return comments;
    }

    public PrevaylerList<Topic> getTopics() {
        if (topics == null) {
            topics = new PrevaylerList<Topic>(Topic.class);
        }

        return topics;
    }

    public void addTopic(Topic topic) {
        getTopics().add(topic);
        
        lastPost = new Date();
    }

    public void addComment(Comment comment) {
        getComments().add(comment);
        
        lastPost = new Date();
    }

    public String getLastPost() {
        String result = "never";

        if (lastPost != null && lastPost.getTime() > 0) {
            DateFormat format = new SimpleDateFormat("dd-MM-yy HH:mm:ss");

            result = format.format(lastPost);
        }

        return result;
    }
    
    public int getNumberOfTopics() {
        return getTopics().size();
    }
    
    public int getNumberOfComments() {
        return getComments().size();
    }
}
