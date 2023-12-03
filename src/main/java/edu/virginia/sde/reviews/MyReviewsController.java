package edu.virginia.sde.reviews;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.input.MouseEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.IOException;

public class MyReviewsController {
    private HelloWorldApplication mainApp;

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
        ObservableList<Review> reviews = FXCollections.observableArrayList(
                new Review("CS", 101, 4, "Great introduction to computer science!"),
                new Review("HIST", 202, 5, "In-depth and fascinating historical perspectives."),
                new Review("MATH", 303, 3, "Challenging course but very rewarding."),
                new Review("BIO", 404, 2, "Course was too fast-paced and lacked detailed explanations.")
        );
        // TODO: Populate 'reviews' with the database

        reviewsListView.setItems(reviews);

        // Setting a cell factory
        reviewsListView.setCellFactory(lv -> new ListCell<Review>() {
            @Override
            protected void updateItem(Review review, boolean empty) {
                super.updateItem(review, empty);
                if (empty || review == null) {
                    setText(null);
                } else {
                    setText(review.getCourseMnemonic() + " " + review.getCourseNumber() +
                            " - Rating: " + review.getRating() + " Review: " + review.getReview());
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
        try {
            mainApp.loadHelloWorldScene();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exceptions
        }
    }


    public void setMainApp(HelloWorldApplication mainApp) {
        this.mainApp = mainApp;
    }
}
