package edu.virginia.sde.reviews;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    @BeforeEach
    public void setUpEach() {
        DatabaseManager.clearDatabase();
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

        // Ensure users and courses are unique for this test
        String uniqueUser1 = "user1_" + System.currentTimeMillis();
        String uniqueUser2 = "user2_" + System.currentTimeMillis();

        // Add users
        DatabaseManager.addNewUser(uniqueUser1, "password1");
        DatabaseManager.addNewUser(uniqueUser2, "password2");

        // Add courses
        DatabaseManager.addCourse("CS", 101, "Intro to Computer Science", 0.0, new ArrayList<>());
        DatabaseManager.addCourse("MATH", 201, "Advanced Mathematics", 0.0, new ArrayList<>());

        // Retrieve users and courses for reference
        User user1 = DatabaseManager.getUserByUsername(uniqueUser1);
        Course course1 = DatabaseManager.getCourseByMnemonicAndNumber("CS", 101).get(0);
        Course course2 = DatabaseManager.getCourseByMnemonicAndNumber("MATH", 201).get(0);

        // Add reviews
        DatabaseManager.addReview(uniqueUser1, course1.getCourseId(), 5, "Excellent course");
        DatabaseManager.addReview(uniqueUser1, course2.getCourseId(), 4, "Challenging but rewarding");

        // Fetch reviews by user1
        List<Review> reviews = DatabaseManager.getReviewsByUser(user1.getId());
        Assertions.assertNotNull(reviews, "Review list should not be null");
        Assertions.assertEquals(2, reviews.size(), "User1 should have 2 reviews");

        // Check contents of the reviews
        boolean foundExcellentCourse = false;
        boolean foundChallengingButRewarding = false;
        for (Review review : reviews) {
            if ("Excellent course".equals(review.getComment())) {
                foundExcellentCourse = true;
            }
            if ("Challenging but rewarding".equals(review.getComment())) {
                foundChallengingButRewarding = true;
            }
        }
        Assertions.assertTrue(foundExcellentCourse, "One review should be 'Excellent course'");
        Assertions.assertTrue(foundChallengingButRewarding, "One review should be 'Challenging but rewarding'");
    }


    @Test
    public void testtest() {
        List<Review> reviews = DatabaseManager.getAllReviews();
        for (Review review : reviews) {
            System.out.println(review.toString());
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
    @Test
    public void testUpdateReview() {
        // Ensure the database is clean
        DatabaseManager.clearDatabase();

        // Arrange: Add a user and a course
        String username = "testUser";
        String password = "password123";
        DatabaseManager.addNewUser(username, password);

        String mnemonic = "CS";
        int courseNumber = 101;
        String courseTitle = "Intro to Computer Science";
        DatabaseManager.addCourse(mnemonic, courseNumber, courseTitle, 0.0, new ArrayList<>());

        // Retrieve the course to get its ID
        List<Course> courses = DatabaseManager.getAllCourses();
        Assertions.assertFalse(courses.isEmpty(), "Courses should not be empty");
        Course addedCourse = courses.get(0);
        String courseId = addedCourse.getCourseId(); // Get the ID of the added course

        // Add a review
        int originalRating = 3;
        String originalComment = "Good course";
        DatabaseManager.addReview(username, courseId, originalRating, originalComment);

        // Retrieve the added review to get its ID
        List<Review> reviews = DatabaseManager.getReviewsByUser(username);
        Assertions.assertFalse(reviews.isEmpty(), "Reviews should not be empty");
        Review addedReview = reviews.get(0);
        int reviewId = addedReview.getId(); // Get the ID of the added review

        // Act: Update the review
        int newRating = 4;
        String newComment = "Great course";
        DatabaseManager.updateReview(reviewId, newRating, newComment);

        // Assert: Retrieve the review and check if it's updated
        Review updatedReview = DatabaseManager.getReviewById(reviewId);
        Assertions.assertEquals(newRating, updatedReview.getRating(), "Rating should be updated");
        Assertions.assertEquals(newComment, updatedReview.getComment(), "Comment should be updated");
    }
    @Test
    public void testGetAllUsers() {
        // Setup: Add some users
        DatabaseManager.addNewUser("user1", "password1");
        DatabaseManager.addNewUser("user2", "password2");

        // Act: Retrieve all users
        List<User> users = DatabaseManager.getAllUsers();

        // Assert: Check if users are retrieved
        Assertions.assertFalse(users.isEmpty(), "Users list should not be empty");
        Assertions.assertTrue(users.stream().anyMatch(user -> "user1".equals(user.getUsername())), "User1 should be present");
        Assertions.assertTrue(users.stream().anyMatch(user -> "user2".equals(user.getUsername())), "User2 should be present");
    }
    @Test
    public void testUpdateUser() {
        // Arrange: Add a user
        String username = "updateUser";
        String originalPassword = "originalPass";
        DatabaseManager.addNewUser(username, originalPassword);

        // Act: Update the user's password
        String newPassword = "newPass";
        DatabaseManager.updateUser(username, newPassword);

        // Assert: Check if the user's password is updated
        boolean loginSuccess = DatabaseManager.login(username, newPassword);
        Assertions.assertTrue(loginSuccess, "User's password should be updated");
    }

    @Test
    public void testDeleteUser() {
        // Arrange: Add a user
        String username = "deleteUser";
        DatabaseManager.addNewUser(username, "password");

        // Act: Delete the user
        DatabaseManager.deleteUser(username);

        // Assert: Check if the user is deleted
        boolean userExists = DatabaseManager.userExists(username);
        Assertions.assertFalse(userExists, "User should be deleted");
    }
    @Test
    public void testGetAllCourses() {
        // Setup: Add some courses
        DatabaseManager.addCourse("CS", 101, "Intro to CS", 0.0, new ArrayList<>());
        DatabaseManager.addCourse("MATH", 201, "Advanced Math", 0.0, new ArrayList<>());

        // Act: Retrieve all courses
        List<Course> courses = DatabaseManager.getAllCourses();

        // Assert: Check if courses are retrieved
        Assertions.assertFalse(courses.isEmpty(), "Courses list should not be empty");
        Assertions.assertTrue(courses.stream().anyMatch(course -> "CS".equals(course.getMnemonic()) && course.getCourseNumber() == 101), "CS 101 should be present");
        Assertions.assertTrue(courses.stream().anyMatch(course -> "MATH".equals(course.getMnemonic()) && course.getCourseNumber() == 201), "MATH 201 should be present");
    }
    @Test
    public void testUpdateCourse() {
        // Arrange: Add a course
        String mnemonic = "CS";
        int courseNumber = 101;
        String originalTitle = "Original Title";
        DatabaseManager.addCourse(mnemonic, courseNumber, originalTitle, 0.0, new ArrayList<>());

        // Retrieve the course to get its ID
        List<Course> courses = DatabaseManager.getCourseByMnemonicAndNumber(mnemonic, courseNumber);
        Assertions.assertFalse(courses.isEmpty(), "Courses should not be empty");
        Course addedCourse = courses.get(0);
        String courseId = addedCourse.getCourseId(); // Assuming courseId is an integer

        // Act: Update the course title
        String newTitle = "New Title";
        boolean isUpdated = DatabaseManager.updateCourse(courseId, newTitle);
        Assertions.assertTrue(isUpdated, "Course update should be successful");

        // Assert: Check if the course title is updated
        Course updatedCourse = DatabaseManager.getCourseByMnemonicAndNumber(mnemonic, courseNumber).get(0);
        Assertions.assertEquals(newTitle, updatedCourse.getCourseTitle(), "Course title should be updated");
    }










    @Test
    public void testClearDatabase() {
        // Setup: Add some entities
        DatabaseManager.addNewUser("testUser", "password");
        DatabaseManager.addCourse("CS", 101, "Intro to Computer Science", 5.0, new ArrayList<>());
        DatabaseManager.addReview("testUser", "CS101", 5, "Great course");

        // Act: Clear the database
        DatabaseManager.clearDatabase();

        // Assert: Check if all tables are empty
        List<User> users = DatabaseManager.getAllUsers();
        List<Course> courses = DatabaseManager.getAllCourses();
        List<Review> reviews = DatabaseManager.getAllReviews();

        Assertions.assertTrue(users.isEmpty(), "Users table should be empty");
        Assertions.assertTrue(courses.isEmpty(), "Courses table should be empty");
        Assertions.assertTrue(reviews.isEmpty(), "Reviews table should be empty");
    }
    @Test
    public void testHasUserReviewedCourse() {
        // Ensure the database is clean
        DatabaseManager.clearDatabase();

        // Arrange: Add a user and a course
        String username = "testUser";
        String password = "password123";
        DatabaseManager.addNewUser(username, password);

        String mnemonic = "CS";
        int courseNumber = 101;
        String courseTitle = "Intro to Computer Science";
        DatabaseManager.addCourse(mnemonic, courseNumber, courseTitle, 0.0, new ArrayList<>());

        // Retrieve the course to get its ID
        List<Course> courses = DatabaseManager.getAllCourses();
        Assertions.assertFalse(courses.isEmpty(), "Courses should not be empty");
        Course addedCourse = courses.get(0);
        String courseId = addedCourse.getCourseId(); // Get the ID of the added course

        // Act: Add a review
        DatabaseManager.addReview(username, courseId, 5, "Excellent course");

        // Assert: Check if the user has reviewed the course
        boolean result = DatabaseManager.hasUserReviewedCourse(username, courseId);
        Assertions.assertTrue(result, "User should have reviewed the course");

        // Act & Assert: Check for a course the user hasn't reviewed
        String otherCourseId = "CS102"; // Ensure this is a different course ID
        result = DatabaseManager.hasUserReviewedCourse(username, otherCourseId);
        Assertions.assertFalse(result, "User should not have reviewed this other course");
    }

    @Test
    public void testDeleteReview() {
        // Arrange: Add a review to delete
        String username = "testUser";
        String courseId = "CS101";
        DatabaseManager.addReview(username, courseId, 5, "Excellent course");

        // Act: Delete the review
        int reviewId = 1; // assuming this is the ID of the review added above
        DatabaseManager.deleteReview(reviewId);

        // Assert: Check if the review is deleted
        Review deletedReview = DatabaseManager.getReviewById(reviewId);
        Assertions.assertNull(deletedReview, "Review should be deleted");
    }


}


    // ... Other tests ...
