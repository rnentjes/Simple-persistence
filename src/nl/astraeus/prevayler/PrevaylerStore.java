package nl.astraeus.prevayler;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * User: rnentjes
 * Date: 7/24/11
 * Time: 7:24 PM
 */
public class PrevaylerStore {
    public final static String SAFEMODE            = "safemode";
    public final static String AUTOCOMMIT          = "autocommit";
    public final static String DATA_DIRECTORY      = "dataDirectory";
    public final static String FILE_AGE_THRESHOLD  = "fileAgeThreshold";
    public final static String FILE_SIZE_THRESHOLD = "fileSizeThreshold";

    private final static PrevaylerStore instance = new PrevaylerStore();
    
    private ThreadLocal<PrevaylerTransaction> transactions = new ThreadLocal<PrevaylerTransaction>();

    public static PrevaylerStore get() {
        return instance;
    }

    private Prevayler prevayler;

    // properties
    private boolean autocommit     = false;
    private boolean safemode       = true;
    private String dataDirectory   = "prevayler";
    private long fileAgeThreshold  = TimeUnit.MINUTES.toMillis(1);
    private long fileSizeThreshold = 10*1024L*1024L;

    private boolean started = false;

    // PrevaylerModel private field cache
    private Field _prevayler_saved = null;
    private Field _prevayler_last_update = null;
    private Field _prevayler_selected_for_update = null;

    public PrevaylerStore() {
        started = true;

        long nano = System.nanoTime();
        
        try {
            if ("false".equals(System.getProperty(SAFEMODE))) {
                safemode = false;
            }

            if ("true".equals(System.getProperty(AUTOCOMMIT))) {
                autocommit = true;
            }

            if (System.getProperty(DATA_DIRECTORY) != null) {
                dataDirectory = System.getProperty(DATA_DIRECTORY);
            }

            if (System.getProperty(FILE_AGE_THRESHOLD) != null) {
                fileAgeThreshold = Long.parseLong(System.getProperty(FILE_AGE_THRESHOLD));
            }

            if (System.getProperty(FILE_SIZE_THRESHOLD) != null) {
                fileSizeThreshold = Long.parseLong(System.getProperty(FILE_SIZE_THRESHOLD));
            }

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

    public boolean isSafemode() {
        return safemode;
    }

    public boolean isAutocommit() {
        return autocommit;
    }

    // transactions
    public PrevaylerTransaction getTransaction() {
        return transactions.get();
    }

    public static boolean transactionActive() {
        return get().getTransaction() != null;
    }
    
    private void setTransaction(PrevaylerTransaction transaction) {
        transactions.set(transaction);
    }
    
    private void execute(PrevaylerTransaction transaction) {
        prevayler.execute(transaction);
    }

    private void beginTransaction() {
        if (getTransaction() != null) {
            throw new IllegalStateException("Transaction already in progress.");
        }

        setTransaction(new PrevaylerTransaction());
    }
    
    private void commitCurrentTransaction() {
        if (getTransaction() == null) {
            throw new IllegalStateException("No transaction to commit.");
        }

        // todo: add unstored references to the transaction
        /* to much magic, probably better to let developer handle this
        try {
            for (PrevaylerModel m : getTransaction().getStored()) {
                for (Field field : ReflectHelper.get().getReferenceFieldsFromClass(m.getClass())) {
                    PrevaylerReference ref = (PrevaylerReference)field.get(m);
                    PrevaylerModel model = ref.get();
                    if (model != null && !getSavedFieldValue(model)) {
                        // add to stored
                    }
                }
                for (Field field : ReflectHelper.get().getListFieldsFromClass(m.getClass())) {
                    PrevaylerList list = (PrevaylerList)field.get(m);
                    for (PrevaylerModel model : list) {
                        if (model != null && !getSavedFieldValue(model)) {
                            // add to stored
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }*/

        if (getTransaction().hasChanges()) {
            prevayler.execute(getTransaction());
        }
        transactions.remove();
    }
    
    private void rollbackCurrentTransaction() {
        if (getTransaction() == null) {
            throw new IllegalStateException("No transaction to rollback.");
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
                if (m.getClass().equals(cls)) {
                    result.put(m.getId(), m);
                }
            }

            for (PrevaylerModel m : getTransaction().getRemoved()) {
                if (m.getClass().equals(cls)) {
                    result.remove(m.getId());
                }
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

        if (safemode && result != null) {
            try {
                result = cls.cast(result.clone());
            } catch (CloneNotSupportedException e) {
                throw new IllegalStateException(e);
            }
        }

        return result;
    }

    /**
     * Store functions
     */

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
                throw new IllegalStateException("PrevaylerModel doesn't have field "+name+".");
            }
        }

        return result;
    }

    void setSavedField(PrevaylerModel model, boolean value) {
        Field field = getSavedField();

        try {
            field.set(model, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Can't set PrevaylerBaseModel._prevayler_saved.");
        }
    }

    void setLastUpdateField(PrevaylerModel model) {
        Field field = getLastUpdateField();

        try {
            field.set(model, System.currentTimeMillis());
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Can't set PrevaylerBaseModel._prevayler_last_update.");
        }
    }

    void setSelectForUpdateField(PrevaylerModel model, boolean value) {
        Field field = getSelectForUpdateField();

        try {
            field.set(model, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Can't set PrevaylerBaseModel._prevayler_selected_for_update.");
        }
    }

    boolean getSavedFieldValue(PrevaylerModel model) {
        Field field = getSavedField();

        try {
            return field.getBoolean(model);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Can't get PrevaylerBaseModel._prevayler_saved.");
        }
    }

    public Set<Class<? extends PrevaylerModel>> getObjectTypeList() {
        PrevalentSystem ps = (PrevalentSystem)prevayler.prevalentSystem();

        return ps.getDataStore().keySet();
    }

    public Map<Class<? extends PrevaylerModel>, Integer> getObjectTypeMap() {
        Map<Class<? extends PrevaylerModel>, Integer> result = new HashMap<Class<? extends PrevaylerModel>, Integer>();
        PrevalentSystem ps = (PrevalentSystem)prevayler.prevalentSystem();

        for (Class cls : ps.getDataStore().keySet()) {
            result.put(cls, ps.getDataStore().get(cls).size());
        }

        return result;
    }
}
