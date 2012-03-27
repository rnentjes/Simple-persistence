package nl.astraeus.util;

import java.util.HashMap;
import java.util.Map;

/**
 * User: rnentjes
 * Date: 3/27/12
 * Time: 8:49 PM
 */
public class Singleton {
    private Map<Class, Object> singletonCache = new HashMap<Class, Object>();

    private static Singleton instance = new Singleton();
    
    protected static Singleton getInstance() {
        return instance;
    }

    protected <T> T getSingleton(Class <T> cls) {
        T result = (T)singletonCache.get(cls);

        if (result == null) {
            synchronized (Singleton.class) {
                result = (T)singletonCache.get(cls);

                if (result == null) {
                    try {
                        result = cls.newInstance();
                    } catch (InstantiationException e) {
                        throw new IllegalStateException(e);
                    } catch (IllegalAccessException e) {
                        throw new IllegalStateException(e);
                    }

                    singletonCache.put(cls, result);
                }
            }
        }

        return result;
    }
    
    public static <T> T get(Class<T> cls) {
        return getInstance().getSingleton(cls);
    }
}
