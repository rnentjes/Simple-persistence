package nl.astraeus.persistence;

import java.io.Serializable;

/**
 * SimpleReference
 * <p/>
 * User: rnentjes
 * Date: 7/27/11
 * Time: 1:02 PM
 */
public class SimpleReference<M extends SimpleModel> implements Serializable {
    public static final long serialVersionUID = 1L;

    private transient M incoming;
    private long id;
    private Class<? extends SimpleModel> cls;

    public SimpleReference(Class cls) {
        id = -1;
        this.cls = cls;
    }

    public SimpleReference(M model) {
        if (model != null) {
            set(model);
        }
    }

    public SimpleReference(Class cls, long id) {
        this.cls = cls;
        this.id = id;
    }

    public M getIncoming() {
        return incoming;
    }

    public void clearIncoming() {
        incoming = null;
    }

    public Class getType() {
        return cls;
    }

    public long getId() {
        return id;
    }

    public M get() {
        M result = incoming;

        if (result == null && cls != null && id > 0) {
           result = (M) SimpleStore.get().find(cls, id);
        }

        return result;
    }

    public void set(M model) {
        this.incoming = model;

        if (model != null) {
            this.cls = model.getClass();
            this.id = model.getId();
        } else {
            this.id = -1;
        }
    }

    public boolean isNull() {
        return (cls == null || id == 0);
    }
    
    public String toString() {
        String clsName = "null";

        if (cls != null) {
            clsName = cls.getName();
        }

        return clsName+":"+id;
    }

}
