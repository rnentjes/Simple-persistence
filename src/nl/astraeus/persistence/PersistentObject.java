package nl.astraeus.persistence;

import nl.astraeus.persistence.reflect.ReflectHelper;

/**
 * User: rnentjes
 * Date: 10/24/12
 * Time: 4:16 PM
 */
public abstract class PersistentObject<K> implements Persistent<K>, Comparable {

    public PersistentObject<K> clone() throws CloneNotSupportedException {
        PersistentObject<K> result = (PersistentObject<K>)super.clone();

        // clone refs and lists
        ReflectHelper.get().copyPrevaylerReferenceAndListProperties(this, result);

        return result;
    }

    @Override
    public int compareTo(Object o) {
        K id = getId();

        if (id instanceof Comparable && o instanceof Persistent) {
            Persistent other = (Persistent)o;

            return ((Comparable) id).compareTo(((Persistent) o).getId());
        } else {
            throw new IllegalStateException("Can't compare non Comparable keys, implement compareTo yourself or use a Comparable for your key.");
        }
    }

}
