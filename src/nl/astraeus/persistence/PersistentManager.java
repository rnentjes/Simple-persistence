package nl.astraeus.persistence;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: rnentjes
 * Date: 7/24/11
 * Time: 7:24 PM
 */
public class PersistentManager {
    private final static Logger logger = LoggerFactory.getLogger(PersistentManager.class);

    public final static String SAFEMODE                             = "safemode";
    public final static String AUTOCOMMIT                           = "autocommit";
    public final static String DATA_DIRECTORY                       = "dataDirectory";
    public final static String FILE_AGE_THRESHOLD                   = "fileAgeThreshold";
    public final static String FILE_SIZE_THRESHOLD                  = "fileSizeThreshold";
    public final static String MINIMAL_FILE_AGE_BEFORE_DELETION     = "minimalFileAgeBeforeDeletion";

    private final static PersistentManager INSTANCE = new PersistentManager();

    private ThreadLocal<PersistentTransaction> transactions = new ThreadLocal<PersistentTransaction>();

    public static PersistentManager get() {
        if (!INSTANCE.started) {
            synchronized (INSTANCE) {
                if (!INSTANCE.started) {
                    INSTANCE.started = true;
                    INSTANCE.start();
                }
            }
        }

        return INSTANCE;
    }

    private Prevayler prevayler;

    // properties
    private boolean autocommit                  = false;
    private boolean safemode                    = true;
    private String dataDirectory                = "persistent";
    private long fileAgeThreshold               = TimeUnit.MINUTES.toMillis(1);
    private long fileSizeThreshold              = 10*1024L*1024L;
    private long minimalFileAgeBeforeDeletion   = TimeUnit.DAYS.toMillis(7);

    private boolean started = false;
    private boolean nodes = false;

