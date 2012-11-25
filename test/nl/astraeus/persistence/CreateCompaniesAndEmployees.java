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
public class CreateCompaniesAndEmployees {

    private EmployeeDao employeeDao = new EmployeeDao();
    private CompanyDao companyDao = new CompanyDao();

    public CreateCompaniesAndEmployees() {
        System.setProperty(PersistentManager.SAFEMODE, String.valueOf(true));

        long nano = System.nanoTime();
        long base = System.currentTimeMillis();
        new Transaction() {
            @Override
            public void execute() {
                Company company = new Company(companyDao.size()+1, "Company "+Integer.toString(companyDao.size()+1));

                companyDao.store(company);
            }
        };
        System.out.println("ExecuteTransaction (1 company) took: "+ Util.formatNano(System.nanoTime() - nano));

        nano = System.nanoTime();
        new Transaction() {
            @Override
            public void execute() {
                Company company = new Company(companyDao.size()+1, "Company "+Integer.toString(companyDao.size()+1));

                companyDao.store(company);
            }
        };
        System.out.println("ExecuteTransaction (1 company) took: "+ Util.formatNano(System.nanoTime() - nano));

        nano = System.nanoTime();
        new Transaction() {
            @Override
            public void execute() {
                Company company = new Company(companyDao.size()+1, "Company "+Integer.toString(companyDao.size()+1));
                Employee employee1 = new Employee(employeeDao.size()+1, "Employee "+(company.getEmployees().size()+1), company);
                Employee employee2 = new Employee(employeeDao.size()+1, "Employee "+(company.getEmployees().size()+1), company);
                Employee employee3 = new Employee(employeeDao.size()+1, "Employee "+(company.getEmployees().size()+1), company);

                employeeDao.store(employee1);
                employeeDao.store(employee2);
                employeeDao.store(employee3);
                companyDao.store(company);
            }
        };
        System.out.println("ExecuteTransaction (1 company, 3 employees) took: "+ Util.formatNano(System.nanoTime() - nano));

        nano = System.nanoTime();
        new Transaction() {
            @Override
            public void execute() {
                for (int x=0; x < 10; x++) {
                    Company company = new Company(companyDao.size()+1, "Company "+Integer.toString(companyDao.size()+1));

                    for (int i=0; i < 50; i++) {
                        Employee employee = new Employee(employeeDao.size()+1, "Employee "+(company.getEmployees().size()+1), company);

                        employeeDao.store(employee);
                    }

                    companyDao.store(company);
                }
            }
        };
        System.out.println("ExecuteTransaction (10 company, 500 employees) took: "+ Util.formatNano(System.nanoTime() - nano));
    }

    public static void main(String [] args) {
        new CreateCompaniesAndEmployees();
    }
}
