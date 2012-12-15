package nl.astraeus.persistence;

/**
 * User: rnentjes
 * Date: 12/15/12
 * Time: 11:26 AM
 * <p/>
 * (c) Astraeus B.V.
 */
public class SimplePersistentReference<T extends SimplePersistent> extends PersistentReference<Long, T> {
    public SimplePersistentReference(Class<T> cls) {
        super(cls);
    }

    public SimplePersistentReference(T model) {
        super(model);
    }

    public SimplePersistentReference(Class<T> cls, Long id) {
        super(cls, id);
    }
}
