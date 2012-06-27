package nl.astraeus.persistence;

import nl.astraeus.persistence.reflect.ReflectHelper;

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
    private String propertyName;

    protected SimpleIndex(Class<M> cls, String propertyName) {
        this.cls = cls;
        this.propertyName = propertyName;
    }

    public T getIndexValue(M model) {
        return (T) ReflectHelper.get().getFieldValue(model, propertyName);
    }

    public abstract void addModel(M model);
    public abstract void removeModel(M model);
    
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
