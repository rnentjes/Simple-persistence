package nl.astraeus.persistence;

import org.prevayler.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

/**
 * StoreModelTransaction
 * <p/>
 * User: rnentjes
 * Date: 7/20/11
 * Time: 12:58 PM
 */
public final class SimpleTransaction implements Serializable, Transaction {
    private final static Logger logger = LoggerFactory.getLogger(SimpleTransaction.class);

	private static final long serialVersionUID = 1L;

    static class Action implements Serializable {
        private static final long serialVersionUID = 1L;

        public boolean remove = false;
        public SimpleModel model;

        private Action(boolean remove, SimpleModel model) {
            this.remove = remove;
            this.model = model;
        }

        public boolean isClass(Class<? extends SimpleModel> cls) {
            return cls.equals(model.getClass());
        }
    }

    private List<Action> actions = new LinkedList<Action>();

    SimpleTransaction() {}

    void store(SimpleModel... models) {
        for (SimpleModel model : models) {
            SimpleStore.get().setLastUpdateField(model);
            SimpleStore.get().setSavedField(model, true);

            Action action = new Action(false, model);

            actions.add(action);
        }
    }
    
    void remove(SimpleModel... models) {
        for (SimpleModel model : models) {
            SimpleStore.get().setSavedField(model, false);

            Action action = new Action(true, model);

            actions.add(action);
        }
    }

    Collection<Action> getActions() {
        return actions;
    }

	public void executeOn(Object prevalentSystem, Date ignored) {
        PrevalentSystem ps = (PrevalentSystem)prevalentSystem;
        boolean castFailure= false;

        try { // send transaction to other nodes
            SimpleNodeManager.get().cast(actions);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            castFailure = true;
        }
        
        for (Action action : actions) {
            if (castFailure || SimpleNodeManager.get().getCurrent() == null || SimpleNodeManager.get().getCurrent().matched(action.model.getId())) {
                if (action.remove) {
                    ps.removeIndex(action.model);
                    ps.remove(action.model);
                } else {
                    ps.updateIndex(action.model);
                    ps.store(action.model);
                }
            }
        }
	}

    public boolean hasChanges() {
        return !actions.isEmpty();
    }
}
