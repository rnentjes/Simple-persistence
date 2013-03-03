package nl.astraeus.persistence;

import javax.annotation.CheckForNull;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * User: rnentjes
 * Date: 10/24/12
 * Time: 1:49 PM
 */
public class PersistentDao<K, M extends Persistent<K>> {

    public PersistentQuery<K, M> createQuery() {
        return new PersistentQuery<K, M>(this);
    }

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

        for (Type type : pt.getActualTypeArguments()) {
            if (Persistent.class.isAssignableFrom((Class)type)) {
                result = (Class<M>)type;
            }
        }

        return result;
    }

    @CheckForNull
    public M find(K key) {
        return PersistentManager.get().find(getModelClass(), key);
    }

    public Set<K> keySet() {
        return PersistentManager.get().getModelMap(getModelClass()).keySet();
    }

    public Collection<M> findAll() {
        Collection<M> result;

        if (PersistentManager.get().isSafemode()) {
            result = getValues();
        } else {
            result = getModelValues();
        }

        return result;
    }

    public Collection<M> findAll(Comparator<M> comp) {
        Collection<M> result = new TreeSet<M>(comp);

        if (PersistentManager.get().isSafemode()) {
            result.addAll(getValues());
        } else {
            result.addAll(getModelValues());
        }

        return result;
    }

    public Collection<M> find(int from, int to) {
        return find(new Comparator<M>() {
            public int compare(M o1, M o2) {
                if (o1 instanceof Comparable) {
                    return ((Comparable)o1).compareTo(o2);
                } else if (o1.getId() instanceof Comparable) {
                    return ((Comparable)o1.getId()).compareTo(o2.getId());
                } else {
                    return 0;
                }
            }
        }, from, to);
    }

    public Collection<M> find(Comparator<M> comp, int from, int to) {
        Class<M> cls = getModelClass();
        List<M> result = new LinkedList<M>();

        List<M> values = new LinkedList<M>();

        values.addAll(getModelValues());
        Collections.sort(values, comp);

        for (int i = from; i < to; i++) {
            if (values.size() > i) {
                if (PersistentManager.get().isSafemode()) {
                    try {
                        result.add(cls.cast(values.get(i).clone()));
                    } catch (CloneNotSupportedException e) {
                        throw new IllegalStateException(e);
                    }
                } else {
                    result.add(values.get(i));
                }
            }
        }

        return result;
    }

    public Collection<M> filter(Filter<M> filter) {
        return filter(getModelValues(), filter);
    }

    public Collection<M> filter(Collection<M> filterSet, Filter<M> filter) {
        Class<M> cls = getModelClass();
        List<M> result = new LinkedList<M>();

        for (M m : filterSet) {
            if (filter.include(m)) {
                if (PersistentManager.get().isSafemode()) {
                    try {
                        result.add(cls.cast(m.clone()));
                    } catch(CloneNotSupportedException e) {
                        throw new IllegalStateException(e);
                    }
                } else {
                    result.add(m);
                }
            }
        }

        return result;
    }

    /** returnes a cloned set of all values */
    private Collection<M> getValues() {
        Class<M> cls = getModelClass();

        Collection<M> result = new TreeSet<M>();

        for (M m : PersistentManager.get().getModelMap(cls).values()) {
            try {
                result.add(cls.cast(m.clone()));
            } catch (CloneNotSupportedException e) {
                throw new IllegalStateException(e);
            }
        }

        return result;

    }

    /** The returned model values are not cloned yet! (safemode) */
    private Collection<M> getModelValues() {
        return PersistentManager.get().getModelMap(getModelClass()).values();
    }

    public void store(final M model) {
        if (!PersistentManager.transactionActive()) {
            if (PersistentManager.get().isAutocommit()) {
                new Transaction() {

                    @Override
                    public void execute() {
                        PersistentManager.get().getTransaction().store(model);
                    }
                };
            } else {
                throw new IllegalStateException("No transaction found and not in autocommit mode.");
            }
        } else {
            PersistentManager.get().getTransaction().store(model);
        }
    }

    public <A extends Persistent> void store(final A ... model) {
        if (!PersistentManager.transactionActive()) {
            if (PersistentManager.get().isAutocommit()) {
                new Transaction() {

                    @Override
                    public void execute() {
                        PersistentManager.get().getTransaction().store(model);
                    }
                };
            } else {
                throw new IllegalStateException("No transaction found and not in autocommit mode.");
            }
        } else {
            // todo: warn, no need to use this function in a transaction
            PersistentManager.get().getTransaction().store(model);
        }
    }

    public void remove(K key) {
        M model = find(key);

        remove(model);
    }

    public void remove(final M model) {
        if (!PersistentManager.transactionActive()) {
            if (PersistentManager.get().isAutocommit()) {
                new Transaction() {

                    @Override
                    public void execute() {
                        PersistentManager.get().getTransaction().remove(model);
                    }
                };
            } else {
                throw new IllegalStateException("No transaction found and not in autocommit mode.");
            }
        } else {
            PersistentManager.get().getTransaction().remove(model);
        }
    }

    public int size() {
        return PersistentManager.get().getModelMap(getModelClass()).size();
    }

    public void createIndex(String property) {
        if (PersistentManager.get().getIndex(getModelClass(), property) == null) {
            PersistentManager.get().createIndex(getModelClass(), property);
        }
    }

    public void removeIndex(String property) {
        if (PersistentManager.get().getIndex(getModelClass(), property) != null) {
            PersistentManager.get().removeIndex(getModelClass(), property);
        }
    }

}
