package nl.astraeus.forum.web.page;

import de.svenjacobs.loremipsum.LoremIpsum;
import nl.astraeus.forum.model.*;
import nl.astraeus.prevayler.PrevaylerStore;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * User: rnentjes
 * Date: 3/28/12
 * Time: 3:23 PM
 */
public class Diagnostics extends Page {

    private MemberDao memberDao = new MemberDao();
    private TopicDao topicDao = new TopicDao();
    private CommentDao commentDao = new CommentDao();
    private LoremIpsum loremIpsum = new LoremIpsum();
    private Random random = new Random(System.nanoTime());

    @Override
    public Page processRequest(HttpServletRequest request) {
        Page result = this;

        if ("gc".equals(request.getParameter("action"))) {
            System.gc();
        } else if ("generatetopics".equals(request.getParameter("action"))) {
            int topicCounter = topicDao.size() + 1;
            int commentCounter = commentDao.size() + 1;
            int memberCounter = memberDao.size() + 1;

            for (int i = 0; i < 10; i++) {
                Member mem = new Member("Member " + (memberCounter++), "", "");

                memberDao.store(mem);
            }

            for (int i = 0; i < 50; i++) {
                Member member = memberDao.findRandom();

                if (member != null) {
                    Topic topic = new Topic(member);

                    topic.setTitle("New topic " + topicCounter++);
                    member.addTopic(topic);

                    topicDao.store(topic);
                    memberDao.store(member);
                    for (int j = 0; j < 10; j++) {
                        member = memberDao.findRandom();

                        if (member != null) {
                            Comment comment = new Comment(member, loremIpsum.getParagraphs(random.nextInt(3)+1));

                            topic.addComment(comment);
                            member.addComment(comment);

                            commentDao.store(comment);
                            memberDao.store(member);
                        }
                    }
                }
            }
        }


        return result;
    }

    @Override
    public Map<String, Object> defineModel(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put("objectMap", PrevaylerStore.get().getObjectTypeMap().entrySet());

        result.put("userMemory", ((Runtime.getRuntime().totalMemory() / (1024 * 1024)) - (Runtime.getRuntime().freeMemory() / (1024 * 1024))));
        result.put("freeMemory", (Runtime.getRuntime().freeMemory() / (1024 * 1024)));
        result.put("totalMemory", (Runtime.getRuntime().totalMemory() / (1024 * 1024)));
        result.put("maxMemory", (Runtime.getRuntime().maxMemory() / (1024 * 1024)));


        return result;
    }


}
