package nl.astraeus.persistence;

import nl.astraeus.persistence.reflect.ReflectHelper;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

/**
 * User: rnentjes
 * Date: 8/19/11
 * Time: 8:54 PM
 */
public class PrevalentSystem implements Serializable {

    public static final long serialVersionUID = 1468478256843309473L;

    /* Alternative approach
      private Map<Class<? extends SimpleModel>, Set<Integer>> freeIds = new HashMap<Class<? extends SimpleModel>, Set<Integer>>();
      private Map<Class<? extends SimpleModel>, Integer> nextId = new HashMap<Class<? extends SimpleModel>, Integer>();
      private Map<Class<? extends SimpleModel>, SimpleModel []> dataStore = new HashMap<Class<? extends SimpleModel>, SimpleModel[]>();
    */

    private Map<Class<? extends SimpleModel>, Map<Long, SimpleModel>> dataStore = new HashMap<Class<? extends SimpleModel>, Map<Long, SimpleModel>>(250);

    // index definitions
    // index class + property
    // index type - tree, hash ???
    private Map<Class<? extends SimpleModel>, Map<String, SimpleIndex>> indexDefinitions = new HashMap<Class<? extends SimpleModel>, Map<String, SimpleIndex>>();

    private Map<Class<? extends SimpleModel>, Map<String, Object>> indexes = new HashMap<Class<? extends SimpleModel>, Map<String, Object>>();

    //private Map<Class<? extends SimpleModel>, SimpleList<SimpleListener<?>>> listeners;

    //private Map<Class<? extends SimpleModel>, SimpleList<SimpleIndex<?>>> listeners;

    /*
     * Class.getName()+id, class, list<id>
     */
    private Map<String, Map<Class<? extends SimpleModel>, Set<Long>>> references = new HashMap<String, Map<Class<? extends SimpleModel>, Set<Long>>>();

    protected Map<Class<? extends SimpleModel>, Map<Long, SimpleModel>> getDataStore() {
        return dataStore;
    }

    @Nonnull
    public Map<Long, SimpleModel> getModelMap(Class<? extends SimpleModel> cls) {
        Map<Long, SimpleModel> result = dataStore.get(cls);
        
        if (result == null) {
            result = new HashMap<Long, SimpleModel>();
            
            dataStore.put(cls, result);
        }
        
        return result;
    }

    public <T extends SimpleModel> T find(Class<T> cls, long id) {
        T result = (T) getModelMap(cls).get(id);

        return result;
    }

    /** Functions called from within Transaction to update data model */
    protected <M extends SimpleModel> void store(M objectToStore) {
        System.out.println("Storing: "+objectToStore.getGUID());

        // check for references, work with proxies?

        saveConversation(objectToStore);
        getModelMap(objectToStore.getClass()).put(objectToStore.getId(), objectToStore);
    }

    /** Functions called from within Transaction to update data model */
    protected void remove(SimpleModel objectToRemove) {
        System.out.println("Removing: "+objectToRemove.getGUID());
        getModelMap(objectToRemove.getClass()).remove(objectToRemove.getId());
    }

    // check any SimpleList and SimpleReferences and if they have values to save
    protected void saveConversation(SimpleModel model) {
        try {
            for (Field field : ReflectHelper.get().getFieldsFromClass(model.getClass())) {
                if (field.getType().equals(SimpleReference.class)) {
                    SimpleReference ref = (SimpleReference) field.get(model);

                    if (ref != null) {
                        SimpleModel m = ref.getIncoming();

                        if (m != null) {
                            store(m);
                        }

                        ref.clearIncoming();
                    }
                } else if (field.getType().equals(SimpleList.class)) {
                    SimpleList list = (SimpleList) field.get(model);

                    if (list != null) {
                        Map incoming = list.getIncoming();

                        if (incoming != null) {
                            for (SimpleModel m : (Collection<SimpleModel>)incoming.values()) {
                                store(m);
                            }
                        }

                        list.clearIncoming();
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }

    }

    protected void addReferences(SimpleModel model) {
        // go through the model, add a reference for every object referenced from this one
        try {
            for (Field field : ReflectHelper.get().getFieldsFromClass(model.getClass())) {
                if (field.getType().equals(SimpleReference.class)) {
                    SimpleReference ref = (SimpleReference) field.get(model);

                    if (!ref.isNull()) {
                        setReference(ref.get(), model.getClass(), model.getId());
                    }
                } else if (field.getType().equals(SimpleList.class)) {

                }
            }
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }

    }

    protected void setReference(SimpleModel model, Class<? extends SimpleModel> cls, Long id) {
        Map<Class<? extends SimpleModel>, Set<Long>> map = getReferenceMap(model);

        Set<Long> set = map.get(cls);

        if (set ==null) {
            set = new HashSet<Long>();

            map.put(cls, set);
        }

        set.add(id);
    }

    protected void setReference(SimpleModel model, SimpleModel other) {
        setReference(model, other.getClass(), other.getId());
   }

    protected void removeReference(SimpleModel model) {
        references.remove(model.getGUID());
    }

    protected String getReference(SimpleModel model) {
        return ReflectHelper.get().getClassName(model.getClass()) + ":" + model.getIdAsString();
    }

    protected Map<Class<? extends SimpleModel>, Set<Long>> getReferenceMap(SimpleModel model) {
        String ref = getReference(model);
        Map<Class<? extends SimpleModel>, Set<Long>> result = references.get(ref);

        if (result == null) {
            result = new HashMap<Class<? extends SimpleModel>, Set<Long>>();

            references.put(ref, result);
        }

        return result;
    }

}
