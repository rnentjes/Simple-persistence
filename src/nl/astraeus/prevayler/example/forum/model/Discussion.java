package nl.astraeus.prevayler.example.forum.model;

import nl.astraeus.prevayler.PrevaylerModel;

/**
 * User: rnentjes
 * Date: 3/28/12
 * Time: 3:14 PM
 */
public class Discussion extends PrevaylerModel {
    public static long serialVersionUID = 1L;

    private String title = "";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
