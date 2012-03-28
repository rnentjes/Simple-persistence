package nl.astraeus.prevayler;

import nl.astraeus.util.Util;
import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
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

    // properties
    private static boolean autocommit;
    private static boolean safemode;
    private static String dataDirectory;
    private static long fileAgeThreshold;
    private static long fileSizeThreshold;

    // defaults
    {
        autocommit = false;
        safemode = false;
        dataDirectory = "prevayler";
        fileAgeThreshold = TimeUnit.MINUTES.toMillis(1);
        fileSizeThreshold = 1024L*1024L;
    }

    
    private boolean started = false;

    // PrevaylerModel private field cache
    private Field _prevayler_saved = null;
    private Field _prevayler_last_update = null;
    private Field _prevayler_selected_for_update = null;

    public PrevaylerStore() {
        started = true;

        long nano = System.nanoTime();
        
        try {
            PrevaylerFactory factory = new PrevaylerFactory();

            factory.configureJournalFileAgeThreshold(fileAgeThreshold);
            factory.configureJournalFileSizeThreshold(fileSizeThreshold);
            factory.configurePrevalentSystem(new PrevalentSystem());
            factory.configurePrevalenceDirectory(dataDirectory);

            prevayler = factory.create();
        } catch (IOException e) {
            throw new IllegalStateException("Can't start Prevayler!", e);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Can't start Prevayler!", e);
        }
    }

    public static boolean isSafemode() {
        return safemode;
    }

    public static void setSafemode(boolean safemode) {
        PrevaylerStore.safemode = safemode;
    }

    public static boolean isAutocommit() {
        return autocommit;
    }

    public static void setAutocommit(boolean autocommit) {
        PrevaylerStore.autocommit = autocommit;
    }

    // transactions
    public PrevaylerTransaction getTransaction() {
        return transactions.get();
    }
    
    private void setTransaction(PrevaylerTransaction transaction) {
        transactions.set(transaction);
    }
    
    private void execute(PrevaylerTransaction transaction) {
        prevayler.execute(transaction);
    }

    private void beginTransaction() {
        if (getTransaction() != null) {
            throw new IllegalStateException("Transaction already in progress!");
        }

        setTransaction(new PrevaylerTransaction());
    }
    
    private void commitCurrentTransaction() {
        if (getTransaction() == null) {
            throw new IllegalStateException("No transaction to commit!");
        }

        // todo: add unstored references to the transaction

        prevayler.execute(getTransaction());
        transactions.remove();
    }
    
    private void rollbackCurrentTransaction() {
        if (getTransaction() == null) {
            throw new IllegalStateException("No transaction to rollback!");
        }

        transactions.remove();
    }

    public static void begin() {
        get().beginTransaction();
    }
    
    public static void commit() {
        get().commitCurrentTransaction();
    }
    
    public static void rollback() {
        get().rollbackCurrentTransaction();
    }

    public void snapshot() {
        try {
            prevayler.takeSnapshot();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    // retrieval functions
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

        if (safemode) {
            try {
                result = cls.cast(result.clone());
            } catch (CloneNotSupportedException e) {
                throw new IllegalStateException(e);
            }
        }

        return result;
    }

    // ??
    public <M extends PrevaylerModel> void assertIsStored(M model) {
        if (!getSavedFieldValue(model)) {
            store(model);
        }
    }

    /**
     * Store functions
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
        return getField(_prevayler_saved, "_prevayler_saved");
    }

    private Field getLastUpdateField() {
        return getField(_prevayler_last_update, "_prevayler_last_update");
    }

    private Field getSelectForUpdateField() {
        return getField(_prevayler_selected_for_update, "_prevayler_selected_for_update");
    }

    private Field getField(Field current, String name) {
        Field result = current;

        if (current == null) {
            try {
                result = PrevaylerModel.class.getDeclaredField(name);
                result.setAccessible(true);
            } catch (NoSuchFieldException e) {
                throw new IllegalStateException("PrevaylerModel doesn't have field "+name);
            }
        }

        return result;
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
            throw new IllegalStateException("Can't set PrevaylerBaseModel._prevayler_last_update");
        }
    }

    private void setSelectForUpdateField(PrevaylerModel model, boolean value) {
        Field field = getSelectForUpdateField();

        try {
            field.set(model, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Can't set PrevaylerBaseModel._prevayler_selected_for_update!");
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
