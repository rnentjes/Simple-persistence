package nl.astraeus.prevayler;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * User: rnentjes
 * Date: 7/24/11
 * Time: 7:24 PM
 */
public class PrevaylerStore {
    private final static PrevaylerStore instance = new PrevaylerStore();
    
    private ThreadLocal<PrevaylerTransaction> transactions = new ThreadLocal<PrevaylerTransaction>();

    public static PrevaylerStore get() {
        return instance;
    }

    private Prevayler prevayler;
    private boolean autocommit = false;

    private Field _prevayler_saved = null;
    private Field _prevayler_last_update = null;

    public PrevaylerStore() {
        System.out.println("Used  memory: "+((Runtime.getRuntime().totalMemory() / (1024*1024))-(Runtime.getRuntime().freeMemory() / (1024*1024))));
        System.out.println("Free  memory: "+(Runtime.getRuntime().freeMemory() / (1024*1024)));
        System.out.println("Total memory: "+(Runtime.getRuntime().totalMemory() / (1024*1024)));
        System.out.println("Max   memory: "+(Runtime.getRuntime().maxMemory() / (1024*1024)));

        long nano = System.nanoTime();
        
        try {
            PrevaylerFactory factory = new PrevaylerFactory();

            factory.configureJournalFileAgeThreshold(TimeUnit.MINUTES.toMillis(1));
            factory.configureJournalFileSizeThreshold(1000000);
            factory.configurePrevalentSystem(new PrevalentSystem());
            factory.configurePrevalenceDirectory("prevayler");

            prevayler = factory.create();

            System.gc();

            System.out.println("Loading took: " + formatNano(System.nanoTime() - nano) + " ms");
            System.out.println("Used  memory: "+((Runtime.getRuntime().totalMemory() / (1024*1024))-(Runtime.getRuntime().freeMemory() / (1024*1024))));
            System.out.println("Free  memory: "+(Runtime.getRuntime().freeMemory() / (1024*1024)));
            System.out.println("Total memory: "+(Runtime.getRuntime().totalMemory() / (1024*1024)));
            System.out.println("Max   memory: "+(Runtime.getRuntime().maxMemory() / (1024*1024)));
            System.out.println();
        } catch (IOException e) {
            throw new IllegalStateException("Can't start Prevayler!", e);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Can't start Prevayler!", e);
        }
    }
    
    public PrevaylerTransaction getTransaction() {
        return transactions.get();
    }

    public void begin() {
        if (getTransaction() != null) {
            throw new IllegalStateException("Transaction already in progress!");
        }

        transactions.set(new PrevaylerTransaction());
    }
    
    public void commit() {
        if (getTransaction() == null) {
            throw new IllegalStateException("No transaction to commit!");
        }

        prevayler.execute(getTransaction());
        transactions.remove();
    }
    
    public void rollback() {
        if (getTransaction() == null) {
            throw new IllegalStateException("No transaction to rollback!");
        }

        transactions.remove();
    }

    private String formatNano(long l) {
        NumberFormat format = new DecimalFormat("###,##0.000");

        return format.format((double) l / 1000000.0);
    }

    public void snapshot() {
        try {
            prevayler.takeSnapshot();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    protected Map<Long, PrevaylerModel> getModelMap(Class<? extends PrevaylerModel> cls) {
        PrevalentSystem ps = (PrevalentSystem)prevayler.prevalentSystem();

        Map<Long, PrevaylerModel> result = ps.getModelMap(cls);

        if (getTransaction() != null) {
            for (PrevaylerModel m : getTransaction().getStored()) {
                result.put(m.getId(), m);
            }

            for (PrevaylerModel m : getTransaction().getRemoved()) {
                result.remove(m.getId());
            }
        }

        return result;
    }

    public <M extends PrevaylerModel> M find(Class<? extends M> cls, long id) {
        PrevalentSystem ps = (PrevalentSystem)prevayler.prevalentSystem();
        
        M result = (M) ps.find(cls, id);
        
        if (getTransaction() != null) {
            if (result != null) {
                if (getTransaction().getRemoved().contains(result)) {
                    result = null;
                }
            } else {
                for (PrevaylerModel m : getTransaction().getStored()) {
                    if (m.getId() == id) {
                        result = (M)m;

                        break;
                    }
                }
            }
        }

        return result;
    }

    public <M extends PrevaylerModel> void assertIsStored(M model) {
        if (!getSavedFieldValue(model)) {
            store(model);
        }
    }

    /**
     * Functions below are called from within Transaction objects
     *
     * @param model
     * @param <M>
     */
    public <M extends PrevaylerModel> void store(PrevaylerModel ... model) {
        if (getTransaction() == null) {
            if (!autocommit) {
                throw new IllegalStateException("No transaction found and autocommit is disabled!");
            }

            for (PrevaylerModel m : model) {
                setLastUpdateField(m);
            }

            prevayler.execute(new StoreBatchTransaction(model));

            for (PrevaylerModel m : model) {
                setSavedField(m, true);
            }
        } else {
            getTransaction().store(model);
        }
    }

    public <M extends PrevaylerModel> void remove(M ... models) {
        if (getTransaction() == null) {
            if (!autocommit) {
                throw new IllegalStateException("No transaction found and autocommit is disabled!");
            }

            for (PrevaylerModel model : models) {
                prevayler.execute(new RemoveModelTransaction<PrevaylerModel>(model));
                
                setSavedField(model, false);
            }
        } else {
            getTransaction().remove(models);
        }
    }

    private Field getSavedField() {
        if (_prevayler_saved == null) {
            try {
                _prevayler_saved = PrevaylerModel.class.getDeclaredField("_prevayler_saved");
                _prevayler_saved.setAccessible(true);
            } catch (NoSuchFieldException e) {
                throw new IllegalStateException("PrevaylerBaseModel doesn't have field _prevayler_saved!");
            }
        }

        return _prevayler_saved;
    }

    private Field getLastUpdateField() {
        synchronized (this) {
            if (_prevayler_last_update == null) {
                try {
                        _prevayler_last_update = PrevaylerModel.class.getDeclaredField("_prevayler_last_update");
                        _prevayler_last_update.setAccessible(true);
                } catch (NoSuchFieldException e) {
                    throw new IllegalStateException("PrevaylerModel doesn't have field _prevayler_last_update!");
                }
            }

            return _prevayler_last_update;
        }
    }

    private void setSavedField(PrevaylerModel model, boolean value) {
        Field field = getSavedField();

        try {
            field.set(model, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Can't set PrevaylerBaseModel._prevayler_saved!");
        }
    }

    private void setLastUpdateField(PrevaylerModel model) {
        Field field = getLastUpdateField();

        try {
            field.set(model, System.currentTimeMillis());
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Can't set PrevaylerBaseModel._prevayler_saved!");
        }
    }

    private boolean getSavedFieldValue(PrevaylerModel model) {
        Field field = getSavedField();

        try {
            return field.getBoolean(model);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Can't get PrevaylerBaseModel._prevayler_saved!");
        }
    }

    public Set<Class<? extends PrevaylerModel>> getObjectTypeList() {
        PrevalentSystem ps = (PrevalentSystem)prevayler.prevalentSystem();

        return ps.getDataStore().keySet();
    }
}
