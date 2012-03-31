package nl.astraeus.prevayler;

import javax.annotation.CheckForNull;

/**
 * User: rnentjes
 * Date: 3/28/12
 * Time: 11:01 AM
 */
public abstract class Transaction<T> {

    private T result;
    
    public Transaction() {
        try {
            PrevaylerStore.begin();

            execute();

            PrevaylerStore.commit();
        } finally  {
            if (PrevaylerStore.transactionActive()) {
                PrevaylerStore.rollback();
            }
        }
    }
    
    protected void setResult(T t) {
        result = t;
    }

    @CheckForNull
    public T getResult() {
        return result;
    }
    
    public abstract void execute();

}