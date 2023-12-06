package edu.virginia.sde.reviews;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;
import org.junit.jupiter.api.BeforeAll;
import edu.virginia.sde.reviews.User;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

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
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    // Login method to check if the username and password match for an existing user
    public static boolean login(String username, String password) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.createQuery("FROM User U WHERE U.username = :username", User.class)
                    .setParameter("username", username)
                    .uniqueResult();

            if (user != null && user.getPassword().equals(password)) {
                return true; // Login successful
            } else {
                return false; // Login failed
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Handling exceptions
        }
    }

    // Create new user

    public static String createNewUser(String username, String password) {
        if (username == null || password == null) {
            return "Username and password cannot be null.";
        }
        // Initialize the SessionFactory instance
        if (userExists(username)) {
            return "Username already exists.";
        }

        if (password.length() < 8) {
            return "Password must be at least 8 characters long.";
        }

        addNewUser(username, password);
        return "User created successfully.";
    }

    // Method to get reviews by user using Hibernate
    public static List<Review> getReviewsByUser(String userId) {
        List<Review> reviews = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            Query<Review> query = session.createQuery("FROM Review r WHERE r.user.id = :userId", Review.class);
            query.setParameter("userId", userId);
            reviews = query.getResultList();
            // Initialize any lazy-loaded collections if necessary
            for (Review review : reviews) {
                // Assuming Review has lazy-loaded properties that need to be accessed
                Hibernate.initialize(review.getUser()); // if User is a lazy-loaded relationship
                // Similarly, initialize other lazy-loaded properties if required
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reviews;
    }
    public static Review getReviewById(int reviewId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Review r WHERE r.id = :reviewId", Review.class)
                    .setParameter("reviewId", reviewId)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void clearDatabase() {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            // Delete all entries in a specific order due to dependencies
            session.createQuery("DELETE FROM Review").executeUpdate();
            session.createQuery("DELETE FROM Course").executeUpdate();
            session.createQuery("DELETE FROM User").executeUpdate();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public static boolean userExists(String username) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("FROM User U WHERE U.username = :username", User.class);
            query.setParameter("username", username);
            return !query.getResultList().isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static void addNewUser(String username, String password) {
        if (userExists(username)) {
            System.out.println("User with username " + username + " already exists.");
            return;
        }

        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            User newUser = new User(username, password);
            session.save(newUser);

            transaction.commit();
            System.out.println("User added successfully");
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }
    public static List<Course> getAllCourses() {
        try (Session session = sessionFactory.openSession()) {
            List<Course> courses = session.createQuery("FROM Course", Course.class).list();
            return courses;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Review> getAllReviews() {
        try (Session session = sessionFactory.openSession()) {
            List<Review> reviews = session.createQuery("FROM Review", Review.class).list();
            return reviews;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void updateUser(String username, String newPassword) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            User user = session.get(User.class, username);
            if (user != null) {
                user.setPassword(newPassword);
                session.update(user);
                transaction.commit();
                System.out.println("User updated successfully");
            } else {
                System.out.println("User not found with username: " + username);
            }
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }

    }
    public static void deleteUser(String username) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            User user = session.get(User.class, username);
            if (user != null) {
                session.delete(user);
                transaction.commit();
                System.out.println("User deleted successfully");
            } else {
                System.out.println("User not found with username: " + username);
            }
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }
    public static List<User> getAllUsers() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM User", User.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static boolean updateCourse(String courseId, String newTitle) {
        Transaction transaction = null;
        boolean updateSuccessful = false;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Course course = session.get(Course.class, courseId);

            if (course != null) {
                course.setCourseTitle(newTitle);
                session.update(course); // This is optional
                transaction.commit();
                updateSuccessful = true;
                System.out.println("Course updated successfully");
            } else {
                System.out.println("Course not found with courseId: " + courseId);
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return updateSuccessful;
    }


    public static void addCourse(String mnemonic, int courseNumber, String courseTitle, double courseRating, List<Review> reviews) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            //Course newCourse = new Course(null, mnemonic, courseNumber, courseTitle, courseRating, reviews);
              Course newCourse = new Course(mnemonic.toUpperCase(), courseNumber, courseTitle, courseRating, reviews);

            session.save(newCourse);

            transaction.commit();

            System.out.println("Course added successfully");
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
    }

    public String getcourseId(String mnemonic, int courseNumber, String courseTitle) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("SELECT C.courseId FROM Course C WHERE lower(C.mnemonic) = lower(:mnemonic) AND C.courseNumber = :number AND lower(C.courseTitle) like lower(:title)", String.class)
                    .setParameter("mnemonic", mnemonic)
                    .setParameter("number", courseNumber)
                    .setParameter("title", courseTitle)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Course getCourseById(String courseId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Course C WHERE C.courseId = :courseId", Course.class)
                    .setParameter("courseId", courseId)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static User getUserByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM User U WHERE U.username = :username", User.class)
                    .setParameter("username", username)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Course> getCourseByMnemonicAndNumberAndTitle(String mnemonic, int number, String title) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Course C WHERE C.mnemonic = :mnemonic AND C.courseNumber = :number AND C.courseTitle = :title", Course.class)
                    .setParameter("mnemonic", mnemonic)
                    .setParameter("number", number)
                    .setParameter("title", title)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Course> getCourseByMnemonicAndNumberAndTitleContains(String mnemonic, int number, String title) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Course C WHERE lower(C.mnemonic) = lower(:mnemonic) AND C.courseNumber = :number AND lower(C.courseTitle) like lower(:title)", Course.class)
                    .setParameter("mnemonic", mnemonic)
                    .setParameter("number", number)
                    .setParameter("title", "%" + title + "%")
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static List<Course> getCourseByMnemonicAndNumber(String mnemonic, int number) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Course C WHERE lower(C.mnemonic) = lower(:mnemonic) AND C.courseNumber = :number", Course.class)
                    .setParameter("mnemonic", mnemonic)
                    .setParameter("number", number)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Course> getCourseByTitleAndNumber(String title, int number) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Course C WHERE lower(C.courseTitle) = lower(:title) AND C.courseNumber = :number", Course.class)
                    .setParameter("title", title)
                    .setParameter("number", number)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Course> getCourseByTitleContainsAndNumber(String title, int number) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Course C WHERE lower(C.courseTitle) like lower(:title) AND C.courseNumber = :number", Course.class)
                    .setParameter("title", "%" + title + "%")
                    .setParameter("number", number)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Course> getCourseByMnemonicAndTitle(String mnemonic, String title) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Course C WHERE lower(C.mnemonic) = lower(:mnemonic) AND lower(C.courseTitle) = lower(:title)", Course.class)
                    .setParameter("mnemonic", mnemonic)
                    .setParameter("title", title)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static List<Course> getCourseByMnemonicAndTitleContains(String mnemonic, String title) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Course C WHERE lower(C.mnemonic) = lower(:mnemonic) AND lower(C.courseTitle) like lower(:title)", Course.class)
                    .setParameter("mnemonic", mnemonic)
                    .setParameter("title", "%" + title + "%")
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Course> getCourseByMnemonic(String mnemonic) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Course C WHERE lower(C.mnemonic) = lower(:mnemonic)", Course.class)
                    .setParameter("mnemonic", mnemonic)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Course> getCourseByNumber(int number) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Course C WHERE C.courseNumber = :number", Course.class)
                    .setParameter("number", number)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Course> getCourseByTitle(String title) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Course C WHERE lower(C.courseTitle) = lower(:title)", Course.class)
                    .setParameter("title", title)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Course> getCourseByTitleContains(String title) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Course C WHERE lower(C.courseTitle) like lower(:title)", Course.class)
                    .setParameter("title", "%" + title + "%")
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void addReview(String username, String courseId, int rating, String comment) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            // Fetch the user and course from the database using their IDs
            User user = session.get(User.class, username);
            Course course = session.get(Course.class, courseId);

            // Check if user and course exist
            if (user == null || course == null) {
                throw new IllegalArgumentException("User or Course not found.");
            }

            // Create a new Review instance
            Review newReview = new Review();
            newReview.setRating(rating);
            newReview.setTimestamp(new Timestamp(System.currentTimeMillis()));
            newReview.setComment(comment);
            newReview.setUser(user);
            newReview.setCourse(course);

            // Save the new review
            session.save(newReview);

            // Commit the transaction
            transaction.commit();
            System.out.println("Review added successfully");
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            System.out.println("Failed to add review: " + e.getMessage());
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    public static void updateReview(int reviewId, int newRating, String newComment) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            Review review = session.get(Review.class, reviewId);
            if (review != null) {
                review.setRating(newRating);
                review.setComment(newComment);
                session.update(review);
                transaction.commit();
                System.out.println("Review updated successfully");
            } else {
                System.out.println("Review not found with id: " + reviewId);
            }
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }
    public static void deleteReview(int reviewId) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            Review review = session.get(Review.class, reviewId);
            if (review != null) {
                session.delete(review);
                transaction.commit();
                System.out.println("Review deleted successfully");
            } else {
                System.out.println("Review not found with id: " + reviewId);
            }
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }
    public static boolean hasUserReviewedCourse(String userId, String courseId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Review> query = session.createQuery("FROM Review r WHERE r.user.id = :userId AND r.course.courseId = :courseId", Review.class);
            query.setParameter("userId", userId);
            query.setParameter("courseId", courseId);
            List<Review> reviews = query.getResultList();
            return !reviews.isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }









    // Call this method when closing the application gracefully closes database
    public void closeApplication() {
        shutdown();
        System.exit(0);

    }

    // Close the SessionFactory
    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
