package edu.virginia.sde.reviews;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.collections.ObservableList;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CourseSearchController {
    @FXML
    private TextField CourseMnemonic;
    @FXML
    private TextField CourseNumber;
    @FXML
    private TextField CourseTitle;
    @FXML
    private ListView<String> CourseSearchList;
    private String courseMnemonic;
    private int courseNumber;
    private String courseTitle;
    private int newCourseRating;

    private List<Review> courseReviews;

    private String averageRating;

    private Stage stage;
    private DatabaseManager databaseManager = new DatabaseManager();
    private User user;

    private Course course;

    public void handleSearchButton() {

        String mnemonicSearch = CourseMnemonic.getText();
        String numberSearch = CourseNumber.getText();
        String titleSearch = CourseTitle.getText();

        List<Course> courseList = new ArrayList<>();
        if (Objects.equals(mnemonicSearch, "") && Objects.equals(numberSearch, "") && Objects.equals(titleSearch, "")) {
            courseList = DatabaseManager.getAllCourses();
        }
        else if (!Objects.equals(mnemonicSearch, "") && !Objects.equals(numberSearch, "") && !Objects.equals(titleSearch, "")) {
              courseList = DatabaseManager.getCourseByMnemonicAndNumberAndTitleContains(mnemonicSearch, Integer.parseInt(numberSearch), titleSearch);

        } else if (!Objects.equals(mnemonicSearch, "") && !Objects.equals(numberSearch, "")) {
            courseList = DatabaseManager.getCourseByMnemonicAndNumber(mnemonicSearch, Integer.parseInt(numberSearch));

        } else if (!Objects.equals(mnemonicSearch, "") && !Objects.equals(titleSearch, "")) {
            courseList = DatabaseManager.getCourseByMnemonicAndTitleContains(mnemonicSearch, titleSearch);

        } else if (!Objects.equals(titleSearch, "") && !Objects.equals(numberSearch, "")) {
           courseList = DatabaseManager.getCourseByTitleContainsAndNumber(titleSearch, Integer.parseInt(numberSearch));

        } else if (!Objects.equals(titleSearch, "")) {
            courseList = DatabaseManager.getCourseByTitleContains(titleSearch);

        } else if (!Objects.equals(numberSearch, "")) {
            courseList = DatabaseManager.getCourseByNumber(Integer.parseInt(numberSearch));

        } else if (!Objects.equals(mnemonicSearch, "")){
            courseList = DatabaseManager.getCourseByMnemonic(mnemonicSearch);
        }
            ObservableList<String> searchResults = FXCollections.observableArrayList();
            for(Course course : courseList) {
                String rating = loadCourseData(course);
                String displayString = String.format("%-1s %-10s %-30s %-20s",course.getMnemonic(), course.getCourseNumber(), course.getCourseTitle(), rating);
                searchResults.add(displayString);
                System.out.println(displayString);
            }
            CourseSearchList.setItems(searchResults);
            setCellFactoryForCourseList();
    }

    private String loadCourseData(Course course) {
        DatabaseManager.initializeHibernate();
        ObservableList<Review> reviews = FXCollections.observableArrayList();
        courseReviews = Objects.requireNonNull(DatabaseManager.getAllReviews()).stream()
                .filter(rev -> rev.getCourse().equals(course))
                .toList();

        var avgRating = courseReviews.stream()
                .mapToInt(Review::getRating)  // Convert Stream<Review> to IntStream
                .average()                    // Calculates the average
                .orElse(0.0);

        String roundedDouble = String.format("%.2f", avgRating);

        if( avgRating== 0.0){
            averageRating = "NoRating";
        }
        else{
           averageRating = roundedDouble;
        }

        return averageRating;

    }
        public void handleAddButton() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add Course");
        dialog.setHeaderText("Enter Course Information");

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        TextField addCourseMnemonic = new TextField();
        TextField addCourseNumber = new TextField();
        TextField addCourseTitle = new TextField();
//        TextField addNewCourseRating = new TextField();

        gridPane.add(new Label("Course Mnemonic"), 0, 0);
        gridPane.add(addCourseMnemonic, 1, 0);
        gridPane.add(new Label("Course Number"), 0, 1);
        gridPane.add(addCourseNumber, 1, 1);
        gridPane.add(new Label("Course Title"), 0, 2);
        gridPane.add(addCourseTitle, 1, 2);
//        gridPane.add(new Label("Rating"), 0, 3);
//        gridPane.add(addNewCourseRating, 1, 3);

        dialog.getDialogPane().setContent(gridPane);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            try {
                if (dialogButton == ButtonType.OK) {
                    courseMnemonic = addCourseMnemonic.getText();
                    if (!Objects.equals(addCourseNumber.getText(), "")) {
                        courseNumber = Integer.parseInt(addCourseNumber.getText());
                    }
                    courseTitle = addCourseTitle.getText();

                    if (courseMnemonic.matches("[A-Za-z]{2,4}") && String.valueOf(courseNumber).matches("[0-9]{4}") && courseTitle.matches("[A-Za-z0-9\\s]{1,49}")) {
                        if (!courseMnemonic.matches("[A-Za-z]{2,4}")) {
                            System.out.println("FAILED adding mnemonic");
                        }
                        if (!String.valueOf(courseNumber).matches("[0-9]{4}")) {
                            System.out.println("FAILED adding course number");
                        }
                        if (!courseTitle.matches("[A-Za-z0-9\\s]{1,49}")) {
                            System.out.println("FAILED adding course title");
                        }

                        String formattedRow = String.format("%-10s %-10s %-30s %-20s", courseMnemonic.toUpperCase(), courseNumber, courseTitle, "NoRating");
                        DatabaseManager.addCourse(courseMnemonic, courseNumber, courseTitle, 0, null);
                        ObservableList<String> searchResults = FXCollections.observableArrayList(formattedRow);
                        CourseSearchList.setItems(searchResults);
                        setCellFactoryForCourseList();

                    } else {
                        Dialog<Void> invalidCourseDialog = new Dialog<>();
                        invalidCourseDialog.setTitle("Invalid Course Credentials");
                        invalidCourseDialog.setHeaderText("Error: Attempted Course Add\nMake sure the fields follow below:\n\t- Mnemonic (2-4 letters)\n\t- Number (exactly 4 numbers)\n\t- Title (1-49 letters)");
                        invalidCourseDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
                        invalidCourseDialog.showAndWait();
                        invalidCourseDialog.close();
                        handleAddButton();
                    }
                }
            } catch (NumberFormatException e) {
                // Handle the NumberFormatException here
                System.out.println("Error converting string to number: " + e.getMessage());
            } catch (Exception e) {
                // Handle other exceptions here
                System.out.println("An unexpected error occurred: " + e.getMessage());
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void setCellFactoryForCourseList() {
        CourseSearchList.setCellFactory(param -> new ListCell<>() {
            private HBox hbox;
            private Label label;
            private Button courseReviewButton;

            {
                hbox = new HBox(80); // int is the spacing between label and button
                label = new Label();
                courseReviewButton = new Button("Course Review");
                courseReviewButton.setStyle("-fx-background-color: #6fa8e3; ");

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                courseReviewButton.setOnAction(event -> {
                    // Call the linked action using the course information
                    String courseInfo = getItem();
                    System.out.println("course info: " + courseInfo);

                    // Split the course information into tokens
                    String[] tokens = courseInfo.split("\\s+");

                    // Ensure that there are at least 3 tokens (Mnemonic, CourseNumber, CourseTitle)
                    if (tokens.length >= 3) {
                        // Extract values from tokens
                        String mnemonic = tokens[0];
                        System.out.println(mnemonic);
                        int courseNumber = Integer.parseInt(tokens[1]);
                        System.out.println(courseNumber);

                        // Combine remaining tokens as CourseTitle
                        StringBuilder courseTitleBuilder = new StringBuilder();
                        for (int i = 2; i < tokens.length - 1; i++) {
                            courseTitleBuilder.append(tokens[i]).append(" ");
                        }
                        // Remove trailing space
                        String courseTitle = courseTitleBuilder.toString().trim();
                        System.out.println(courseTitle);

                        // Use the extracted values in the getcourseID method
                        String courseId = databaseManager.getcourseId(mnemonic, courseNumber, courseTitle);

                        System.out.println(courseId);

//                         System.out.println("courseID"+  courseId);
//                         if( courseId== null){
//                             System.out.println("caught it");
//                             throw new NullPointerException("caught the issue");
//                         }

                        handleLinkedButtonAction(courseId);
                    }


                });

                hbox.getChildren().addAll(label, spacer, courseReviewButton);
            }

            // function updates/displays the linked button next to each course listing
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    // Set the label text with the course information
                    label.setText(item);
                    setGraphic(hbox);
                }
            }
        });
    }

    private void handleLinkedButtonAction(String currentCourseId) {
        // when button is clicked then go to the course review scene
        System.out.println("Linked button clicked for: " + currentCourseId);

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("course-reviews.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            stage.setScene(scene);
            stage.setTitle("Course Reviews");

            CourseReviewsController controller = fxmlLoader.getController();
            controller.setStage(stage);

            Course currentCourse = databaseManager.getCourseById(currentCourseId);
            controller.setCourse(currentCourse);

//             if( currentCourse== null){
//                 System.out.println("caught it");
//                 throw new NullPointerException("caught the issue");
//             }
            System.out.println("username is ");
            System.out.println(user.toString()+ user.getUsername());
            controller.initCourseData(currentCourse, user);
            stage.show();

        }  catch (IOException e) {
            System.out.println("there was an Error!" +  e.getMessage() );
            throw new RuntimeException(e);
        }

    }
    public void setStage(Stage stage){
        this.stage = stage;
    }
    public void setUser(User user){
        this.user = user;
    }



    // header navigation //
//    @FXML
//    protected void handleCourReviewsNavAction(ActionEvent event) {
//        try {
//            Parent cReviewsRoot = FXMLLoader.load(getClass().getResource("course-reviews.fxml"));
//            Scene cReviewsScene = new Scene(cReviewsRoot);
//
//
//            stage.setScene(cReviewsScene);
//            stage.show();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

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
            System.out.println("user is" + user);
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

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

