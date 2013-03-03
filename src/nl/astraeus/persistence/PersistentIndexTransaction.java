package nl.astraeus.persistence;

import org.prevayler.Transaction;
import java.io.Serializable;
import java.util.Date;

/**
 * User: rnentjes
 * Date: 10/17/12
 * Time: 9:19 PM
 */
public final class PersistentIndexTransaction<K, M extends Persistent<K>> implements Serializable, Transaction {

    private static final long serialVersionUID = 1L;

    private Class<M> cls;
    private String property;
    private PersistentIndex index;

    public PersistentIndexTransaction(PersistentObjectStore store, Class<M> cls, String property) {
        this.cls = cls;
        this.property = property;
        this.index = store.createIndex(cls, property);
    }

    public void executeOn(Object prevalentSystem, Date date) {
        PersistentObjectStore pos = (PersistentObjectStore)prevalentSystem;

        pos.setIndex(cls, property, index);
    }
}
