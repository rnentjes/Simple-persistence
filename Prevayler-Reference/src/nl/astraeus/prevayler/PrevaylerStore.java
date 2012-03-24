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

    public static PrevaylerStore get() {
        return instance;
    }

    private Prevayler prevayler;

    private Field _eos_saved = null;
    private Field _eos_last_update = null;

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
        
        return result;
    }

    public <M extends PrevaylerModel> M find(Class<? extends M> cls, long id) {
        PrevalentSystem ps = (PrevalentSystem)prevayler.prevalentSystem();

        return (M) ps.find(cls, id);
    }

    public <M extends PrevaylerModel> void assertIsStored(M model) {
        if (!getEosSavedFieldValue(model)) {
            store(model);
        }
    }

    public <M extends PrevaylerModel> void store(M model) {
        Class<?> modelClass = model.getClass();

        setEosLastUpdateField(model);
        prevayler.execute(new StoreModelTransaction<M>(model));
        setEosSavedField(model, true);
    }

    public <M extends PrevaylerModel> void store(PrevaylerModel ... model) {
        Class<?> modelClass = model.getClass();

        for (PrevaylerModel m : model) {
            setEosLastUpdateField(m);
        }

        prevayler.execute(new StoreBatchTransaction(model));

        for (PrevaylerModel m : model) {
            setEosSavedField(m, true);
        }
    }

    public <M extends PrevaylerModel> void remove(M model) {
        prevayler.execute(new RemoveModelTransaction<M>(model));
        setEosSavedField(model, false);
    }

    private Field getEosSavedField() {
        if (_eos_saved == null) {
            try {
                _eos_saved = PrevaylerModel.class.getDeclaredField("_eos_saved");
                _eos_saved.setAccessible(true);
            } catch (NoSuchFieldException e) {
                throw new IllegalStateException("PrevaylerBaseModel doesn't have field _eos_saved!");
            }
        }

        return _eos_saved;
    }

    private Field getEosLastUpdateField() {
        synchronized (this) {
            if (_eos_last_update == null) {
                try {
                        _eos_last_update = PrevaylerModel.class.getDeclaredField("_eos_last_update");
                        _eos_last_update.setAccessible(true);
                } catch (NoSuchFieldException e) {
                    throw new IllegalStateException("PrevaylerBaseModel doesn't have field _eos_saved!");
                }
            }

            return _eos_last_update;
        }
    }

    private void setEosSavedField(PrevaylerModel model, boolean value) {
        Field field = getEosSavedField();

        try {
            field.set(model, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Can't set PrevaylerBaseModel._eos_saved!");
        }
    }

    private void setEosLastUpdateField(PrevaylerModel model) {
        Field field = getEosLastUpdateField();

        try {
            field.set(model, System.currentTimeMillis());
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Can't set PrevaylerBaseModel._eos_saved!");
        }
    }

    private boolean getEosSavedFieldValue(PrevaylerModel model) {
        Field field = getEosSavedField();

        try {
            return field.getBoolean(model);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Can't set PrevaylerBaseModel._eos_saved!");
        }
    }

    public Set<Class<? extends PrevaylerModel>> getObjectList() {
        PrevalentSystem ps = (PrevalentSystem)prevayler.prevalentSystem();

        return ps.getDataStore().keySet();
    }
}
