package nl.astraeus.prevayler.example.forum.model;

import nl.astraeus.prevayler.PrevaylerList;
import nl.astraeus.prevayler.PrevaylerModel;
import nl.astraeus.prevayler.PrevaylerReference;
import org.apache.commons.lang.StringEscapeUtils;
import org.omg.CORBA.PUBLIC_MEMBER;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * User: rnentjes
 * Date: 3/28/12
 * Time: 3:14 PM
 */
public class Topic extends PrevaylerModel {
    public final static long serialVersionUID = -9038882251579382910L;

    private long date = System.currentTimeMillis();
    private String title = "";
    private PrevaylerReference<Member> creator = new PrevaylerReference<Member>(Member.class);
    private PrevaylerList<Comment> comments = new PrevaylerList<Comment>(Comment.class);
    private int views;
    private Date lastPost;

    public Topic() {}
    
    public Topic(Member creator) {
        this.creator.set(creator);
    }
    
    public String getTitle() {
        String title = this.title;

        return StringEscapeUtils.escapeHtml(title);
    }

    public String getShortTitle() {
        String title = this.title;

        if (title.length() > 50) {
            title = title.substring(0, 47)+"...";
        }
        return StringEscapeUtils.escapeHtml(title);
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDate() {
        DateFormat format = new SimpleDateFormat("dd-MM-yy HH:mm:ss");
        
        return format.format(date);
    }
    
    public void addComment(Comment comment) {
        getComments().add(comment);

        lastPost = new Date();
    }

    public Member getCreator() {
        return creator.get();
    }

    public PrevaylerList<Comment> getComments() {
        if (comments == null) {
            comments = new PrevaylerList<Comment>(Comment.class);
        }

        return comments;
    }

    public void addView() {
        views++;
    }
    
    public int getNumberOfReplies() {
        return (getComments().size()-1);
    }
    
    public int getNumberOfViews() {
        return views;
    }
    
    public String getLastPost() {
        if (lastPost == null) {
            lastPost = new Date();
        }

        DateFormat format = new SimpleDateFormat("dd-MM-yy HH:mm:ss");

        return format.format(lastPost);
    }
}
