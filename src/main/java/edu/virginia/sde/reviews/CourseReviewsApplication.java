package edu.virginia.sde.reviews;

import javafx.application.Application;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class CourseReviewsApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Initialize your JavaFX application
        // For example, set up the primary stage and show it

        // Optionally, perform any Hibernate initialization here
        // Or defer it to a later part of your application
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Example method for Hibernate operations
    public static void addNewUser() {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        try {
            User newUser = new User("username", "password");
            session.save(newUser);
            transaction.commit();
            System.out.println("User added successfully");
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
            sessionFactory.close();
        }
    }
}
