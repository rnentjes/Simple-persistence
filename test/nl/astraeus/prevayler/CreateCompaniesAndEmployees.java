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
public class CreateCompaniesAndEmployees {

    private EmployeeDao employeeDao = new EmployeeDao();
    private CompanyDao companyDao = new CompanyDao();

    public CreateCompaniesAndEmployees() {
        PrevaylerStore.setSafemode(true);

        long nano = System.nanoTime();
        new Transaction() {
            @Override
            public void execute() {
                Company company = new Company("Company "+Integer.toString(companyDao.size()+1));

                companyDao.store(company);
            }
        };
        System.out.println("Transaction (1 company) took: "+ Util.formatNano(System.nanoTime() - nano));

        nano = System.nanoTime();
        new Transaction() {
            @Override
            public void execute() {
                Company company = new Company("Company "+Integer.toString(companyDao.size()+1));

                companyDao.store(company);
            }
        };
        System.out.println("Transaction (1 company) took: "+ Util.formatNano(System.nanoTime() - nano));

        nano = System.nanoTime();
        new Transaction() {
            @Override
            public void execute() {
                Company company = new Company("Company "+Integer.toString(companyDao.size()+1));
                Employee employee1 = new Employee("Employee "+(company.getEmployees().size()+1), company);
                Employee employee2 = new Employee("Employee "+(company.getEmployees().size()+1), company);
                Employee employee3 = new Employee("Employee "+(company.getEmployees().size()+1), company);

                employeeDao.store(employee1);
                employeeDao.store(employee2);
                employeeDao.store(employee3);
                companyDao.store(company);
            }
        };
        System.out.println("Transaction (1 company, 3 employees) took: "+ Util.formatNano(System.nanoTime() - nano));

        nano = System.nanoTime();
        new Transaction() {
            @Override
            public void execute() {
                for (int x=0; x < 10; x++) {
                    Company company = new Company("Company "+Integer.toString(companyDao.size()+1));

                    for (int i=0; i < 50; i++) {
                        Employee employee = new Employee("Employee "+(company.getEmployees().size()+1), company);

                        employeeDao.store(employee);
                    }

                    companyDao.store(company);
                }
            }
        };
        System.out.println("Transaction (10 company, 500 employees) took: "+ Util.formatNano(System.nanoTime() - nano));
    }

    public static void main(String [] args) {
        new CreateCompaniesAndEmployees();
    }
}
