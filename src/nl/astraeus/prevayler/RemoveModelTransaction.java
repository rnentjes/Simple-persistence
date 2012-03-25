package nl.astraeus.prevayler;

import javax.annotation.Nonnull;
import java.util.Date;

/**
 * StoreModelTransaction
 * <p/>
 * User: rnentjes
 * Date: 7/20/11
 * Time: 12:58 PM
 */
public final class RemoveModelTransaction<M extends PrevaylerModel> extends ModelTransaction<M> {

	private static final long serialVersionUID = -2023934810496653301L;
	private M objectToRemove;

    private RemoveModelTransaction() {}

	RemoveModelTransaction(@Nonnull M objectToRemove) {
        this.objectToRemove = objectToRemove;
	}

	public void executeOn(Object prevalentSystem, Date ignored) {
        if (objectToRemove == null) {
            throw new IllegalArgumentException("Can't remove null object form store.");
        }

        ((PrevalentSystem)prevalentSystem).remove(objectToRemove);
	}

}
