package nl.astraeus.persistence;

import junit.framework.Assert;
import nl.astraeus.persistence.model.Company;
import nl.astraeus.persistence.model.CompanyDao;
import nl.astraeus.persistence.model.Employee;
import nl.astraeus.persistence.model.EmployeeDao;
import org.junit.Test;

/**
 * User: rnentjes
 * Date: 3/31/12
 * Time: 11:23 PM
 */
public class TestTransaction {


    @Test
    public void testTransaction() {
        CompanyDao companyDao = new CompanyDao();

        SimpleStore.begin();

        Company company = new Company("Company x");

        companyDao.store(company);

        Company c = companyDao.find(company.getId());

        Assert.assertEquals(c.getId(), company.getId());
        System.out.println("Found company: "+c); // finds company "x"

        SimpleStore.rollback();

        c = companyDao.find(company.getId());

        Assert.assertNull(c);
    }

    @Test
    public void testAddAfterRemove() {
        try {
            CompanyDao companyDao = new CompanyDao();

            SimpleStore.begin();

            Company company = new Company("Company x");

            companyDao.store(company);

            SimpleStore.commit();

            SimpleStore.begin();

            Company c = companyDao.find(company.getId());

            companyDao.remove(c);

            companyDao.store(c);

            SimpleStore.commit();

            SimpleStore.begin();

            c = companyDao.find(company.getId());

            Assert.assertNotNull(c);
        } finally {
            if (SimpleStore.transactionActive()) {
                SimpleStore.rollback();
            }
        }
    }

    @Test
    public void testReferenceList() {
        CompanyDao companyDao = new CompanyDao();
        EmployeeDao employeeDao= new EmployeeDao();

        SimpleStore.begin();

        Company company = new Company("Company "+Integer.toString(companyDao.size()+1));
        Employee employee1 = new Employee("Employee "+(company.getEmployees().size()+1), company);
        Employee employee2 = new Employee("Employee "+(company.getEmployees().size()+1), company);
        Employee employee3 = new Employee("Employee "+(company.getEmployees().size()+1), company);

        employeeDao.store(employee1);
        employeeDao.store(employee2);
        employeeDao.store(employee3);
        companyDao.store(company);

        SimpleStore.commit();

        SimpleStore.begin();

        Company c = companyDao.find(company.getId());

        Assert.assertEquals(c.getEmployees().size(), 3);

        for (Employee e : c.getEmployees()) {
            Assert.assertTrue(e.equals(employee1) || e.equals(employee2) || e.equals(employee3));
        }

        companyDao.remove(c);

        SimpleStore.commit();

        Assert.assertNotNull(employeeDao.find(employee1.getId()));
        Assert.assertNotNull(employeeDao.find(employee2.getId()));
        Assert.assertNotNull(employeeDao.find(employee3.getId()));

        SimpleStore.begin();

        employeeDao.remove(employee1);
        employeeDao.remove(employee2);
        employeeDao.remove(employee3);

        SimpleStore.commit();

        Assert.assertNull(employeeDao.find(employee1.getId()));
        Assert.assertNull(employeeDao.find(employee2.getId()));
        Assert.assertNull(employeeDao.find(employee3.getId()));
    }
}
