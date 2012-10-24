package nl.astraeus.persistence;

/**
 * User: rnentjes
 * Date: 10/24/12
 * Time: 7:36 PM
 */
public class OptimisticLockingException extends RuntimeException {

    public OptimisticLockingException() {
    }

    public OptimisticLockingException(String message) {
        super(message);
    }

    public OptimisticLockingException(String message, Throwable cause) {
        super(message, cause);
    }

    public OptimisticLockingException(Throwable cause) {
        super(cause);
    }

    public OptimisticLockingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
