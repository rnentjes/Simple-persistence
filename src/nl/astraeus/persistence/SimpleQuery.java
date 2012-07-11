package nl.astraeus.persistence;

import nl.astraeus.persistence.reflect.ReflectHelper;
import org.w3c.dom.UserDataHandler;

import javax.annotation.CheckForNull;
import java.util.*;

/**
 * User: rnentjes
 * Date: 6/26/12
 * Time: 8:05 PM
 */
public class SimpleQuery<M extends SimpleModel> {

    private SimpleDao<M> dao;
    private Map<String, Set<Object>> selections = new HashMap<String, Set<Object>>();
    private String [] order = new String[0];
    private int from, max;

    public SimpleQuery(SimpleDao<M> dao) {
        this.dao = dao;
        this.from = 0;
        this.max = dao.size();
    }

    public SimpleQuery<M> from(int from) {
        this.from = from;

        return this;
    }

    public SimpleQuery<M> max(int max) {
        this.max = max;

        return this;
    }

    private void addSelection(String property, Object value) {
        Set<Object> values = selections.get(property);

        if (values == null) {
            values = new HashSet<Object>();

            selections.put(property, values);
        }

        values.add(value);
    }

    public SimpleQuery<M> where(String property, Object value) {
        addSelection(property, value);

        return this;
    }

    public SimpleQuery<M> order(String ... property) {
        this.order = property;

        return this;
    }

    private static class OrderComparator implements Comparator<SimpleModel> {
        String [] order;

        private OrderComparator(String[] order) {
            this.order = order;
        }

        @Override
        public int compare(SimpleModel o1, SimpleModel o2) {
            int result = 0;
            int index = 0;

            while(result == 0 && index < order.length) {
                result = compareProperties(o1, o2, order[index]);

                index++;
            }

            return result;
        }

        private int compareProperties(SimpleModel o1, SimpleModel o2, String property) {
            Object v1 = ReflectHelper.get().getFieldValue(o1, property);
            Object v2 = ReflectHelper.get().getFieldValue(o2, property);

            if (v1 == null && v2 == null) {
                return 0;
            } else if (v1 == null) {
                return 1;
            } else if (v2 == null) {
                return -1;
            } else if (v1 instanceof Comparable && v2 instanceof Comparable) {
                Comparable c1 = (Comparable)v1;
                Comparable c2 = (Comparable)v2;

                return c1.compareTo(c2);
            } else {
                throw new IllegalStateException("Unable to compare objects of type "+v1.getClass()+" and "+v2.getClass()+". Check your order properties on SimpleQuery "+this);
            }
        }
    }


    public SortedSet<M> getResultSet() {
        // result as set with order comparator
        int fromCounter = 0;
        int nrResults = 0;


        SortedSet<M> result = new TreeSet<M>(new OrderComparator(order));

        for (M m : dao.findAll()) {
            boolean match = isMatch(m);

            if (match) {
                if (fromCounter >= from) {
                    result.add(m);
                    nrResults++;
                }
                fromCounter++;
            }

            if (nrResults == max) {
                break;
            }
        }

        return result;
    }

    @CheckForNull
    public M getSingleResult() {
        M result = null;
        Set<M> resultList = getResultSet();

        if (resultList.size() == 1) {
            result = resultList.iterator().next();
        }

        return result;
    }

    public boolean isMatch(M m) {
        boolean result = true;
        Set<String> properties = selections.keySet();

        for (String property : properties) {
            Object om = ReflectHelper.get().getFieldValue(m, property);

            if (om != null) {
                for (Object matches : selections.get(property)) {
                    if (!om.equals(matches)) {
                        result = false;
                        break;
                    }
                }
            }
        }

        return result;
    }
}
