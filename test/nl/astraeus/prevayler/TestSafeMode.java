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
public class TestSafeMode {

    private EmployeeDao employeeDao = new EmployeeDao();
    private CompanyDao companyDao = new CompanyDao();

    public TestSafeMode() {
        PrevaylerStore.setSafemode(true);

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
