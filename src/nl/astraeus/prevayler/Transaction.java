package nl.astraeus.prevayler;

import javax.annotation.CheckForNull;

/**
 * User: rnentjes
 * Date: 3/28/12
 * Time: 11:01 AM
 */
public abstract class Transaction<T> {

    T result;
    
    public Transaction() {
        try {
            PrevaylerStore.begin();

            execute();

            PrevaylerStore.commit();
        } catch (Exception e) {
            PrevaylerStore.rollback();
            
            throw new IllegalStateException(e);
        }
    }
    
    private void setResult(T t) {
        result = t;
    }

    @CheckForNull
    public T getResult() {
        return result;
    }
    
    public abstract void execute();

}