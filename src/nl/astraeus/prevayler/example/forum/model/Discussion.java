package nl.astraeus.prevayler.example.forum.model;

import com.sun.org.apache.xerces.internal.impl.dv.DatatypeException;
import nl.astraeus.prevayler.PrevaylerModel;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.Date;

/**
 * User: rnentjes
 * Date: 3/28/12
 * Time: 3:14 PM
 */
public class Discussion extends PrevaylerModel {
    public final static long serialVersionUID = -9038882251579382910L;

    private long date = System.currentTimeMillis();
    private String title = "";
    private String description = "";

    public String getTitle() {
        String title = this.title;

        return StringEscapeUtils.escapeHtml(title);
    }

    public String getShortTitle() {
        String title = this.title;

        if (title.length() > 25) {
            title = title.substring(0, 23)+"...";
        }
        return StringEscapeUtils.escapeHtml(title);
    }

    public void setTitle(String title) {
        this.title = title;
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
        return String.valueOf(new Date(date));
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
