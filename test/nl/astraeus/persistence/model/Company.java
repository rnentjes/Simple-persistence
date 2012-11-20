package nl.astraeus.persistence.model;

import nl.astraeus.persistence.Persistent;
import nl.astraeus.persistence.PersistentList;
import org.junit.Ignore;

/**
 * Employee: rnentjes
 * Date: 3/24/12
 * Time: 9:59 PM
 */
@Ignore
public class Company implements Persistent<Long> {
    public final static long serialVersionUID = 1L;


    private long id;
    private String name;
    private long [] randomData;
    private PersistentList<Long, Employee> employees = new PersistentList<Long, Employee>(Employee.class);

    public Company(long id, String name) {
        this.id = id;
        this.name = name;

        createRandomData();
    }

    public void setName(String name) {
        this.name = name;
    }

    private void createRandomData() {
        randomData = new long[100];

        for (int i=0; i<randomData.length; i++) {
            randomData[i] = i;
        }
    }

    public PersistentList<Long, Employee> getEmployees() {
        return employees;
    }

    @Override
    public String toString() {
        return "Company{" +
                "name='" + name + '\'' +
                ", employees=" + employees +
                '}';
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public int compareTo(Object o) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Company clone() throws CloneNotSupportedException {
        return (Company)super.clone();
    }
}
