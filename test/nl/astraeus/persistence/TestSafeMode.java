package nl.astraeus.persistence;

import nl.astraeus.persistence.model.Company;
import nl.astraeus.persistence.model.CompanyDao;
import nl.astraeus.persistence.model.EmployeeDao;
import org.junit.Ignore;

/**
 * User: rnentjes
 * Date: 3/24/12
 * Time: 9:58 PM
 */
@Ignore
public class TestSafeMode {

    private EmployeeDao employeeDao = new EmployeeDao();
    private CompanyDao companyDao = new CompanyDao();

    public TestSafeMode() {
        System.setProperty(PersistentManager.SAFEMODE, String.valueOf(true));

        System.out.println(companyDao.size()+" companies.");

        for (Company company : companyDao.find(0,5)) {
            System.out.println(company);
            company.setName("CHANGED");
        }

        System.out.println("-------------------------");

        new Transaction() {
            @Override
            public void execute() {
                for (Company company : companyDao.find(3,4)) {
                    company.setName("CHANGED");
                    companyDao.store(company);
                }
            }
        };

        System.out.println("-------------------------");

        for (Company company : companyDao.find(0,5)) {
            System.out.println(company);
        }
    }

    public static void main(String [] args) {
        new TestSafeMode();
    }
}
