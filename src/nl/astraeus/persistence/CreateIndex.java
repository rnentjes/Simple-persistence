package nl.astraeus.persistence;

import org.prevayler.Transaction;

import java.io.Serializable;
import java.util.Date;

/**
 * StoreModelTransaction
 * <p/>
 * User: rnentjes
 * Date: 7/20/11
 * Time: 12:58 PM
 */
public final class CreateIndex implements Serializable, Transaction {

	private static final long serialVersionUID = 1L;

    private Class<? extends SimpleModel> cls;
    private String propertyName;

    public CreateIndex(Class<? extends SimpleModel> cls, String propertyName) {
        this.cls = cls;
        this.propertyName = propertyName;
    }

    public void executeOn(Object prevalentSystem, Date ignored) {
        PrevalentSystem ps = (PrevalentSystem)prevalentSystem;

        ps.createIndex(cls, propertyName);
	}

}
