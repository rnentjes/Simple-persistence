package nl.astraeus.persistence;

import nl.astraeus.persistence.model.CompanyDao;
import nl.astraeus.persistence.model.EmployeeDao;
import nl.astraeus.util.Util;
import org.junit.Ignore;

/**
 * User: rnentjes
 * Date: 3/24/12
 * Time: 9:58 PM
 */
@Ignore
public class ShowMemoryUsage {
    private CompanyDao companyDao = new CompanyDao();
    private EmployeeDao employeeDao = new EmployeeDao();

    public ShowMemoryUsage() {
        Util.printMemoryUsage();

        long nano = System.nanoTime();

        System.out.println("Loaded " + companyDao.size() + " companies");
        System.out.println("Loaded " + employeeDao.size() + " employees");

        System.out.println("Loading data took: "+Util.formatNano(System.nanoTime() - nano));

        System.gc();
        Util.printMemoryUsage();
    }

    public static void main(String [] args) {
        new ShowMemoryUsage();
    }
}
