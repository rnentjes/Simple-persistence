package nl.astraeus.persistence;

/**
 * User: rnentjes
 * Date: 11/25/12
 * Time: 10:01 PM
 */
public class SimplePersistent extends PersistentObject<Long> {

    private static volatile long nextId;

    static {
        long time = System.currentTimeMillis();

        nextId = time*100000;
    }

    public Long getNextId() {
        synchronized (SimplePersistent.class) {
            return ++nextId;
        }
    }

    private long id;

    public SimplePersistent() {
        this.id = getNextId();
    }

    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = true;

        if (obj == null || obj.getClass() != this.getClass()) {
            result = false;
        } else if (!((SimplePersistent)obj).getId().equals(this.getId())) {
            result = false;
        }

        return result;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
