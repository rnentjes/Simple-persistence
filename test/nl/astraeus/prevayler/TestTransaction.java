package nl.astraeus.prevayler;

import junit.framework.Assert;
import nl.astraeus.prevayler.model.Company;
import nl.astraeus.prevayler.model.CompanyDao;
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

        PrevaylerStore.begin();

        Company company = new Company("Company x");

        companyDao.store(company);

        Company c = companyDao.find(company.getId());

        Assert.assertEquals(c.getId(), company.getId());
        System.out.println("Found company: "+c); // finds company "x"

        PrevaylerStore.rollback();

        c = companyDao.find(company.getId());

        Assert.assertNull(c);
    }
}
