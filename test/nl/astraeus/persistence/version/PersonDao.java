package nl.astraeus.persistence.version;

import nl.astraeus.persistence.PersistentDao;
import org.junit.Ignore;

/**
 * User: rnentjes
 * Date: 11/25/12
 * Time: 8:59 PM
 */
@Ignore
public class PersonDao extends PersistentDao<Long, Person> {

    private static PersonDao instance = new PersonDao();

    public static PersonDao get() {
        return instance;
    }

    private long nextId;

    public PersonDao() {
        synchronized (Person.class) {
            for (Person person : findAll()) {
                nextId = Math.max(person.getId(), nextId);
            }
            ++nextId;
        }
    }

    public long getNextId() {
        return nextId++;
    }

}
