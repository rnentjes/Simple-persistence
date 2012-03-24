package nl.astraeus.prevayler;

/**
 * User: rnentjes
 * Date: 9/11/11
 * Time: 9:05 PM
 */
public abstract class Filter<M extends PrevaylerModel> {

    public abstract boolean include(M model);

    
}
