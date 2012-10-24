package nl.astraeus.persistence;

import org.prevayler.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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

    public boolean hasChanges() {
        return !actions.isEmpty();
    }
}
