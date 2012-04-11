package nl.astraeus.persistence.model;

import nl.astraeus.persistence.SimpleModel;
import nl.astraeus.persistence.SimpleReference;
import org.junit.Ignore;

/**
 * Employee: rnentjes
 * Date: 3/24/12
 * Time: 9:59 PM
 */
@Ignore
public class Employee extends SimpleModel {
    public final static long serialVersionUID = 1L;

    private String name;
    private String description;
    private long [] randomData;
    private SimpleReference<Company> company = new SimpleReference<Company>(Company.class);

    public Employee(String name, Company company) {
        this.name = name;
        this.company.set(company);
        company.getEmployees().add(this);
        
        createRandomData();
    }
    
    private void createRandomData() {
        randomData = new long[100];

        for (int i=0; i<randomData.length; i++) {
            randomData[i] = i;
        }
    }

    public String getName() {
        return name;
    }

    public Company getCompany() {
        return company.get();
    }

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", company=" + company +
                '}';
    }
}
