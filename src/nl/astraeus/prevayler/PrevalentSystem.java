package nl.astraeus.prevayler;

import nl.astraeus.prevayler.reflect.ReflectHelper;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * User: rnentjes
 * Date: 8/19/11
 * Time: 8:54 PM
 */
public class PrevalentSystem implements Serializable {

    public static final long serialVersionUID = 1468478256843309473L;

    private Map<Class<? extends PrevaylerModel>, Map<Long, PrevaylerModel>> dataStore = new HashMap<Class<? extends PrevaylerModel>, Map<Long, PrevaylerModel>>(250);

    //private Map<Class<? extends PrevaylerModel>, List<PrevaylerListener<?>>> listeners;

    //private Map<Class<? extends PrevaylerModel>, List<PrevaylerIndex<?>>> listeners;

    /*
     * Class.getName()+id, class, list<id>
     */
    private Map<String, Map<Class<? extends PrevaylerModel>, Set<Long>>> references = new HashMap<String, Map<Class<? extends PrevaylerModel>, Set<Long>>>();

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

    /** Functions called from within Transaction to update data model */
    protected <M extends PrevaylerModel> void store(M objectToStore) {
        System.out.println("Storing: "+objectToStore.getGUID());
        getModelMap(objectToStore.getClass()).put(objectToStore.getId(), objectToStore);
    }

    /** Functions called from within Transaction to update data model */
    protected void remove(PrevaylerModel objectToRemove) {
        System.out.println("Removing: "+objectToRemove.getGUID());
        getModelMap(objectToRemove.getClass()).remove(objectToRemove.getId());
    }

    protected void addReferences(PrevaylerModel model) {
        // go through the model, add a reference for every object referenced from this one
        try {
            for (Field field : ReflectHelper.get().getFieldsFromClass(model.getClass())) {
                if (field.getType().equals(PrevaylerReference.class)) {
                    PrevaylerReference ref = (PrevaylerReference) field.get(model);

                    if (!ref.isNull()) {
                        setReference(ref.get(), model.getClass(), model.getId());
                    }
                } else if (field.getType().equals(PrevaylerList.class)) {

                }
            }
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }

    }

    protected void setReference(PrevaylerModel model, Class<? extends PrevaylerModel> cls, Long id) {
        Map<Class<? extends PrevaylerModel>, Set<Long>> map = getReferenceMap(model);

        Set<Long> set = map.get(cls);

        if (set ==null) {
            set = new HashSet<Long>();

            map.put(cls, set);
        }

        set.add(id);
    }

    protected void setReference(PrevaylerModel model, PrevaylerModel other) {
        setReference(model, other.getClass(), other.getId());
   }

    protected void removeReference(PrevaylerModel model) {
        references.remove(model.getGUID());
    }

    protected String getReference(PrevaylerModel model) {
        return ReflectHelper.get().getClassName(model.getClass()) + ":" + model.getIdAsString();
    }

    protected Map<Class<? extends PrevaylerModel>, Set<Long>> getReferenceMap(PrevaylerModel model) {
        String ref = getReference(model);
        Map<Class<? extends PrevaylerModel>, Set<Long>> result = references.get(ref);

        if (result == null) {
            result = new HashMap<Class<? extends PrevaylerModel>, Set<Long>>();

            references.put(ref, result);
        }

        return result;
    }

}
