package nl.astraeus.prevayler;

import java.io.Serializable;

/**
 * PrevaylerModel
 * <p/>
 * User: rnentjes
 * Date: 7/16/11
 * Time: 1:56 PM
 */
public abstract class PrevaylerModel implements Serializable, Cloneable {
    public final static long serialVersionUID = 1L;

    private static volatile long nextId;

    static {
        long time = System.currentTimeMillis();
        long nano = System.nanoTime();

        nextId = time*1000000+nano%1000000;
    }

    private long id;

    private boolean _eos_saved;
    private long    _eos_last_update;

    public Long getNextId() {
        synchronized (PrevaylerModel.class) {
            return ++nextId;
        }
    }

    public PrevaylerModel() {
        this.id = getNextId();
    }

    public String getPrimaryKeyAsString() {
        return Long.toString(id);
    }

    public String getKey() {
        return getPrimaryKeyAsString();
    }

    public Long getPrimaryKey() {
        return id;
    }

    public String getGUID() {
        return this.getClass()+"-"+this.getPrimaryKeyAsString();
    }

    public String getDescription() {
        return toString();
    }

    public PrevaylerModel clone() throws CloneNotSupportedException {
        return (PrevaylerModel)super.clone();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = true;

        if (obj == null || obj.getClass() != this.getClass()) {
            result = false;
        } else if (!((PrevaylerModel)obj).getPrimaryKey().equals(this.getPrimaryKey())) {
            result = false;
        }

        return result;
    }
}
