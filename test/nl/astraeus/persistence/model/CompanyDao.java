package nl.astraeus.persistence.model;

import nl.astraeus.persistence.Filter;
import nl.astraeus.persistence.SimpleDao;
import org.junit.Ignore;

import java.util.Collection;

/**
 * Employee: rnentjes
 * Date: 3/24/12
 * Time: 10:03 PM
 */
@Ignore
public class CompanyDao extends SimpleDao<Company> {
    
    public Collection<Company> findByEmpoyeeName(final String name) {
        return filter(new Filter<Company>() {
            @Override
            public boolean include(Company model) {
                if (model.getEmployees() != null) {
                    for (Employee e : model.getEmployees()) {
                        if (e.getName().contains(name)) {
                            return true;
                        }
                    }
                }

                return false;
            }
        });
    }

}
