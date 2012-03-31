package nl.astraeus.forum.model;

import nl.astraeus.prevayler.PrevaylerList;
import nl.astraeus.prevayler.PrevaylerModel;

/**
 * User: rnentjes
 * Date: 3/28/12
 * Time: 3:07 PM
 */
public class Forum extends PrevaylerModel {
    public static long serialVersionUID = 1L;

    private String name;
    private PrevaylerList<Topic> topic = new PrevaylerList<Topic>(Topic.class);
    private PrevaylerList<Member> member = new PrevaylerList<Member>(Member.class);
    
    public Forum() {}
    
    public Forum(String name) {
        this.name = name;
    }
}
