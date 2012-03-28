package nl.astraeus.prevayler;

import java.io.Serializable;

/**
 * PrevaylerReference
 * <p/>
 * User: rnentjes
 * Date: 7/27/11
 * Time: 1:02 PM
 */
public class PrevaylerReference<M extends PrevaylerModel> implements Serializable {
    public static final long serialVersionUID = 1L;
    
    private long id;
    private Class<? extends PrevaylerModel> cls;

    public PrevaylerReference(Class cls) {
        id = -1;
        this.cls = cls;
    }

    public PrevaylerReference(M model) {
        if (model != null) {
            set(model);
        }
    }

    public PrevaylerReference(Class cls, long id) {
        this.cls = cls;
        this.id = id;
    }

    public Class getType() {
        return cls;
    }

    public long getId() {
        return id;
    }

    public M get() {
        if (cls != null && id > 0) {
           return (M) PrevaylerStore.get().find(cls, id);
        } else {
            return null;
        }
    }

    public void set(M model) {
        this.cls = model.getClass();
        this.id = model.getId();

        //PrevaylerStore.get().assertIsStored(model);
    }

    public boolean isNull() {
        return cls == null || id == 0;
    }
    
    public String toString() {
        return String.valueOf(get());
    }

}
