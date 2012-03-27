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

    private Map<Long, M> objectMap = null;

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

    public Map<Long, M> getStore() {
        return (Map<Long, M>)PrevaylerStore.get().getModelMap(getModelClass());
    }

    @CheckForNull
    public M find(Long pk) {
        if (objectMap == null) {
            objectMap = (Map<Long, M>)PrevaylerStore.get().getModelMap(getModelClass());
        }

        return objectMap.get(pk);
    }

    public Collection<M> findAll() {
        List<M> result = new LinkedList<M>();

        result.addAll(getValues());

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
        List<M> result = new LinkedList<M>();

        List<M> values = new LinkedList<M>();

        values.addAll(getValues());
        Collections.sort(values, comp);

        for (int i = from; i < to; i++) {
            if (values.size() > i) {
                result.add(values.get(i));
            }
        }

        return result;
    }

    public Collection<M> filter(Filter<M> filter) {
        List<M> result = new LinkedList<M>();

        for (M m : findAll()) {
            if (filter.include(m)) {
                result.add(m);
            }
        }

        return result;
    }

    protected Collection<? extends M> getValues() {
        return (Collection<? extends M>) PrevaylerStore.get().getModelMap(getModelClass()).values();
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
