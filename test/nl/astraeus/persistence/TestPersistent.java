package nl.astraeus.persistence;

import org.junit.Ignore;

/**
 * User: rnentjes
 * Date: 10/24/12
 * Time: 3:54 PM
 */
@Ignore
public class TestPersistent {

    public static class User implements Persistent<String> {
        private static final long serialVersionUID = 2937144352847242360L;

        private String name;
        private String email;

        public User(String name, String email) {
            this.name = name;
            this.email = email;
        }

        @Override
        public String getId() {
            return email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        @Override
        public User clone() throws CloneNotSupportedException {
            return (User)super.clone();
        }

        public int compareTo(Object o) {
            User other = (User)o;

            return email.compareTo(other.getEmail());
        }
    }

    public static class UserDao extends PersistentDao<String, User> { }

    public static void main(String [] args) {
        PersistentManager.begin();

        System.out.println("Creating some users");

        User user = new User("Rien"+System.currentTimeMillis(), "rien@nentjes.com"+System.currentTimeMillis());

        UserDao dao = new UserDao();

        dao.store(user);

        for (User u : dao.findAll()) {
            System.out.println("User: "+u.getName()+" - "+u.getEmail());
        }

        System.out.println("------------------------- Users");

        User user1 = dao.find(user.getEmail());
        User user2 = dao.find(user.getEmail());

        user1.setName("New name 1");
        user2.setName("New name 2");

        dao.store(user1);
        dao.store(user2);

        PersistentManager.commit();

        for (User u : dao.findAll()) {
            System.out.println("User: "+u.getName()+" - "+u.getEmail());
        }
    }
}
