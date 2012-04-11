package nl.astraeus.persistence;

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
public abstract class ModelTransaction<M extends SimpleModel> implements Serializable, Transaction {

    public Map<Long, SimpleModel> getModelStore(PrevalentSystem prevalentSystem, Class<? extends SimpleModel> cls) {
        Map<Long, SimpleModel> result = prevalentSystem.getModelMap(cls);

        return result;
    }
}
