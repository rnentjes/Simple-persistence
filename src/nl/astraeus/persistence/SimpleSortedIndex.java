package nl.astraeus.persistence;

import java.io.Serializable;
import java.util.*;

/**
 * User: rnentjes
 * Date: 3/27/12
 * Time: 10:09 PM
 */
public class SimpleSortedIndex<M extends SimpleModel, T> extends SimpleIndex<M,T> implements Serializable {
    public final static long serialVersionUID = 1L;

    private Comparable<M> comparable;

    protected SimpleSortedIndex(Class<M> cls, String propertyName, Comparable<M> comparable) {
        super(cls, propertyName);

        this.comparable = comparable;
    }

    public void update(M model) {
        Set<Long> set = index.get(getIndexValue(model));

        if (set == null) {
            set = new TreeSet<Long>();

            index.put(getIndexValue(model), set);
        }

        set.add(model.getId());
    }

    public void remove(M model) {
        Set<Long> set = index.get(getIndexValue(model));

        if (set != null) {
            set.remove(model.getId());
        }
    }
    
    public Set<Long> find(T value) {
        Set<Long> result = new HashSet<Long>();

        if (index.get(value) != null) {
            result = index.get(value);
        }

        return result;
    }

    public Set<M> findSmaller(M model) {
        return null;
    }
}
