package nl.astraeus.persistence;

import org.prevayler.Transaction;
import java.io.Serializable;
import java.util.Date;

/**
 * User: rnentjes
 * Date: 10/17/12
 * Time: 9:19 PM
 */
public final class CreateIndexTransaction implements Serializable, Transaction {

    private static final long serialVersionUID = 1L;

    private String cls;
    private String property;

    public CreateIndexTransaction(Class<? extends Persistent> cls, String property) {
        this.cls = cls.getName();
        this.property = property;
    }

    @Override
    public void executeOn(Object prevalentSystem, Date date) {
        PersistentObjectStore ps = (PersistentObjectStore)prevalentSystem;

        Class<? extends Persistent> cls = null;
        try {
            cls = (Class<? extends Persistent>) Class.forName(this.cls);

            ps.createIndex(cls, property);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
