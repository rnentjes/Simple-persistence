package nl.astraeus.prevayler.example.forum.model;

import nl.astraeus.prevayler.PrevaylerList;
import nl.astraeus.prevayler.PrevaylerModel;
import nl.astraeus.prevayler.PrevaylerReference;
import org.apache.commons.lang.StringEscapeUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * User: rnentjes
 * Date: 3/28/12
 * Time: 3:14 PM
 */
public class Comment extends PrevaylerModel {
    public final static long serialVersionUID = -9038882251579382910L;

    private long date = System.currentTimeMillis();
    private String description = "";
    private PrevaylerReference<Member> creator = new PrevaylerReference<Member>(Member.class);

    public Comment() {}

    public Comment(Member creator) {
        this.creator.set(creator);
    }
    
    public String getDescription() {
        return StringEscapeUtils.escapeHtml(description);
    }

    public String getShortDescription() {
        String description = this.description;

        if (description.length() > 50) {
            description = description.substring(0, 47)+"...";
        }

        return StringEscapeUtils.escapeHtml(description);
    }
    
    public String getDate() {
        DateFormat format = new SimpleDateFormat("dd-MM-yy HH:mm:ss");
        
        return format.format(date);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Member getCreator() {
        return creator.get();
    }
    
}
