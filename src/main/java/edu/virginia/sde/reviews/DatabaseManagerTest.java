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

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static edu.virginia.sde.reviews.DatabaseManager.*;

public class DatabaseManagerTest {

    private static SessionFactory sessionFactory;

    @BeforeAll
    public static void setUp() {
        DatabaseManager db = new DatabaseManager();
        db.initializeHibernate();
        sessionFactory = db.getSessionFactory();
    }

    @AfterAll
    public static void tearDown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @Test
    public void testAddAndRetrieveUser() {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            // Create a new User instance
            User newUser = new User("testUsername", "testPassword");
            session.save(newUser);
            // Commit the transaction to save the user to the database
            transaction.commit();

            // Clear the session to ensure we fetch from the db, not from the cache
            session.clear();

            // Retrieve the user from the database
            User retrievedUser = session.get(User.class, newUser.getId());

            // Assert that the user was retrieved successfully and check field values
            Assertions.assertNotNull(retrievedUser, "User should be retrieved from the database");
            Assertions.assertEquals("testUsername", retrievedUser.getUsername(), "Username should match the one that was saved");
            Assertions.assertEquals("testPassword", retrievedUser.getPassword(), "Password should match the one that was saved");

        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }
    @Test
    public void testaddNewUser() {
        // Add a new user using the addNewUser method
        addNewUser("newuniqueuser", "testPassword");

        Session session = sessionFactory.openSession();
        try {
            // Clear the session to ensure we fetch from the db, not from the cache
            session.clear();

            // Retrieve the user from the database
            User retrievedUser = session.get(User.class, "newuniqueuser");

            // Assert that the user was retrieved successfully and check field values
            Assertions.assertNotNull(retrievedUser, "User should be retrieved from the database");
            Assertions.assertEquals("newuniqueuser", retrievedUser.getUsername(), "Username should match the one that was saved");
            Assertions.assertEquals("testPassword", retrievedUser.getPassword(), "Password should match the one that was saved");

        } finally {
            session.close();
        }


    }
    @Test
    public void testGetReviewsByUser() {
        // Add users
        DatabaseManager.addNewUser("user1", "password1");
        DatabaseManager.addNewUser("user2", "password2");

        // Add courses
        DatabaseManager.addCourse("2","CS", 101, "Intro to Computer Science", 0.0, new ArrayList<>());
        DatabaseManager.addCourse("3","MATH", 201, "Advanced Mathematics", 0.0, new ArrayList<>());

        // Retrieve users and courses for reference
        User user1 = DatabaseManager.getUserByUsername("user1");
        Course course1 = DatabaseManager.getCourseByMnemonicAndNumber("CS", 101);
        Course course2 = DatabaseManager.getCourseByMnemonicAndNumber("MATH", 201);

        // Add reviews by user1
        LocalDateTime localDateTime1 = LocalDateTime.of(2023, 1, 1, 1, 1, 1, 1_000_000);
        Timestamp timestamp1 = Timestamp.valueOf(localDateTime1);
        LocalDateTime localDateTime2 = LocalDateTime.of(2023, 1, 2, 1, 1, 1, 1_000_000);
        Timestamp timestamp2 = Timestamp.valueOf(localDateTime2);
        DatabaseManager.addReview(user1, course1, 5, timestamp1, "Excellent course");
        DatabaseManager.addReview(user1, course2, 4, timestamp2, "Challenging but rewarding");

        // Fetch reviews by user1
        List<Review> reviews = DatabaseManager.getReviewsByUser(user1.getId());
        Assertions.assertNotNull(reviews, "Review list should not be null");
        Assertions.assertEquals(2, reviews.size(), "User1 should have 2 reviews");

        // Check contents of the reviews
        Review firstReview = reviews.get(0);
        Review secondReview = reviews.get(1);
        Assertions.assertTrue(firstReview.getComment().equals("Excellent course") || secondReview.getComment().equals("Excellent course"), "One review should be 'Excellent course'");
        Assertions.assertTrue(firstReview.getComment().equals("Challenging but rewarding") || secondReview.getComment().equals("Challenging but rewarding"), "One review should be 'Challenging but rewarding'");

    }
    @Test
    public void testtest() {
        List<Course> courses = new ArrayList<>();
        courses = DatabaseManager.getAllCourses();
        for (Course course : courses) {
            System.out.println(course.toString());
        }
    }
    @Test
    public void testLoginFunctionality() {
        // Setup: Create a test user
        String testUsername = "testdeletion";
        String testPassword = "testPasswordsdfd";
        DatabaseManager.addNewUser(testUsername, testPassword);

        // Test: Attempt to login with correct credentials
        boolean loginSuccess = DatabaseManager.login(testUsername, testPassword);
        Assertions.assertTrue(loginSuccess, "Login should succeed with correct credentials");

        // Test: Attempt to login with incorrect credentials
        boolean loginFail = DatabaseManager.login(testUsername, "wrongPassword");
        Assertions.assertFalse(loginFail, "Login should fail with incorrect credentials");

        // Cleanup: Optionally delete the test user after the test
        DatabaseManager.deleteUser(testUsername);
    }
}


    // ... Other tests ...
