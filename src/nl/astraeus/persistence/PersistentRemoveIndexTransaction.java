package nl.astraeus.persistence;

import org.prevayler.Transaction;

import java.io.Serializable;
import java.util.Date;

/**
 * User: rnentjes
 * Date: 10/17/12
 * Time: 9:19 PM
 */
public final class PersistentRemoveIndexTransaction<K, M extends Persistent<K>> implements Serializable, Transaction {

    private static final long serialVersionUID = 1L;

    private Class<M> cls;
    private String property;

    public PersistentRemoveIndexTransaction(Class<M> cls, String property) {
        this.cls = cls;
        this.property = property;
    }

    public void executeOn(Object prevalentSystem, Date date) {
        PersistentObjectStore pos = (PersistentObjectStore)prevalentSystem;

        pos.removeIndex(cls, property);
    }
}
