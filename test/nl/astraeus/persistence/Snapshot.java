package nl.astraeus.persistence;

import org.junit.Ignore;

/**
 * User: rnentjes
 * Date: 3/24/12
 * Time: 9:58 PM
 */
@Ignore
public class Snapshot {

    public Snapshot() {
        SimpleStore.get().snapshot();
    }

    public static void main(String [] args) {
        new Snapshot();
    }
}
