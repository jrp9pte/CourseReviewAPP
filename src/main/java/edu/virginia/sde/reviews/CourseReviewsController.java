package edu.virginia.sde.reviews;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
        ratingComboBox.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5));
        loadReviews(course);
        loadCourseData(course);
        checkAndSetUserReviewStatus(course);
        setCourse(course);
        database.initializeHibernate();
        this.user = user;
        setupReviewsTable();
        reviewsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }
    private void setupReviewsTable() {
//        TODO: Fix timestamp initalliation in table, also just fyi the list view is the old way i used to show data table is new way, will have to delete the listview after table works complety, kept it for now as the listview had less bugs and kinda shows what the table needs to
        // Assuming Review class has methods: getRating, getTimestamp, and getComment

        // Set up the Rating column
        ratingColumn.setCellValueFactory(review -> new SimpleIntegerProperty(review.getValue().getRating()).asObject());

//
//        // Set up the Timestamp column
//        timestampColumn.setCellValueFactory(cellData ->
//                new SimpleStringProperty(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cellData.getValue().getTimestamp()))
//        );
//
//        // Set up the Comment column
//        commentColumn.setCellValueFactory(review -> new PropertyValueFactory<>(review.getComment()));
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
        if( avgRating== 0.0){
            averageRatingLabel.setText("Average Rating : " + "None");
        }
        else{
            averageRatingLabel.setText("Average Rating : " + avgRating);
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
//        TODO: Old approach was to diable the form if course is already reviewed but it makes more sense to just use that same for to poteiallty edit a review as well.
//        Old:
                    // Check if the user has already reviewed the course
                    // Enable or disable the review form accordingly
//        New: can either delete this function of use as helper function to determine if user can sumbit a new review or not.
    }

    @FXML
    private void handleSubmitReview() {
        // TODO: Implement logic for submitting/editing a review. The user clicks a review to edit it, The comment and rating box should propagte with the reviews details, Refresh Page after
        // Logic to submit/edit review in the database
//        if( user has already reviewed ){
//          deleted selected review
//        }
        submitNewReview(); // use current values
    }

    private void submitNewReview(){
// TODO: Ensure alerts are shown if user tries to sumbit an invalid review or another review if there already sent one, refresh page after.
        // Logic to submit/edit review in the database
//        let database chooose the id
        if (ratingComboBox.getValue() == null) {
            showAlert("Please select a rating.");
            return;
        }
//        Need to check if user has reviewed already as well
//        if (user has reviewed already) {
//            showAlert("You have reviewed this course already!");
//            return;
//        }
//        Review newReview = new Review(0, ratingComboBox.getValue() , new Timestamp(System.currentTimeMillis()), commentTextArea.getText(), user, course  );
        DatabaseManager.addReview(user.getUsername(), course.getCourseId(),ratingComboBox.getValue(),commentTextArea.getText()  );
    }

    @FXML
    private void handleDeleteReview() {
//        TODO: implememt deletion, be sure to send and potential error messages and be sure to only allow deletion if the selcted reveiw is the user's
        // Logic to delete the user's review from the database
        System.out.println("this was selected to be deleted" + selectedReview.getCourse() + selectedReview.getComment());
    }

    @FXML
    private void handleBackButton() {
//         Logic to return to the Course Search Screen
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

    public void setCourse(Course course) {
        this.course = course;
    }
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}