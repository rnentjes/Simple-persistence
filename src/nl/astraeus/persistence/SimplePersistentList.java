package nl.astraeus.persistence;

/**
 * User: rnentjes
 * Date: 12/15/12
 * Time: 11:28 AM
 * <p/>
 * (c) Astraeus B.V.
 */
public class SimplePersistentList<T extends SimplePersistent> extends PersistentList<Long, T> {
    public SimplePersistentList(Class<T> cls) {
        super(cls);
    }
}
