package nl.astraeus.persistence.serialize;

import nl.astraeus.persistence.SimpleStore;
import nl.astraeus.persistence.model.CompanyDao;
import org.junit.Ignore;

/**
 * User: rnentjes
 * Date: 7/8/12
 * Time: 11:35 AM
 */
@Ignore
public class TestSnapshotSerializer {

    public static void main(String [] args) throws Exception {
        new TestSnapshotSerializer();
    }

    public TestSnapshotSerializer() throws Exception {
        CompanyDao dao = new CompanyDao();

        dao.findAll();

        SimpleStore.get().snapshot();
    }
}
