package nl.astraeus.persistence;

import java.io.Serializable;

/**
 * User: rnentjes
 * Date: 10/24/12
 * Time: 1:48 PM
 */
public interface Persistent<K> extends Cloneable, Serializable, Comparable {

    public K getId();

    public Persistent<K> clone() throws CloneNotSupportedException;
}
