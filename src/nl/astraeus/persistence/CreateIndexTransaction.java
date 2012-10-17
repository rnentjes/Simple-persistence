package nl.astraeus.persistence;

import java.io.Serializable;
import java.util.Date;

/**
 * User: rnentjes
 * Date: 10/17/12
 * Time: 9:19 PM
 */
public final class CreateIndexTransaction implements Serializable, org.prevayler.Transaction {

    private static final long serialVersionUID = 1L;

    private String cls;
    private String property;

    public CreateIndexTransaction(Class<? extends SimpleModel> cls, String property) {
        this.cls = cls.getName();
        this.property = property;
    }

    @Override
    public void executeOn(Object prevalentSystem, Date date) {
        PrevalentSystem ps = (PrevalentSystem)prevalentSystem;

        Class<? extends SimpleModel> cls = null;
        try {
            cls = (Class<? extends SimpleModel>) Class.forName(this.cls);

            ps.createIndex(cls, property);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
