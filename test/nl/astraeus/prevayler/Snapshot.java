package nl.astraeus.prevayler;

import nl.astraeus.prevayler.model.Company;
import nl.astraeus.prevayler.model.CompanyDao;
import nl.astraeus.prevayler.model.Employee;
import nl.astraeus.prevayler.model.EmployeeDao;
import nl.astraeus.util.Util;

/**
 * User: rnentjes
 * Date: 3/24/12
 * Time: 9:58 PM
 */
public class Snapshot {

    public Snapshot() {
        PrevaylerStore.get().snapshot();
    }

    public static void main(String [] args) {
        new Snapshot();
    }
}
