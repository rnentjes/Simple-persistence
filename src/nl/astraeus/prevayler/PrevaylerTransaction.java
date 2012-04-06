package nl.astraeus.prevayler;

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
public final class PrevaylerTransaction implements Serializable, Transaction {

	private static final long serialVersionUID = 1L;

	private Set<PrevaylerModel> store  = new HashSet<PrevaylerModel>();
    private Set<PrevaylerModel> remove = new HashSet<PrevaylerModel>();

    PrevaylerTransaction() {}
    
    void store(PrevaylerModel ... models) {
        for (PrevaylerModel model : models) {
            if (remove.contains(model)) {
                throw new IllegalStateException("Object "+model+" already marked for removal in transaction.");
            }

            PrevaylerStore.get().setLastUpdateField(model);
            PrevaylerStore.get().setSavedField(model, true);

            if (store.contains(model)) {
                store.remove(model);
            }

            store.add(model);
        }
    }
    
    void remove(PrevaylerModel ... models) {
        for (PrevaylerModel model : models) {
            if (store.contains(model)) {
                store.remove(model);
            }

            PrevaylerStore.get().setSavedField(model, false);

            remove.add(model);
        }
    }
    
    Collection<PrevaylerModel> getStored() {
        return store;
    }

    Collection<PrevaylerModel> getRemoved() {
        return remove;
    }

	public void executeOn(Object prevalentSystem, Date ignored) {
        PrevalentSystem ps = (PrevalentSystem)prevalentSystem;
        
        for (PrevaylerModel model : store) {
            ps.store(model);
        }
        
        for (PrevaylerModel model : remove) {
            ps.remove(model);
        }
	}

    public boolean hasChanges() {
        return !store.isEmpty() || !remove.isEmpty();
    }
}
