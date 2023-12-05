package edu.virginia.sde.reviews;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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
import java.util.Objects;

public class MyReviewsController {
    private CourseReviewsApplication mainApp;
    private Stage stage;

    @FXML
    private ListView<Review> reviewsListView;

    @FXML
    private Button backButton;
    private User user;

    // Method to initialize the controller
    public void initialize(User user) {
        loadReviews();
        setupReviewClickListener();
        this.user = user;
    }

    // Method to load reviews into the ListView
    private void loadReviews() {
//        DatabaseManager database = new DatabaseManager();
// Need to change to only get my reviews ( current user's reviews)
        DatabaseManager.initializeHibernate();
        ObservableList<Review> reviews  = FXCollections.observableArrayList();
        reviews.addAll(Objects.requireNonNull(DatabaseManager.getAllReviews()));
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

    // Method to setup click listener for each review in the ListView
    private void setupReviewClickListener() {
        reviewsListView.setOnMouseClicked((MouseEvent event) -> {
            Review selectedReview = reviewsListView.getSelectionModel().getSelectedItem();
            System.out.println("reached load reviews");
            if (selectedReview != null) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CourseReviews.fxml"));
                    Scene scene = new Scene(fxmlLoader.load());
                    stage.setScene(scene);
                    stage.setTitle("Course Reviews");

                    CourseReviewsController controller = fxmlLoader.getController();
                    controller.setStage(stage);
                    controller.initCourseData(selectedReview.getCourse(), user);
//                    controller.setCourse(selectedReview.getCourse());
                    stage.show();

                }  catch (IOException e) {
                    System.out.println("there was an Error!" +  e.getMessage() );
                    throw new RuntimeException(e);
                }
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
            stage.setTitle("Course Search");
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


    // header navigation //
    @FXML
    protected void handleCourReviewsNavAction(ActionEvent event) {
        try {
            Parent cReviewsRoot = FXMLLoader.load(getClass().getResource("course-reviews.fxml"));
            Scene cReviewsScene = new Scene(cReviewsRoot);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(cReviewsScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleCourSearchNavAction(ActionEvent event) {
        try {
            Parent mReviewsRoot = FXMLLoader.load(getClass().getResource("course-search.fxml"));
            Scene mReviewsScene = new Scene(mReviewsRoot);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(mReviewsScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleLoginNavAction(ActionEvent event) {
        try {
            Parent logoutRoot = FXMLLoader.load(getClass().getResource("login-screen.fxml"));
            Scene logoutScene = new Scene(logoutRoot);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(logoutScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
