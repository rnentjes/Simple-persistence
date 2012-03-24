package nl.astraeus.prevayler;

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
public final class StoreBatchTransaction extends ModelTransaction {

	private static final long serialVersionUID = -2023934810496653301L;
	private List<PrevaylerModel> objectsToStore = new LinkedList<PrevaylerModel>();

    private StoreBatchTransaction() {}

    StoreBatchTransaction(PrevaylerModel ... objectsToStore) {
        for (PrevaylerModel model : objectsToStore) {
            this.objectsToStore.add(model);
        }
    }

	public void executeOn(Object prevalentSystem, Date ignored) {
        PrevalentSystem ps = (PrevalentSystem)prevalentSystem;

        for (PrevaylerModel objectToStore : objectsToStore) {
            ps.store(objectToStore);
        }
	}
    
}
