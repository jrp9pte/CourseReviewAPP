package edu.virginia.sde.reviews;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;

import java.util.List;

public class DatabaseManager {
    private static SessionFactory sessionFactory;

    // Initialize the SessionFactory instance
    public static void initializeHibernate() {
        try {
            // Create a Configuration instance
            Configuration configuration = new Configuration();

            // Configure Hibernate using the application resource file
            configuration.configure("hibernate.cfg.xml");

            // Add our annotated classes
            configuration.addAnnotatedClass(User.class);
            configuration.addAnnotatedClass(Course.class);
            configuration.addAnnotatedClass(Review.class);

            // Build the ServiceRegistry from the configuration
            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();

            // Create the SessionFactory from the ServiceRegistry
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    // Method to get reviews by user using Hibernate
    public static List<Review> getReviewsByUser(String userId) {
        List<Review> reviews = null;
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Query<Review> query = session.createQuery("FROM Review r WHERE r.user.id = :userId", Review.class);
            query.setParameter("userId", userId);
            reviews = query.getResultList();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
        return reviews;
    }

    // Add other CRUD operations here...

    // Close the SessionFactory
    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
