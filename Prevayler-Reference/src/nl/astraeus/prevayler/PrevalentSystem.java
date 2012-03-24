package nl.astraeus.prevayler;

import nl.astraeus.prevayler.reflect.ReflectHelper;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * User: rnentjes
 * Date: 8/19/11
 * Time: 8:54 PM
 */
public class PrevalentSystem implements Serializable {

    public static final long serialVersionUID = 1468478256843309473L;

    private Map<Class<? extends PrevaylerModel>, Map<Long, PrevaylerModel>> dataStore = new HashMap<Class<? extends PrevaylerModel>, Map<Long, PrevaylerModel>>(250);

    private Map<String, Map<String, Long>> references = new HashMap<String, Map<String, Long>>();

    protected Map<Class<? extends PrevaylerModel>, Map<Long, PrevaylerModel>> getDataStore() {
        return dataStore;
    }

    @Nonnull
    public Map<Long, PrevaylerModel> getModelMap(Class<? extends PrevaylerModel> cls) {
        Map<Long, PrevaylerModel> result = dataStore.get(cls);
        
        if (result == null) {
            result = new HashMap<Long, PrevaylerModel>();
            
            dataStore.put(cls, result);
        }
        
        return result;
    }

    public <T extends PrevaylerModel> T find(Class<T> cls, long id) {
        T result = (T) getModelMap(cls).get(id);

        return result;
    }

    protected <M extends PrevaylerModel> void store(M objectToStore) {
        getModelMap(objectToStore.getClass()).put(objectToStore.getPrimaryKey(), objectToStore);
    }

    protected void remove(PrevaylerModel objectToRemove) {
        getModelMap(objectToRemove.getClass()).remove(objectToRemove.getPrimaryKey());
    }

    protected void addReferences(PrevaylerModel model) {
        // go through the model, add a reference for every object referenced from this one
        try {
            for (Field field : ReflectHelper.get().getFieldsFromClass(model.getClass())) {
                if (field.getType().equals(PrevaylerReference.class)) {
                    PrevaylerReference ref = (PrevaylerReference) field.get(model);

                    if (!ref.isNull()) {
                        setReference(ref.get(), model.getClass(), model.getPrimaryKey());
                    }
                } else if (field.getType().equals(PrevaylerList.class)) {

                }
            }
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }

    }

    protected void removeReferences(PrevaylerModel model) {
        // get all references to this object, remove them or throw exception


    }

    protected void setReference(PrevaylerModel model, Class<? extends PrevaylerModel> cls, Long id) {
    }

    protected void removeReference(PrevaylerModel model) {
    }

    protected String getReference(PrevaylerModel model) {
        return ReflectHelper.get().getClassName(model.getClass()) + ":" + model.getPrimaryKeyAsString();
    }

    protected Map<String, Long> getReferenceMap(PrevaylerModel model) {
        String ref = getReference(model);
        Map<String, Long> result = references.get(ref);

        if (result == null) {
            result = new HashMap<String, Long>();

            references.put(ref, result);
        }

        return result;
    }

}
