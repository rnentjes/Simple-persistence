package nl.astraeus.prevayler;

import org.prevayler.Transaction;

import java.io.Serializable;
import java.util.Map;

/**
 * StoreModelTransaction
 * <p/>
 * User: rnentjes
 * Date: 7/20/11
 * Time: 12:58 PM
 */
public abstract class ModelTransaction<M extends PrevaylerModel> implements Serializable, Transaction {

    public Map<Long, PrevaylerModel> getModelStore(PrevalentSystem prevalentSystem, Class<? extends PrevaylerModel> cls) {
        Map<Long, PrevaylerModel> result = prevalentSystem.getModelMap(cls);

        return result;
    }
}
