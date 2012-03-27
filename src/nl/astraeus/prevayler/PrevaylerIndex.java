package nl.astraeus.prevayler;

import java.io.Serializable;
import java.util.*;

/**
 * User: rnentjes
 * Date: 3/27/12
 * Time: 10:09 PM
 */
public abstract class PrevaylerIndex<M extends PrevaylerModel, T> implements Serializable {
    public final static long serialVersionUID = 1L;
    
    private Map<T, Set<Long>> index = new HashMap<T, Set<Long>>();
    private Class<M> cls;

    protected PrevaylerIndex(Class<M> cls) {
        this.cls = cls;
    }

    public abstract T getIndexValue(M model);
    
    public List<M> find(T value) {
        List<M> result = new LinkedList<M>();
        
        if (index.get(value) != null) {
            for (Long id : index.get(value)) {
                result.add(PrevaylerStore.get().find(cls, id));
            }
        }

        return result;
    }

}
