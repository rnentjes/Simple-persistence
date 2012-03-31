package nl.astraeus.prevayler;

import javax.annotation.CheckForNull;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * PrevaylerDao
 * <p/>
 * User: rnentjes
 * Date: 7/20/11
 * Time: 11:12 AM
 */
public abstract class PrevaylerDao<M extends PrevaylerModel> {

    public M getNewModelInstance() {
        M instance = null;

        try {
            Type type = getModelClass();
            
            instance = (M) ((Class) type).newInstance();
        } catch (InstantiationException ex) {
            throw new IllegalStateException(ex);
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }

        return instance;
    }

    protected Class<M> getModelClass() {
        Class<M> result = null;

        ParameterizedType pt = null;

        pt = (ParameterizedType) getClass().getGenericSuperclass();

        Type type = pt.getActualTypeArguments()[0];
        result = (Class<M>) type;

        return result;
    }

    @CheckForNull
    public M find(Long pk) {
        return PrevaylerStore.get().find(getModelClass(), pk);
    }

    public Collection<M> findAll() {
        List<M> result = new LinkedList<M>();

        if (PrevaylerStore.get().isSafemode()) {
            result.addAll(getValues());
        } else {
            result.addAll(getModelValues());
        }

        return result;
    }

    public Collection<M> findAll(Comparator<M> comp) {
        Collection<M> result = new TreeSet<M>(comp);

        if (PrevaylerStore.get().isSafemode()) {
            result.addAll(getValues());
        } else {
            result.addAll(getModelValues());
        }

        return result;
    }

    public Collection<M> find(int from, int to) {
        return find(new Comparator<M>() {
            public int compare(M o1, M o2) {
                return o1.getId().compareTo(o2.getId());
            }
        }, from, to);
    }

    public Collection<M> find(Comparator<M> comp, int from, int to) {
        Class<M> cls = getModelClass();
        List<M> result = new LinkedList<M>();

        List<M> values = new LinkedList<M>();

        values.addAll(getModelValues());
        Collections.sort(values, comp);

        try {
            for (int i = from; i < to; i++) {
                if (values.size() > i) {
                    if (PrevaylerStore.get().isSafemode()) {
                        result.add(cls.cast(values.get(i).clone()));
                    } else {
                        result.add(values.get(i));
                    }
                }
            }
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException(e);
        }

        return result;
    }

    public Collection<M> filter(Filter<M> filter) {
        Class<M> cls = getModelClass();
        List<M> result = new LinkedList<M>();

        try {
            for (M m : getModelValues()) {
                if (filter.include(m)) {
                    if (PrevaylerStore.get().isSafemode()) {
                        result.add(cls.cast(m.clone()));
                    } else {
                        result.add(m);
                    }
                }
            }
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException(e);
        }

        return result;
    }

    /** returnes a cloned set of all values */
    private Collection<? extends M> getValues() {
        Collection<M> result = new TreeSet<M>();
        Class<M> cls = getModelClass();
        
        try {
            for (PrevaylerModel m : PrevaylerStore.get().getModelMap(getModelClass()).values()) {
                result.add(cls.cast(m.clone()));
            }
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException(e);
        }
        
        return result;
    }

    /** The returned model values are not cloned yet! (safemode) */
    private Collection<? extends M> getModelValues() {
        return (Collection<? extends M>)PrevaylerStore.get().getModelMap(getModelClass()).values();
    }

    public void store(M model) {
        if (PrevaylerStore.get().getTransaction() != null) {
            PrevaylerStore.get().getTransaction().store(model);
        } else {
            PrevaylerStore.get().store(model);
        }
    }

    public <A extends PrevaylerModel> void store(A ... model) {
        if (PrevaylerStore.get().getTransaction() != null) {
            // todo: warn, no need to use this function in a transaction
            PrevaylerStore.get().getTransaction().store(model);
        } else {
            PrevaylerStore.get().store(model);
        }
    }

    public void remove(Long key) {
        M model = find(key);
        remove(model);
    }

    public void remove(M model) {
        if (PrevaylerStore.get().getTransaction() != null) {
            PrevaylerStore.get().getTransaction().remove(model);
        } else {
            PrevaylerStore.get().remove(model);
        }
    }

    public int size() {
        return PrevaylerStore.get().getModelMap(getModelClass()).size();
    }
}
