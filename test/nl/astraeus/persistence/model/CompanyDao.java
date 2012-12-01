package nl.astraeus.persistence.model;

import nl.astraeus.persistence.PersistentDao;
import org.junit.Ignore;

import java.util.Collection;

/**
 * Employee: rnentjes
 * Date: 3/24/12
 * Time: 10:03 PM
 */
@Ignore
public class CompanyDao extends PersistentDao<Long, Company> {

    public static CompanyDao get() {
        return new CompanyDao();
    }

    public Collection<Company> findByEmpoyeeName(final String name) {
        return createQuery().equals("name", name).getResultSet();
    }

}
