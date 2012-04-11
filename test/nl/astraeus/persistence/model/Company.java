package nl.astraeus.persistence.model;

import nl.astraeus.persistence.SimpleList;
import nl.astraeus.persistence.SimpleModel;
import org.junit.Ignore;

/**
 * Employee: rnentjes
 * Date: 3/24/12
 * Time: 9:59 PM
 */
@Ignore
public class Company extends SimpleModel {
    public final static long serialVersionUID = 1L;
    
    private String name;
    private long [] randomData;
    private SimpleList<Employee> employees = new SimpleList<Employee>(Employee.class);

    public Company(String name) {
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

    public SimpleList<Employee> getEmployees() {
        return employees;
    }

    @Override
    public String toString() {
        return "Company{" +
                "name='" + name + '\'' +
                ", employees=" + employees +
                '}';
    }
}
