package nl.astraeus.prevayler;

import org.prevayler.Transaction;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * StoreModelTransaction
 * <p/>
 * User: rnentjes
 * Date: 7/20/11
 * Time: 12:58 PM
 */
public final class PrevaylerTransaction implements Serializable, Transaction {

	private static final long serialVersionUID = 1L;

	private List<PrevaylerModel> store = new LinkedList<PrevaylerModel>();
    private List<PrevaylerModel> remove = new LinkedList<PrevaylerModel>();

    PrevaylerTransaction() {}
    
    void store(PrevaylerModel ... models) {
        for (PrevaylerModel model : models) {
            store.add(model);
        }
    }
    
    void remove(PrevaylerModel ... models) {
        for (PrevaylerModel model : models) {
            if (store.contains(model)) {
                store.remove(model);
            }

            remove.add(model);
        }
    }
    
    List<PrevaylerModel> getStored() {
        return store;
    }
    
    List<PrevaylerModel> getRemoved() {
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
    
}
