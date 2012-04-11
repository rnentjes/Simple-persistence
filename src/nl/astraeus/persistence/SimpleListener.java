package nl.astraeus.persistence;

import java.io.Serializable;

/**
 * User: rnentjes
 * Date: 3/27/12
 * Time: 10:05 PM
 */
public interface SimpleListener<M extends SimpleModel> extends Serializable {
    
    public void objectUpdates(M model);
    
    public void objectRemoved(M model);
    
}
