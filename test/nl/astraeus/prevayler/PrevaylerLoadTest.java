package nl.astraeus.prevayler;

import nl.astraeus.prevayler.model.CompanyDao;
import org.junit.Test;

/**
 * User: rnentjes
 * Date: 3/31/12
 * Time: 11:31 AM
 */
public class PrevaylerLoadTest {

    @Test
    public void test() {
        new CompanyDao().size();
    }
}
