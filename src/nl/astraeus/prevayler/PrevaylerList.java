package nl.astraeus.prevayler;

import java.io.Serializable;
import java.util.*;

/**
 * PrevaylerList
 * <p/>
 * User: rnentjes
 * Date: 7/27/11
 * Time: 2:52 PM
 */
public class PrevaylerList<M extends PrevaylerModel> implements List<M>, Serializable {
    public static final long serialVersionUID = 1L;

    private Class<? extends PrevaylerModel> cls;
    private List<Long> list = new LinkedList<Long>();

    public PrevaylerList(Class<? extends PrevaylerModel> cls) {
        this.cls = cls;
    }

    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public boolean contains(Object o) {
        return list.contains(o);
    }
    
    public List<Long> getIdList() {
        return list;
    }

    public Class<? extends PrevaylerModel> getType() {
        return cls;
    }

    public Iterator<M> iterator() {
        return new Iterator<M>() {
            Iterator<Long> it = list.iterator();
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
        throw new IllegalStateException("Not implemented yet!");
    }

    public <T> T[] toArray(T[] a) {
        throw new IllegalStateException("Not implemented yet!");
    }

    public boolean add(M m) {
        //PrevaylerStore.get().assertIsStored(m);
        return list.add(m.getId());
    }

    public boolean add(long id) {
        //PrevaylerStore.get().assertIsStored(m);
        return list.add(id);
    }

    public boolean remove(Object o) {
        return list.remove(((M)o).getId());
    }

    public boolean containsAll(Collection<?> c) {
        throw new IllegalStateException("Not implemented yet!");
    }

    public boolean addAll(Collection<? extends M> c) {
        boolean result = true;

        for (M m : c) {
            result = result && add(m);
        }

        return result;
    }

    public boolean addAll(int index, Collection<? extends M> c) {
        for (M m : c) {
            add(index, m);
        }

        return true;
    }

    public boolean removeAll(Collection<?> c) {
        for (Object m : c) {
            remove(m);
        }

        return true;
    }

    private List<Long> getLongList(Collection<? extends M> c) {
        List<Long> result = new LinkedList<Long>();

        for (M m : c) {
            result.add(m.getId());
        }

        return result;
    }

    public boolean retainAll(Collection<?> c) {
        return list.retainAll(getLongList((Collection<? extends M>) c));
    }

    public void clear() {
        list.clear();
    }

    public M get(int index) {
        return (M) PrevaylerStore.get().find(cls, list.get(index));
    }

    public M set(int index, M element) {
        PrevaylerStore.get().assertIsStored(element);

        return (M) PrevaylerStore.get().find(cls, list.set(index, element.getId()));
    }

    public void add(int index, M element) {
        PrevaylerStore.get().assertIsStored(element);

        list.add(index, element.getId());
    }

    public M remove(int index) {
        return (M) PrevaylerStore.get().find(cls, list.remove(index));
    }

    public int indexOf(Object o) {
        return list.indexOf(((M)o).getId());
    }

    public int lastIndexOf(Object o) {
        return list.lastIndexOf(((M) o).getId());
    }

    public ListIterator<M> listIterator() {
        throw new IllegalStateException("Not implemented yet!");
    }

    public ListIterator<M> listIterator(int index) {
        throw new IllegalStateException("Not implemented yet!");
    }

    public List<M> subList(int fromIndex, int toIndex) {
        throw new IllegalStateException("Not implemented yet!");
    }
    
    public String toString() {
        StringBuilder result = new StringBuilder();
        
        if (list.isEmpty()) {
            result.append("empty");
        } else {
            for (int i = 0; i < 10 && this.list.size() > i; i++) {
                if (i > 0) {
                    result.append(", ");
                }
                result.append(this.list.get(i));
            }
            if (this.list.size() > 10) {
                result.append(", <" + (this.list.size() - 10) + " more>");
            }
        }
        return result.toString();
    }
}
