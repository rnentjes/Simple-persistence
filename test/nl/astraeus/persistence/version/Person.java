package nl.astraeus.persistence.version;

import nl.astraeus.persistence.Persistent;
import nl.astraeus.persistence.Version;
import org.junit.Ignore;

/**
 * User: rnentjes
 * Date: 11/25/12
 * Time: 8:58 PM
 */
@Ignore
public class Person implements Persistent<Long> {
    public final static long serialVersionUID = 1L;

    private long id;

    @Version
    private long version;

    private String name;

    public Person(String name) {
        this.name = name;
        this.id = PersonDao.get().getNextId();
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(Object o) {
        if (name == null) {
            return -1;
        } else {
            return name.compareTo(((Person)o).name);
        }
    }

    @Override
    public Person clone() throws CloneNotSupportedException {
        return (Person)super.clone();
    }
}
