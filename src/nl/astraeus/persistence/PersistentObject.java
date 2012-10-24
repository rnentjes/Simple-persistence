package nl.astraeus.persistence;

/**
 * User: rnentjes
 * Date: 10/24/12
 * Time: 4:16 PM
 */
public abstract class PersistentObject<K> implements Persistent<K> {

    @Override
    public Persistent<K> clone() throws CloneNotSupportedException {
        return (Persistent<K>)super.clone();
    }

    @Override
    public int compareTo(Object o) {
        K id = getId();

        if (id instanceof Comparable && o instanceof Persistent) {
            Persistent other = (Persistent)o;

            return ((Comparable) id).compareTo(((Persistent) o).getId());
        } else {
            throw new IllegalStateException("Can't compare non Comparable keys, implement compareTo yourself or user a Comparable for your key.");
        }
    }

}
