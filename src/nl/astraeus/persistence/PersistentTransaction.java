package nl.astraeus.persistence;

import nl.astraeus.persistence.reflect.ReflectHelper;
import org.prevayler.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * StoreModelTransaction
 * <p/>
 * User: rnentjes
 * Date: 7/20/11
 * Time: 12:58 PM
 */
public final class PersistentTransaction implements Serializable, Transaction {
    private final static Logger logger = LoggerFactory.getLogger(PersistentTransaction.class);

	private static final long serialVersionUID = 1L;

    private static Set<Class<? extends Persistent>> validatedClasses = new CopyOnWriteArraySet<>();

    static class Action implements Serializable {
        private static final long serialVersionUID = 1L;

        public boolean remove = false;
        public Persistent model;

        private Action(boolean remove, Persistent model) {
            this.remove = remove;
            this.model = model;
        }

        public boolean isClass(Class<? extends Persistent> cls) {
            return cls.equals(model.getClass());
        }
    }

    private List<Action> actions = new LinkedList<Action>();

    PersistentTransaction() {}

    void store(Persistent... models) {
        for (Persistent model : models) {
            Action action = new Action(false, model);

            actions.add(action);
        }
    }
    
    void remove(Persistent... models) {
        for (Persistent model : models) {
            Action action = new Action(true, model);

            actions.add(action);
        }
    }

    Collection<Action> getActions() {
        return actions;
    }

	public void executeOn(Object prevalentSystem, Date ignored) {
        PersistentObjectStore os = (PersistentObjectStore)prevalentSystem;
        boolean castFailure= false;

        try { // send transaction to other nodes
            //SimpleNodeManager.get().cast(actions);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            castFailure = true;
        }

        // check @Version fields
        for (Action action : actions) {
            if (!validatedClasses.contains(action.model.getClass())) {
                validateClass(action.model);
            }

            Persistent current = ((PersistentObjectStore) prevalentSystem).find(action.model.getClass(), action.model.getId());

            if (ReflectHelper.get().getVersion(current) != null) {
                if (!ReflectHelper.get().getVersion(current).equals(ReflectHelper.get().getVersion(action.model))) {
                    throw new OptimisticLockingException("Persistent object "+action.model.getClass()+":"+String.valueOf(action.model.getId())+" has been changed!");
                } else {
                    ReflectHelper.get().updateVersion(action.model);
                }
            }
        }

        for (Action action : actions) {
//            if (castFailure || SimpleNodeManager.get().getCurrent() == null || SimpleNodeManager.get().getCurrent().matched(action.model.getId())) {
                if (action.remove) {
                    os.removeIndex(action.model);
                    os.remove(action.model);
                } else {
                    os.updateIndex(action.model);
                    os.store(action.model);
                }
  //          }
        }
	}

    private void validateClass(Persistent model) {
        List<Field> fields = ReflectHelper.get().getFieldsFromClass(model.getClass());

        for (Field field : fields) {
            if (Persistent.class.isAssignableFrom(field.getType()) && field.getAnnotation(Serialized.class) == null) {
                logger.error("Field "+field.getName()+" in class "+model.getClass().getName()+" is Persistent but does not have the Serialized annotation. Either Use PersistentReference<? extends Persistent> or mark the field Serialized!");
                throw new IllegalStateException("Field "+field.getName()+" in class "+model.getClass().getName()+" is Persistent but does not have the Serialized annotation. Either Use PersistentReference<? extends Persistent> or mark the field Serialized!");
            }
        }

        validatedClasses.add(model.getClass());
    }

    public boolean hasChanges() {
        return !actions.isEmpty();
    }
}
