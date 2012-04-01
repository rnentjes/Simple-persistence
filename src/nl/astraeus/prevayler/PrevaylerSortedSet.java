package nl.astraeus.prevayler;

import java.io.Serializable;
import java.util.*;

/**
 * User: rnentjes
 * Date: 4/1/12
 * Time: 10:36 AM
 */
public class PrevaylerSortedSet<M extends PrevaylerModel> implements SortedSet<M>, Serializable {
    public static final long serialVersionUID = 1L;

    private Class<? extends PrevaylerModel> cls;
    private SortedSet<Long> set = new TreeSet<Long>();
    private Comparator<? super M> comparator = new Comparator<M>() {
        public int compare(M o1, M o2) {
            if (o1 != null && o1.getId() > o2.getId()) {
                return -1;
            } else {
                return 1;
            }
        }
    };

    private static class LongModelComparator<M extends PrevaylerModel> implements Comparator<Long>, Serializable {

        private Class<? extends PrevaylerModel> cls;
        private Comparator<M> comparator;

        private LongModelComparator(Class<? extends PrevaylerModel> cls, Comparator<M> comparator) {
            this.cls = cls;
            this.comparator = comparator;
        }

        public int compare(Long o1, Long o2) {
            M m1 = (M)PrevaylerStore.get().find(cls, o1);
            M m2 = (M)PrevaylerStore.get().find(cls, o2);

            return comparator.compare(m1, m2);
        }
    }

    public PrevaylerSortedSet(Class<? extends PrevaylerModel> cls) {
        this.cls = cls;
    }

    public PrevaylerSortedSet(final Class<? extends PrevaylerModel> cls, final Comparator<M> comp) {
        if (!(comp instanceof Serializable)) {
            throw new IllegalStateException("Given comparator is not Serializable.");
        }
        this.cls = cls;
        this.comparator = comp;
        this.set = new TreeSet<Long>(new LongModelComparator(cls, comp));
    }

    public int size() {
        return set.size();
    }

    public boolean isEmpty() {
        return set.isEmpty();
    }

    public boolean contains(Object m) {
        boolean result = false;

        if (m instanceof PrevaylerModel) {
           result = set.contains(((PrevaylerModel) m).getId());
        }

        return result;
    }

    public Set<Long> getIdSet() {
        return set;
    }

    public Class<? extends PrevaylerModel> getType() {
        return cls;
    }

    public Iterator<M> iterator() {
        return new Iterator<M>() {
            Iterator<Long> it = set.iterator();
            M next = null;

            public boolean hasNext() {
                while (next == null && it.hasNext()) {
                    long id = it.next();

                    next = (M) PrevaylerStore.get().getModelMap(cls).get(id);
                }

                return (next != null);
            }

            public M next() {
                M result = next;

                next = null;

                while (it.hasNext() && next == null) {
                    next = (M) PrevaylerStore.get().find(cls, it.next());
                }

                return result;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public Object[] toArray() {
        Object [] result = new Object[set.size()];

        int index = 0;
        for (Long id : set) {
            result[index++] = PrevaylerStore.get().find(cls, id);
        }

        return result;
    }

    public <T> T[] toArray(T[] a) {
        return (T[])toArray();
    }

    public boolean add(M m) {
        return set.add(m.getId());
    }

    public boolean add(Long id) {
        return set.add(id);
    }

    public boolean remove(Object m) {
        boolean result = false;

        if (m instanceof PrevaylerModel) {
            result = set.remove(((PrevaylerModel)m).getId());
        }

        return result;
    }

    public boolean containsAll(Collection<?> c) {
        boolean result = true;

        Collection<Long> idset = new HashSet<Long>();

        for (Object o : c) {
            if (o instanceof PrevaylerModel) {
                idset.add(((PrevaylerModel)o).getId());
            } else {
                result = false;
                break;
            }
        }

        if (result) {
            for (Long id : set) {
                result = result && idset.contains(id);
            }
        }

        return result;
    }

    public boolean addAll(Collection<? extends M> c) {
        boolean result = true;

        for (M m : c) {
            result = result && add(m);
        }

        return result;
    }

    public boolean retainAll(Collection<?> c) {
        boolean result = false;

        Iterator it = set.iterator();

        while(it.hasNext()) {
            M m = (M)it.next();
            if (!c.contains(m)) {
                iterator().remove();
                result = true;
            }
        }

        return result;
    }

    public boolean removeAll(Collection<?> c) {
        boolean result = true;

        for (Object m : c) {
            result = result && remove(m);
        }

        return result;
    }

    public void clear() {
        set.clear();
    }

    public Comparator<? super M> comparator() {
        return comparator;
    }

    public SortedSet<M> subSet(M fromElement, M toElement) {
        SortedSet<M> result = new TreeSet<M>(comparator);

        Set<Long> subSet = set.subSet(fromElement.getId(), toElement.getId());

        for (Long id : subSet) {
            result.add((M)PrevaylerStore.get().find(cls, id));
        }

        return result;
    }

    public SortedSet<M> headSet(M toElement) {
        SortedSet<M> result = new TreeSet<M>(comparator);

        Set<Long> subSet = set.headSet(toElement.getId());

        for (Long id : subSet) {
            result.add((M)PrevaylerStore.get().find(cls, id));
        }

        return result;
    }

    public SortedSet<M> tailSet(M fromElement) {
        SortedSet<M> result = new TreeSet<M>(comparator);

        Set<Long> subSet = set.tailSet(fromElement.getId());

        for (Long id : subSet) {
            result.add((M)PrevaylerStore.get().find(cls, id));
        }

        return result;
    }

    public M first() {
        M result = null;
        Long id = set.first();

        if (id != null) {
            result = (M)PrevaylerStore.get().find(cls, id);
        }

        return result;
    }

    public M last() {
        M result = null;
        Long id = set.last();

        if (id != null) {
            result = (M)PrevaylerStore.get().find(cls, id);
        }

        return result;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();

        if (set == null || set.isEmpty()) {
            result.append("empty");
        } else {
            int i = 0;
            Iterator<Long> it = set.iterator();

            while (it.hasNext() && i++ < 10) {
                if (i > 0) {
                    result.append(", ");
                }
                result.append(it.next());
            }

            if (this.set.size() > 10) {
                result.append(", <" + (this.set.size() - 10) + " more>");
            }
        }
        return result.toString();
    }
}
