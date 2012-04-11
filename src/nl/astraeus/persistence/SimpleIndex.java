package nl.astraeus.persistence;

import java.io.Serializable;
import java.util.*;

/**
 * User: rnentjes
 * Date: 3/27/12
 * Time: 10:09 PM
 */
public abstract class SimpleIndex<M extends SimpleModel, T> implements Serializable {
    public final static long serialVersionUID = 1L;
    
    private Map<T, Set<Long>> index = new HashMap<T, Set<Long>>();
    private Class<M> cls;

    protected SimpleIndex(Class<M> cls) {
        this.cls = cls;
    }

    public abstract T getIndexValue(M model);
    
    public List<M> find(T value) {
        List<M> result = new LinkedList<M>();
        
        if (index.get(value) != null) {
            for (Long id : index.get(value)) {
                result.add(SimpleStore.get().find(cls, id));
            }
        }

        return result;
    }

}
