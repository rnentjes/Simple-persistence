package nl.astraeus.prevayler.model;

import nl.astraeus.prevayler.Filter;
import nl.astraeus.prevayler.PrevaylerDao;
import org.junit.Ignore;

import java.util.Collection;

/**
 * Employee: rnentjes
 * Date: 3/24/12
 * Time: 10:03 PM
 */
@Ignore
public class CompanyDao extends PrevaylerDao<Company> {
    
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
