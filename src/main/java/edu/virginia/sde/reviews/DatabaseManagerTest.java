package edu.virginia.sde.reviews;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class DatabaseManagerTest {

    private static SessionFactory sessionFactory;

    @BeforeAll
    public static void setUp() {
        // Configure based on the hibernate.cfg.xml
        Configuration configuration = new Configuration().configure();
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Course.class);
        configuration.addAnnotatedClass(Review.class);

        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties()).build();
        sessionFactory = configuration.buildSessionFactory(serviceRegistry);
    }

    @AfterAll
    public static void tearDown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @Test
    public void testSessionFactoryInitialization() {
        Assertions.assertNotNull(sessionFactory, "SessionFactory should be initialized");
    }

    @Test
    public void testBasicOperations() {
        // Use this method to test basic database operations like create, read, update, delete
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            // Create operation
            User user = new User("testUser", "testPassword");
            session.save(user);

            // Read operation
            List<User> userList = session.createQuery("FROM User", User.class).list();
            Assertions.assertFalse(userList.isEmpty(), "User list should not be empty after saving a user");

            // Update operation
            user.setPassword("updatedPassword");
            session.update(user);

            // Delete operation
            session.delete(user);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }
}
