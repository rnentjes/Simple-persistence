package nl.astraeus.persistence;

import nl.astraeus.persistence.reflect.ReflectHelper;

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
    private List<String> order = new LinkedList<String>();
    private int from, max;

    protected static class OrderComparator implements Comparator<SimpleModel> {

        private String [] order;

        public OrderComparator(List<String> order) {
            this.order = order.toArray(new String[order.size()]);
        }

        @Override
        public int compare(SimpleModel o1, SimpleModel o2) {
            int index = 0;
            int result = 0;

            boolean desc;
            String field;

            while(index < order.length && result == 0) {
                field = order[index];
                desc = false;

                if (field.charAt(0) == '-') {
                    desc = true;
                    field = field.substring(1);
                } else if (field.charAt(0) == '+') {
                    field = field.substring(1);
                }

                Object v1 = ReflectHelper.get().getField(o1, field);
                Object v2 = ReflectHelper.get().getField(o2, field);

                if (v1 instanceof Comparable) {
                    result = ((Comparable)v1).compareTo(v2);

                    if (desc) {
                        result = -result;
                    }
                }

                index++;
            }

            if (o1.getId() > o2.getId()) {
                result = -1;
            } else {
                result = 1;
            }

            return result;
        }
    }

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
        order.addAll(Arrays.asList(property));

        return this;
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
