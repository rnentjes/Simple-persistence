package nl.astraeus.prevayler;

import nl.astraeus.prevayler.reflect.ReflectHelper;

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

    private boolean _prevayler_saved                = false;
    private long    _prevayler_last_update          = System.currentTimeMillis();
    private boolean _prevayler_selected_for_update  = false;

    public Long getNextId() {
        synchronized (PrevaylerModel.class) {
            return ++nextId;
        }
    }

    public PrevaylerModel() {
        this.id = getNextId();
    }

    public String getIdAsString() {
        return Long.toString(id);
    }

    public String getKey() {
        return getIdAsString();
    }

    public Long getId() {
        return id;
    }

    public String getGUID() {
        return this.getClass()+"-"+this.getIdAsString();
    }

    public String getDescription() {
        return toString();
    }

    public PrevaylerModel clone() throws CloneNotSupportedException {
        PrevaylerModel result = (PrevaylerModel)super.clone();

        // clone refs and lists
        ReflectHelper.get().copyPrevaylerReferenceAndListProperties(this, result);

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = true;

        if (obj == null || obj.getClass() != this.getClass()) {
            result = false;
        } else if (!((PrevaylerModel)obj).getId().equals(this.getId())) {
            result = false;
        }

        return result;
    }
}
