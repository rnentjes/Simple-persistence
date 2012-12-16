package nl.astraeus.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckForNull;

/**
 * User: rnentjes
 * Date: 3/28/12
 * Time: 11:01 AM
 */
public abstract class Transaction<T> {
    private final static Logger logger = LoggerFactory.getLogger(Transaction.class);

    private T result;

    public Transaction(Lockable ... locks) {

    }

    public Transaction() {
        try {
            PersistentManager.begin();

            execute();

            PersistentManager.commit();
        } catch (Throwable e) {
            logger.error(e.getMessage(),e);

            throw e;
        } finally  {
            if (PersistentManager.transactionActive()) {
                PersistentManager.rollback();
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