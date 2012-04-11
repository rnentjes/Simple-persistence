package nl.astraeus.persistence;

import org.prevayler.Transaction;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * StoreModelTransaction
 * <p/>
 * User: rnentjes
 * Date: 7/20/11
 * Time: 12:58 PM
 */
public final class SimpleTransaction implements Serializable, Transaction {

	private static final long serialVersionUID = 1L;

	private Set<SimpleModel> store  = new HashSet<SimpleModel>();
    private Set<SimpleModel> remove = new HashSet<SimpleModel>();

    SimpleTransaction() {}
    
    void store(SimpleModel... models) {
        for (SimpleModel model : models) {
            if (remove.contains(model)) {
                throw new IllegalStateException("Object "+model+" already marked for removal in transaction.");
            }

            SimpleStore.get().setLastUpdateField(model);
            SimpleStore.get().setSavedField(model, true);

            if (store.contains(model)) {
                store.remove(model);
            }

            store.add(model);
        }
    }
    
    void remove(SimpleModel... models) {
        for (SimpleModel model : models) {
            if (store.contains(model)) {
                store.remove(model);
            }

            SimpleStore.get().setSavedField(model, false);

            remove.add(model);
        }
    }
    
    Collection<SimpleModel> getStored() {
        return store;
    }

    Collection<SimpleModel> getRemoved() {
        return remove;
    }

	public void executeOn(Object prevalentSystem, Date ignored) {
        PrevalentSystem ps = (PrevalentSystem)prevalentSystem;
        
        for (SimpleModel model : store) {
            ps.store(model);
        }
        
        for (SimpleModel model : remove) {
            ps.remove(model);
        }
	}

    public boolean hasChanges() {
        return !store.isEmpty() || !remove.isEmpty();
    }
}