    public PersistentManager() {
        if ("false".equals(System.getProperty(SAFEMODE))) {
            logger.warn("Simple persistence is not operating in SAFEMODE, I hope you know what you are doing...");

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

        if (System.getProperty(MINIMAL_FILE_AGE_BEFORE_DELETION) != null) {
            minimalFileAgeBeforeDeletion = Long.parseLong(System.getProperty(MINIMAL_FILE_AGE_BEFORE_DELETION)) * 60000L;
        }

        try {
            SimpleNodeManager.get().init(
                    System.getProperty("simple.node.ip"),
                    Integer.parseInt(System.getProperty("simple.node.port")),
                    Integer.parseInt(System.getProperty("simple.node.divider")),
                    Integer.parseInt(System.getProperty("simple.node.remainder")));

            nodes = true;
        } catch (NumberFormatException e) {
            logger.debug(e.getMessage(), e);
        } catch (IllegalStateException e) {
            logger.warn(e.getMessage(), e);
        }
    }

    private void start() {
        try {
            //PrevaylerFactory.
            PrevaylerFactory factory = new PrevaylerFactory();

            factory.configureJournalFileAgeThreshold(fileAgeThreshold);
            factory.configureJournalFileSizeThreshold(fileSizeThreshold);
            factory.configurePrevalentSystem(new PersistentObjectStore());
            factory.configurePrevalenceDirectory(dataDirectory);

            //factory.configureJournalSerializer("journal", new SimpleJournalSerializer());
            //factory.configureSnapshotSerializer("snapshot", new SimpleSnapshotSerializer());

            prevayler = factory.create();
        } catch (Exception e) {
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
    public PersistentTransaction getTransaction() {
        return transactions.get();
    }

    public static boolean transactionActive() {
        return get().getTransaction() != null;
    }

    private void setTransaction(PersistentTransaction transaction) {
        transactions.set(transaction);
    }

    private void execute(PersistentTransaction transaction) {
        prevayler.execute(transaction);
    }

    private void beginTransaction() {
        if (getTransaction() != null) {
            throw new IllegalStateException("ExecuteTransaction already in progress.");
        }

        setTransaction(new PersistentTransaction());
    }

    private void commitCurrentTransaction() {
        if (getTransaction() == null) {
            throw new IllegalStateException("No transaction to commit.");
        }

        // todo: add unstored references to the transaction
        /* to much magic, probably better to let developer handle this
        try {
            for (SimpleModel m : getTransaction().getStored()) {
                for (Field field : ReflectHelper.get().getReferenceFieldsFromClass(m.getClass())) {
                    SimpleReference ref = (SimpleReference)field.get(m);
                    SimpleModel model = ref.get();
                    if (model != null && !getSavedFieldValue(model)) {
                        // add to stored
                    }
                }
                for (Field field : ReflectHelper.get().getListFieldsFromClass(m.getClass())) {
                    SimpleList list = (SimpleList)field.get(m);
                    for (SimpleModel model : list) {
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
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void cleanUp() {
        File dataDirectory = new File(this.dataDirectory);

        if (!dataDirectory.isDirectory()) {
            throw new IllegalStateException("Can't cleanup data directory is not a directory!");
        }

        long lastSnapshot = 0L;
        long lastModified = System.currentTimeMillis() - minimalFileAgeBeforeDeletion;
        for (File file : dataDirectory.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".snapshot")) {
                lastSnapshot = Math.max(lastSnapshot, file.lastModified());
            }
        }

        lastModified = Math.min(lastModified, lastSnapshot);

        for (File file : dataDirectory.listFiles()) {
            if (file.isFile() &&
                    (file.getName().endsWith(".snapshot")  || file.getName().endsWith(".journal")) &&
                    file.lastModified() < lastModified)  {
                file.delete();
            }
        }

    }

    // retrieval functions
    public <K, M extends Persistent<K>> Map<K, M> getModelMap(Class<M> cls) {
        PersistentObjectStore pos = (PersistentObjectStore)prevayler.prevalentSystem();

        Map result = pos.getModelMap(cls);

        if (getTransaction() != null) {
            for (PersistentTransaction.Action action : getTransaction().getActions()) {
                if (action.isClass(cls)) {
                    if (action.remove) {
                        result.remove(action.model.getId());
                    } else {
                        result.put((K)action.model.getId(), (M)action.model);
                    }
                }
            }
        }

        return result;
    }

    public <K, M extends Persistent<K>> M find(Class<? extends M> cls, K id) {
        PersistentObjectStore ps = (PersistentObjectStore)prevayler.prevalentSystem();

        M result = (M) ps.find(cls, id);

        if (getTransaction() != null) {
            for (PersistentTransaction.Action action : getTransaction().getActions()) {
                if (cls.equals(action.model.getClass()) && id.equals(action.model.getId())) {
                    if (action.remove) {
                        result = null;
                    } else {
                        result = (M)action.model;
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

    public Set<Class<? extends Persistent>> getObjectTypeList() {
        PersistentObjectStore ps = (PersistentObjectStore)prevayler.prevalentSystem();

        return ps.getPersistentStore().keySet();
    }

    public Map<Class<? extends Persistent>, Integer> getObjectTypeMap() {
        Map<Class<? extends Persistent>, Integer> result = new HashMap<Class<? extends Persistent>, Integer>();
        PersistentObjectStore ps = (PersistentObjectStore)prevayler.prevalentSystem();

        for (Class cls : ps.getPersistentStore().keySet()) {
            result.put(cls, ps.getPersistentStore().get(cls).size());
        }

        return result;
    }

    public <K, M extends Persistent<K>> Collection<Persistent> findAll(Class<M> clazz) {
        PersistentObjectStore ps = (PersistentObjectStore)prevayler.prevalentSystem();

        return ps.getPersistentStore().get(clazz).values();
    }

    public <K, M extends Persistent<K>> void createIndex(Class<M> cls, String property) {
        PersistentObjectStore ps = (PersistentObjectStore)prevayler.prevalentSystem();

        PersistentIndexTransaction cit = new PersistentIndexTransaction(ps, cls, property);

        prevayler.execute(cit);
    }

    public <K, M extends Persistent<K>> void removeIndex(Class<M> cls, String property) {
        PersistentRemoveIndexTransaction prit = new PersistentRemoveIndexTransaction(cls, property);

        prevayler.execute(prit);
    }

    public <K, M extends Persistent<K>> PersistentIndex getIndex(Class<M> cls, String name) {
        PersistentObjectStore pos = (PersistentObjectStore)prevayler.prevalentSystem();

        return pos.getIndex(cls, name);
    }

    public <K, M extends Persistent<K>> Map<String, PersistentIndex> getIndexMap(Class<M> cls) {
        PersistentObjectStore pos = (PersistentObjectStore)prevayler.prevalentSystem();

        return pos.getIndexMap(cls);
    }

}
