package nl.astraeus.forum.main;

import nl.astraeus.forum.model.Comment;
import nl.astraeus.forum.model.CommentDao;
import nl.astraeus.prevayler.PrevaylerModel;
import nl.astraeus.prevayler.PrevaylerStore;

import java.util.Collection;
import java.util.Map;

/**
 * User: rnentjes
 * Date: 3/31/12
 * Time: 8:38 PM
 */
public class TestShowComments {

    public static void main(String [] args) {
        CommentDao dao = new CommentDao();

        Collection<Comment> comments = dao.findAll();

        for (Comment comment : comments) {
            System.out.println("Comment: "+comment);
        }

        for (Map.Entry<Class<? extends PrevaylerModel>, Integer> entry: PrevaylerStore.get().getObjectTypeMap().entrySet()) {
            System.out.println("Class: "+entry.getKey()+" -> "+entry.getValue());
        }
    }

}
