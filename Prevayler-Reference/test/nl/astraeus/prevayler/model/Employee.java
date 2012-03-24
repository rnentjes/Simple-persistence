package nl.astraeus.prevayler.model;

import nl.astraeus.prevayler.PrevaylerModel;
import nl.astraeus.prevayler.PrevaylerReference;

/**
 * Employee: rnentjes
 * Date: 3/24/12
 * Time: 9:59 PM
 */
public class Employee extends PrevaylerModel {
    public final static long serialVersionUID = 1L;

    private String name;
    private String description;
    private PrevaylerReference<Company> company = new PrevaylerReference<Company>(Company.class);

    public Employee(String name, Company company) {
        this.name = name;
        this.company.set(company);
        company.getEmployees().add(this);
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
