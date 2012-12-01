package nl.astraeus.persistence;

import nl.astraeus.persistence.model.Company;
import nl.astraeus.persistence.model.CompanyDao;
import nl.astraeus.persistence.model.Employee;
import nl.astraeus.persistence.model.EmployeeDao;
import org.junit.Before;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

/**
 * User: rnentjes
 * Date: 4/11/12
 * Time: 10:19 PM
 */
public class TestSerializer {

    public static void main(String [] args) throws IOException {
        new TestSerializer().testSerializer();
    }

    @Before
    public void init() {
        System.setProperty(PersistentManager.DATA_DIRECTORY, "data/test");
    }

    @Test
    public void testSerializer() throws IOException {
        CompanyDao dao = new CompanyDao();

        Collection<Company> comps = dao.find(0, 1);

        if (comps.isEmpty()) {
            PersistentManager.begin();

            Company c = new Company(1, "Test");
            Employee e = new Employee(1, "Rien", c);

            CompanyDao.get().store(c);
            EmployeeDao.get().store(e);

            PersistentManager.commit();

            comps = dao.find(0, 1);
        }

        if (!comps.isEmpty()) {
            SimpleSerializer simpleSer = new SimpleSerializer();

            FileOutputStream fos = new FileOutputStream("test_serializer.txt");

            simpleSer.writeObject(fos, comps.iterator().next());

            fos.close();
        }
    }
}
