package nl.astraeus.prevayler.model;

import nl.astraeus.prevayler.PrevaylerModel;
import nl.astraeus.prevayler.PrevaylerReference;
import org.junit.Ignore;

/**
 * Employee: rnentjes
 * Date: 3/24/12
 * Time: 9:59 PM
 */
@Ignore
public class Employee extends PrevaylerModel {
    public final static long serialVersionUID = 1L;

    private String name;
    private String description;
    private long [] randomData;
    private PrevaylerReference<Company> company = new PrevaylerReference<Company>(Company.class);

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
