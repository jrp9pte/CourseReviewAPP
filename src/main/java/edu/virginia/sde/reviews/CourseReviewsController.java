package edu.virginia.sde.reviews;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class CourseReviewsController {
    private CourseReviewsApplication mainApp;

    @FXML
    private Label courseInfoLabel;
    @FXML
    private Label averageRatingLabel;
    @FXML
    private ListView<Review> reviewsListView;
    @FXML
    private ComboBox<Integer> ratingComboBox;
    @FXML
    private TextArea commentTextArea;

    private DatabaseManager database = new DatabaseManager();
    private Stage stage;
    private Course course;
    private List<Review> courseReviews;

    // Call this method to initialize the scene with a specific course
    public void initCourseData(Course course) {
        ratingComboBox.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5));
        loadReviews(course);
        loadCourseData(course);
        checkAndSetUserReviewStatus(course);
        setCourse(course);
        database.initializeHibernate();
    }

    private void loadCourseData(Course course) {
        // Set course info and average rating
        courseInfoLabel.setText(course.getMnemonic() + " " + course.getCourseNumber() + ": " + course.getCourseTitle());
        var avgRating = courseReviews.stream()
                .mapToInt(Review::getRating)  // Convert Stream<Review> to IntStream
                .average()                    // Calculates the average
                .orElse(0.0);
        averageRatingLabel.setText("Average Rating : " + avgRating);

    }

    private void loadReviews(Course course) {
        // Load reviews from the database and display them in the ListView
        DatabaseManager.initializeHibernate();
        ObservableList<Review> reviews  = FXCollections.observableArrayList();
//        System.out.println();
//        courseReviews = Objects.requireNonNull(DatabaseManager.getAllReviews()).stream().filter(rev -> rev.getCourse().equals(course)).toList();
        courseReviews = Objects.requireNonNull(DatabaseManager.getAllReviews()).stream()
                .filter(rev -> rev.getCourse().equals(course))
                .toList();
        reviews.addAll( courseReviews);
        reviewsListView.setItems(reviews);

        // Setting a cell factory
        reviewsListView.setCellFactory(lv -> new ListCell<Review>() {
            @Override
            protected void updateItem(Review review, boolean empty) {
                super.updateItem(review, empty);
                if (empty || review == null) {
                    setText(null);
                } else {
                    setText(review.getCourse().getMnemonic() + " " + review.getCourse().getCourseNumber() +
                            " - Rating: " + review.getRating() + " Review: " + review.getComment());
                }
            }
        });
    }

    private void checkAndSetUserReviewStatus(Course course) {
        // Check if the user has already reviewed the course
        // Enable or disable the review form accordingly
    }

    @FXML
    private void handleSubmitReview() {
        // Logic to submit/edit review in the database
    }

    @FXML
    private void handleDeleteReview() {
        // Logic to delete the user's review from the database

    }

    @FXML
    private void handleBackButton() {
//         Logic to return to the Course Search Screen
        try {
            // Load the course search screen
            Parent courseSearchRoot = FXMLLoader.load(getClass().getResource("course-search.fxml"));
            Scene courseSearchScene = new Scene(courseSearchRoot);

            // Set the course search scene on the stage
            stage.setScene(courseSearchScene);
            stage.setTitle("Course Search");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

}