package nl.astraeus.prevayler;

import nl.astraeus.forum.model.*;
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
public class CreateForumsAndTopics {

    private TopicDao topicDao = new TopicDao();
    private CommentDao commentDao = new CommentDao();

    public CreateForumsAndTopics() {
        System.setProperty(PrevaylerStore.SAFEMODE, String.valueOf(true));

        long nano = System.nanoTime();

        new Transaction() {
            @Override
            public void execute() {
                Member member = new MemberDao().find(0, 1).iterator().next();
                for (int i = 0; i < 50; i++) {
                    Topic topic = new Topic(member);
                    topic.setTitle("New topic "+i);

                    for (int j = 0; j < 10; j++) {
                        Comment comment = new Comment(member, "New comment " + j);

                        topic.addComment(comment);

                        commentDao.store(comment);
                    }

                    topicDao.store(topic);
                }
            }
        };

        System.out.println("Transaction took: " + Util.formatNano(System.nanoTime() - nano));
    }

    public static void main(String[] args) {
        new CreateForumsAndTopics();
    }
}
