package nl.astraeus.persistence;

import nl.astraeus.persistence.model.Company;
import nl.astraeus.persistence.model.CompanyDao;
import nl.astraeus.persistence.model.Employee;
import nl.astraeus.persistence.model.EmployeeDao;
import nl.astraeus.util.Util;
import org.junit.Ignore;

/**
 * User: rnentjes
 * Date: 3/24/12
 * Time: 9:58 PM
 */
@Ignore
public class CreateALotOfCompaniesAndEmployees {

    private EmployeeDao employeeDao = new EmployeeDao();
    private CompanyDao companyDao = new CompanyDao();

    public CreateALotOfCompaniesAndEmployees() {
        System.setProperty(PersistentManager.SAFEMODE, String.valueOf(true));

        long nano = System.nanoTime();
        new Transaction() {
            @Override
            public void execute() {
                long base = System.currentTimeMillis();
                for (int x=0; x < 100; x++) {
                    Company company = new Company(base+x, "Company "+Integer.toString(companyDao.size()+1));

                    for (int i=0; i < 50; i++) {
                        Employee employee = new Employee(base + x * 1000 + i, "Employee "+(company.getEmployees().size()+1), company);

                        employeeDao.store(employee);
                    }

                    companyDao.store(company);
                }
            }
        };
        System.out.println("ExecuteTransaction (10 company, 500 employees) took: "+ Util.formatNano(System.nanoTime() - nano));
    }

    public static void main(String [] args) {
        new CreateALotOfCompaniesAndEmployees();
    }
}
