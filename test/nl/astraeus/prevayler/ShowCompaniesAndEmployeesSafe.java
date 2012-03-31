package nl.astraeus.prevayler;

import nl.astraeus.prevayler.model.Company;
import nl.astraeus.prevayler.model.CompanyDao;
import nl.astraeus.prevayler.model.Employee;
import nl.astraeus.prevayler.model.EmployeeDao;
import nl.astraeus.util.Util;
import org.junit.Ignore;

/**
 * User: rnentjes
 * Date: 3/24/12
 * Time: 9:58 PM
 */
@Ignore
public class ShowCompaniesAndEmployeesSafe {

    private EmployeeDao employeeDao = new EmployeeDao();
    private CompanyDao companyDao = new CompanyDao();

    public ShowCompaniesAndEmployeesSafe() {
        System.setProperty(PrevaylerStore.SAFEMODE, String.valueOf(true));

        long nano = System.nanoTime();
        System.out.println(companyDao.size()+" Companies and "+ employeeDao.size()+" employees.");
        for (Company company : companyDao.findAll()) {
            System.out.println("Company: "+company);
            for (Employee employee : company.getEmployees()) {
                System.out.println("\tEmployee: "+ employee);
            }
        }
        System.out.println("Showing model data took: "+ Util.formatNano(System.nanoTime() - nano));
    }

    public static void main(String [] args) {
        new ShowCompaniesAndEmployeesSafe();
    }
}
