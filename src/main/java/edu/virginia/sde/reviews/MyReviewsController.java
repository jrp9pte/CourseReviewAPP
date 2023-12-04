package edu.virginia.sde.reviews;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.input.MouseEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.io.IOException;

public class MyReviewsController {
    private CourseReviewsApplication mainApp;
    private Stage stage;

    @FXML
    private ListView<Review> reviewsListView;

    @FXML
    private Button backButton;

    // Method to initialize the controller
    public void initialize() {
        loadReviews();
        setupReviewClickListener();
    }

    // Method to load reviews into the ListView
    private void loadReviews() {
        DatabaseManager database = new DatabaseManager();
        database.initializeHibernate();
        ObservableList<Review> reviews  = FXCollections.observableArrayList();
        database.getAllReviews().forEach(review -> reviews.add(review));
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
                            " - Rating: " + review.getRating() + " Review: " + review.getRating());
                }
            }
        });
    }

    // Method to setup click listener for each review in the ListView
    private void setupReviewClickListener() {
        reviewsListView.setOnMouseClicked((MouseEvent event) -> {
            Review selectedReview = reviewsListView.getSelectionModel().getSelectedItem();
            if (selectedReview != null) {
                // TODO: Handle the click on the review.
            }
        });
    }

    // Event handler for the back button
    @FXML
    private void handleBackButton() throws IOException {
        // Logic to return to the Course Search Screen
        try {
            // Load the course search screen
            Parent courseSearchRoot = FXMLLoader.load(getClass().getResource("course-search.fxml"));
            Scene courseSearchScene = new Scene(courseSearchRoot);

            // Set the course search scene on the stage
            stage.setScene(courseSearchScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//    public void setMainApp(HelloWorldApplication mainApp) {
//        this.mainApp = mainApp;
//    }
    public void setStage(Stage stage){
        this.stage = stage;
    }
}
