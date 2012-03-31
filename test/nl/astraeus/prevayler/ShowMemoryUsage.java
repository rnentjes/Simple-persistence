package nl.astraeus.prevayler;

import nl.astraeus.prevayler.example.forum.model.CommentDao;
import nl.astraeus.prevayler.example.forum.model.TopicDao;
import nl.astraeus.prevayler.model.Company;
import nl.astraeus.prevayler.model.CompanyDao;
import nl.astraeus.prevayler.model.Employee;
import nl.astraeus.prevayler.model.EmployeeDao;
import nl.astraeus.util.Util;
import org.junit.Ignore;

/**
 * User: rnentjes
 * Date: 3/24/12
 * Time: 9:58 PM
 */
@Ignore
public class ShowMemoryUsage {
    private TopicDao topicDao = new TopicDao();
    private CommentDao commentDao = new CommentDao();
    private CompanyDao companyDao = new CompanyDao();
    private EmployeeDao employeeDao = new EmployeeDao();

    public ShowMemoryUsage() {
        Util.printMemoryUsage();

        long nano = System.nanoTime();

        System.out.println("Loaded " + companyDao.size() + " companies");
        System.out.println("Loaded " + employeeDao.size() + " employees");
        System.out.println("Loaded " + topicDao.size() + " topics");
        System.out.println("Loaded " + commentDao.size() + " comments");

        System.out.println("Loading data took: "+Util.formatNano(System.nanoTime() - nano));

        System.gc();
        Util.printMemoryUsage();
    }

    public static void main(String [] args) {
        new ShowMemoryUsage();
    }
}
