package edu.virginia.sde.reviews;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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

    @FXML
    private TableView<Review> reviewsTableView;
    @FXML
    private TableColumn<Review, Integer> ratingColumn;
    @FXML
    private TableColumn<Review, String> timestampColumn;
    @FXML
    private TableColumn<Review, String> commentColumn;

    private DatabaseManager database = new DatabaseManager();
    private Stage stage;
    private Course course;
    private List<Review> courseReviews;
    private User user;
    private Review selectedReview;

    // Call this method to initialize the scene with a specific course
    public void initCourseData(Course course, User user) {
        DatabaseManager.initializeHibernate();
        ratingComboBox.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5));
        loadReviews(course);
        loadCourseData(course);
        this.course = course;
        this.user = user;
//        System.out.println(user.toString());
        setupReviewsTable();
        reviewsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        checkAndSetUserReviewStatus(course);

    }
    private void setupReviewsTable() {

//        TODO: Fix timestamp initalliation in table, also just fyi the list view is the old way i used to show data table is new way, will have to delete the listview after table works complety, kept it for now as the listview had less bugs and kinda shows what the table needs to
        // Assuming Review class has methods: getRating, getTimestamp, and getComment
        // Set up the Rating column
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));

        // Set up the Timestamp column
        timestampColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cellData.getValue().getTimestamp()))
        );

        // Set up the Comment column
        commentColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));

        setCenteredCellFactory(ratingColumn);
        setCenteredCellFactory(timestampColumn);
        setCenteredCellFactory(commentColumn);

        reviewsTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedReview = newSelection;
        });
    }
    private <T> void setCenteredCellFactory(TableColumn<Review, T> column) {
        column.setCellFactory(col -> {
            TableCell<Review, T> cell = new TableCell<Review, T>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(item.toString());
                        setAlignment(Pos.CENTER);
                    }
                }
            };
            return cell;
        });
    }


    private void loadCourseData(Course course) {

        // Set course info and average rating
        courseInfoLabel.setText(course.getMnemonic() + " " + course.getCourseNumber() + ": " + course.getCourseTitle());
        var avgRating = courseReviews.stream()
                .mapToInt(Review::getRating)  // Convert Stream<Review> to IntStream
                .average()                    // Calculates the average
                .orElse(0.0);


        double roundedDouble = Math.round(avgRating * 100.0) / 100.0;

        if( avgRating== 0.0){
            averageRatingLabel.setText("Average Rating : " + "None");
        }
        else{
            averageRatingLabel.setText("Average Rating : " + roundedDouble);
        }

    }

    private void loadReviews(Course course) {
        // Load reviews from the database and display them in the ListView
        DatabaseManager.initializeHibernate();
        ObservableList<Review> reviews  = FXCollections.observableArrayList();
//        System.out.println("Course Reviews:");
//        courseReviews = Objects.requireNonNull(DatabaseManager.getAllReviews()).stream().filter(rev -> rev.getCourse().equals(course)).toList();
        courseReviews = Objects.requireNonNull(DatabaseManager.getAllReviews()).stream()
                .filter(rev -> rev.getCourse().equals(course))
                .toList();
        reviews.addAll( courseReviews);
        reviewsListView.setItems(reviews);
        reviewsTableView.setItems(reviews);

        // Setting a cell factory
        reviewsListView.setCellFactory(lv -> new ListCell<Review>() {
            @Override
            protected void updateItem(Review review, boolean empty) {
                super.updateItem(review, empty);
                if (empty || review == null) {
                    setText(null);
                } else {
                    setText(review.getCourse().getMnemonic() + " " + review.getCourse().getCourseNumber() +
                            " - Rating: " + review.getRating() + " Review: " + review.getComment() + " " + review.getTimestamp());
                }
            }
        });
    }

    private void checkAndSetUserReviewStatus(Course course) {
        boolean hasReviewed = DatabaseManager.hasUserReviewedCourse(user.getId(), course.getCourseId());
        if (hasReviewed) {
            // The user has already reviewed this course
            // Find the user's review and set it to the selectedReview variable
            selectedReview = DatabaseManager.getReviewsByUser(user.getId()).stream()
                    .filter(review -> review.getCourse().equals(course))
                    .findFirst()
                    .orElse(null);
            if (selectedReview != null) {
                // Populate the form with the user's review details
                ratingComboBox.setValue(selectedReview.getRating());
                commentTextArea.setText(selectedReview.getComment());
            }
            // Optionally, disable the submit button if you want to prevent users from submitting multiple reviews
            // submitButton.setDisable(true);
        } else {
            // The user has not reviewed this course, enable the form for new submission
            ratingComboBox.setValue(null);
            commentTextArea.clear();
            // submitButton.setDisable(false);
        }
    }


    @FXML
    private void handleSubmitReview() {

        if (selectedReview != null && ratingComboBox.getValue() != null) {
            // Edit existing review
            DatabaseManager.updateReview(selectedReview.getId(), ratingComboBox.getValue(), commentTextArea.getText());
            showAlert("Review updated successfully!");
        } else if (selectedReview == null && ratingComboBox.getValue() != null) {
            // Submit a new review
            submitNewReview();
        } else {
            showAlert("Please select a rating.");
            return;
        }
        // After submission, refresh the reviews list or page as needed
        loadReviews(course);
        refreshPage();
    }


    private void submitNewReview() {
        // Check for valid input
        if (ratingComboBox.getValue() == null || commentTextArea.getText().trim().isEmpty()) {
            showAlert("Please fill in all fields to submit a review.");
            return;
        }
        // Check if the user has already submitted a review
        if (DatabaseManager.hasUserReviewedCourse(user.getUsername(), course.getCourseId())) {
            showAlert("You have already reviewed this course!");
            return;
        }
        // Add the review to the database
        DatabaseManager.addReview(user.getUsername(), course.getCourseId(), ratingComboBox.getValue(), commentTextArea.getText());
        showAlert("Review submitted successfully!");
    }
    @FXML
    private void handleDeleteReview() {
        // Check if a review is actually selected
        if (selectedReview == null) {
            showAlert("No review selected to delete.");
            return;
        }
        // Check if the selected review belongs to the current user
        System.out.println(selectedReview.getUser().toString());
        System.out.println(user.getId());
        if (!selectedReview.getUser().getId().equals(user.getId())) {
            showAlert("You can only delete your own reviews.");
            return;
        }
        // Delete the review from the database
        DatabaseManager.deleteReview(selectedReview.getId());
        showAlert("Review deleted successfully.");
        // Refresh the reviews list or page as needed
        loadReviews(course);
    }


    @FXML
    private void handleBackButton() throws IOException {
        // Logic to return to the Course Search Screen
        try {
            // Load the course search screen

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("course-search.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            // Set the course search scene on the stage
            stage.setScene(scene);
            stage.setTitle("Course Search");
            CourseSearchController controller = fxmlLoader.getController();
            controller.setStage(stage);
            controller.setUser(user);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
//    public void setUser(User user){
//        this.user = user;
//    }
//    public void setCourse(Course course) {
//        this.course = course;
//    }
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    // header navigation //
    @FXML
    protected void handleCourSearchNavAction(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("course-search.fxml"));
            Scene cSearchScene = new Scene(fxmlLoader.load());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(cSearchScene);

            CourseSearchController controller = fxmlLoader.getController();
            controller.setStage(stage);
            controller.setUser(user);
            stage.setTitle("Course Search");

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void refreshPage() {
        try {
            // Load the same FXML file
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("course-reviews.fxml"));
            Parent root = fxmlLoader.load();

            // Create a new scene with the loaded FXML content
            Scene scene = new Scene(root);

            // Set the new scene on the stage
            stage.setScene(scene);
            stage.setTitle("Course Reviews"); // Set your window title here

            // Pass the necessary data to the controller if needed
            CourseReviewsController controller = fxmlLoader.getController();
            controller.setStage(stage);
//            controller.setUser(user);
//            controller.setCourse(course);
            controller.initCourseData(course, user);

            // Reset form fields
            controller.ratingComboBox.getSelectionModel().clearSelection(); // Clear selected rating
            controller.commentTextArea.clear(); // Clear comment text

            // Show the updated scene
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleMyReviewsNavAction(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("my-reviews.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.setTitle("My Reviews");
            stage.show();

            // Assuming MyReviewsController has a method to set the main application reference
            MyReviewsController controller = fxmlLoader.getController();
            controller.setStage(stage);

            controller.initialize(user);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleLoginNavAction(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("initial-login.fxml"));
            Scene logoutScene = new Scene(fxmlLoader.load());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(logoutScene);

            LoginScreenController controller = fxmlLoader.getController();
            controller.setStage(stage);
            controller.setUser(user);
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}