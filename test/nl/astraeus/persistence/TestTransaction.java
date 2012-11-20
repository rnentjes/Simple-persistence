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

        PersistentManager.begin();

        Company company = new Company(companyDao.size()+1, "Company x");

        companyDao.store(company);

        Company c = companyDao.find(company.getId());

        Assert.assertEquals(c.getId(), company.getId());
        System.out.println("Found company: "+c); // finds company "x"

        PersistentManager.rollback();

        c = companyDao.find(company.getId());

        Assert.assertNull(c);
    }

    @Test
    public void testAddAfterRemove() {
        try {
            CompanyDao companyDao = new CompanyDao();

            PersistentManager.begin();

            Company company = new Company(companyDao.size()+1, "Company x");

            companyDao.store(company);

            PersistentManager.commit();

            PersistentManager.begin();

            Company c = companyDao.find(company.getId());

            companyDao.remove(c);

            companyDao.store(c);

            PersistentManager.commit();

            PersistentManager.begin();

            c = companyDao.find(company.getId());

            Assert.assertNotNull(c);
        } finally {
            if (PersistentManager.transactionActive()) {
                PersistentManager.rollback();
            }
        }
    }

    @Test
    public void testReferenceList() {
        CompanyDao companyDao = new CompanyDao();
        EmployeeDao employeeDao= new EmployeeDao();

        PersistentManager.begin();

        Company company = new Company(companyDao.size()+1, "Company "+Integer.toString(companyDao.size()+1));
        Employee employee1 = new Employee(employeeDao.size()+1, "Employee "+(company.getEmployees().size()+1), company);
        Employee employee2 = new Employee(employeeDao.size()+1, "Employee "+(company.getEmployees().size()+1), company);
        Employee employee3 = new Employee(employeeDao.size()+1, "Employee "+(company.getEmployees().size()+1), company);

        employeeDao.store(employee1);
        employeeDao.store(employee2);
        employeeDao.store(employee3);
        companyDao.store(company);

        PersistentManager.commit();

        PersistentManager.begin();

        Company c = companyDao.find(company.getId());

        Assert.assertEquals(c.getEmployees().size(), 3);

        for (Employee e : c.getEmployees()) {
            Assert.assertTrue(e.equals(employee1) || e.equals(employee2) || e.equals(employee3));
        }

        companyDao.remove(c);

        PersistentManager.commit();

        Assert.assertNotNull(employeeDao.find(employee1.getId()));
        Assert.assertNotNull(employeeDao.find(employee2.getId()));
        Assert.assertNotNull(employeeDao.find(employee3.getId()));

        PersistentManager.begin();

        employeeDao.remove(employee1);
        employeeDao.remove(employee2);
        employeeDao.remove(employee3);

        PersistentManager.commit();

        Assert.assertNull(employeeDao.find(employee1.getId()));
        Assert.assertNull(employeeDao.find(employee2.getId()));
        Assert.assertNull(employeeDao.find(employee3.getId()));
    }
}
