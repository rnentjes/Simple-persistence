package nl.astraeus.prevayler;

import nl.astraeus.prevayler.model.Company;
import nl.astraeus.prevayler.model.CompanyDao;
import nl.astraeus.prevayler.model.Employee;
import nl.astraeus.prevayler.model.EmployeeDao;
import nl.astraeus.util.Singleton;

/**
 * User: rnentjes
 * Date: 3/24/12
 * Time: 9:58 PM
 */
public class TestPrevayler {

    private EmployeeDao employeeDao = new EmployeeDao();
    private CompanyDao companyDao = new CompanyDao();

    public TestPrevayler() {
        System.out.println(companyDao.size()+" Companies and "+ employeeDao.size()+" employees.");
        for (Company company : companyDao.findAll()) {
            System.out.println("Company: "+company);
            for (Employee employee : company.getEmployees()) {
                System.out.println("\tEmployee: "+ employee);
            }
        }
        
        PrevaylerStore.get().begin();

        Company company = new Company("Company "+Integer.toString(companyDao.size()+1));
        Employee employee1 = new Employee("Employee "+(company.getEmployees().size()+1), company);
        Employee employee2 = new Employee("Employee "+(company.getEmployees().size()+1), company);
        Employee employee3 = new Employee("Employee "+(company.getEmployees().size()+1), company);

        companyDao.store(company, employee1, employee2, employee3);
        
        Company c = companyDao.find(company.getId());
        
        System.out.println("Found company: "+c);

        PrevaylerStore.get().commit();
    }
    
    public void testTransaction() {
        PrevaylerStore.get().begin();

        try {
            Company c = new Company("test company");
            companyDao.store(c);
            Employee e = new Employee("employee", c);

            employeeDao.store(e);

            employeeDao.find(e.getId());

            PrevaylerStore.get().commit();
        } catch (Exception e) {
             PrevaylerStore.get().rollback();

             throw new IllegalStateException(e);
        }
        
    }


    public static void main(String [] args) {
        new TestPrevayler();
    }
}