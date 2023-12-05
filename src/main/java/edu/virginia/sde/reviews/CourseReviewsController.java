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
        System.out.println("this was selected to be deleted" + selectedReview.getCourse() + selectedReview.getComment());
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


    // header navigation //
    @FXML
    protected void handleCourSearchNavAction(ActionEvent event) {
        try {
            Parent cSearchRoot = FXMLLoader.load(getClass().getResource("course-search.fxml"));
            Scene cSearchScene = new Scene(cSearchRoot);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(cSearchScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleMyReviewsNavAction(ActionEvent event) {
        try {
            Parent mReviewsRoot = FXMLLoader.load(getClass().getResource("my-reviews.fxml"));
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
            Parent logoutRoot = FXMLLoader.load(getClass().getResource("initial-login.fxml"));
            Scene logoutScene = new Scene(logoutRoot);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(logoutScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}