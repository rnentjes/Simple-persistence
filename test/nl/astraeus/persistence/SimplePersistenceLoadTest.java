package nl.astraeus.persistence;

import nl.astraeus.persistence.model.CompanyDao;
import org.junit.Test;

/**
 * User: rnentjes
 * Date: 3/31/12
 * Time: 11:31 AM
 */
public class SimplePersistenceLoadTest {

    @Test
    public void test() {
        new CompanyDao().size();
    }
}
