package nl.astraeus.prevayler.model;

import nl.astraeus.prevayler.PrevaylerList;
import nl.astraeus.prevayler.PrevaylerModel;

/**
 * Employee: rnentjes
 * Date: 3/24/12
 * Time: 9:59 PM
 */
public class Company extends PrevaylerModel {
    public final static long serialVersionUID = 1L;
    
    private String name;
    private PrevaylerList<Employee> employees = new PrevaylerList<Employee>(Employee.class);

    public Company(String name) {
        this.name = name;
    }

    public PrevaylerList<Employee> getEmployees() {
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
