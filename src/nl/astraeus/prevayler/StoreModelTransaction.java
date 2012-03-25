package nl.astraeus.prevayler;


import java.util.Date;

/**
 * StoreModelTransaction
 * <p/>
 * User: rnentjes
 * Date: 7/20/11
 * Time: 12:58 PM
 */
public final class StoreModelTransaction<M extends PrevaylerModel> extends ModelTransaction<M> {

	private static final long serialVersionUID = -2023934810496653301L;
	private M objectToStore;
    public static int newCount = 0;
    public static int executeCount = 0;

    private StoreModelTransaction() {}

	StoreModelTransaction(M objectToStore) {
        this.objectToStore = objectToStore;

        newCount++;
	}

    public void executeOn(Object prevalentSystem, Date ignored) {
        executeCount++;

        ((PrevalentSystem)prevalentSystem).store(objectToStore);
    }
    
}
