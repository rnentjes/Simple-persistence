package nl.astraeus.prevayler;

import java.io.Serializable;

/**
 * User: rnentjes
 * Date: 3/27/12
 * Time: 10:05 PM
 */
public interface PrevaylerListener<M extends PrevaylerModel> extends Serializable {
    
    public void objectUpdates(M model);
    
    public void objectRemoved(M model);
    
}
