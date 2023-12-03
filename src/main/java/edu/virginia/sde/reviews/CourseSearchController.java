package edu.virginia.sde.reviews;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.collections.ObservableList;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

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
    private String courseNumber;
    private String courseTitle;

    public void handleSearchButton() {

        String mnemonicSearch = CourseMnemonic.getText();
        String numberSearch = CourseNumber.getText();
        String titleSearch = CourseTitle.getText();
        if (!Objects.equals(mnemonicSearch, "") || !Objects.equals(numberSearch, "") || !Objects.equals(titleSearch, "")) {
            ObservableList<String> searchResults = FXCollections.observableArrayList("Course 1", "Course 2");
            CourseSearchList.setItems(searchResults);
            setCellFactoryForCourseList();

        }
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

        gridPane.add(new Label("Course Mnemonic"), 0, 0);
        gridPane.add(addCourseMnemonic, 1, 0);
        gridPane.add(new Label("Course Number"), 0, 1);
        gridPane.add(addCourseNumber, 1, 1);
        gridPane.add(new Label("Course Title"), 0, 2);
        gridPane.add(addCourseTitle, 1, 2);

        dialog.getDialogPane().setContent(gridPane);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                courseMnemonic = addCourseMnemonic.getText();
                courseNumber = addCourseNumber.getText();
                courseTitle = addCourseTitle.getText();

                if (!Objects.equals(addCourseMnemonic.getText(), "") && !Objects.equals(addCourseNumber.getText(), "") && !Objects.equals(addCourseTitle.getText(), "")) {
                    // add the added class to the list
                    String formattedRow = String.format(courseMnemonic + " " + courseNumber + " " + courseTitle);
                    ObservableList<String> searchResults = FXCollections.observableArrayList(formattedRow);
                    CourseSearchList.setItems(searchResults);
                } else {
                    Dialog<Void> invalidCourseDialog = new Dialog<>();
                    invalidCourseDialog.setTitle("Invalid Course Credentials");
                    invalidCourseDialog.setHeaderText("Error: Attempted Course Add\nMake sure the fields follow below:\n\t- Mnemonic (2-4 letters)\n\t- Number (exactly 4 numbers)\n\t- Title (1-49 letters)");
                    invalidCourseDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
                    invalidCourseDialog.showAndWait();
                }
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
                hbox = new HBox(1000); // 1000 is the spacing between label and button
                label = new Label();
                courseReviewButton = new Button("Course Review");

                courseReviewButton.setOnAction(event -> {
                    // Call the linked action using the course information
                    // can delete the next few lines later (used for testing)
                    String courseInfo = getItem();
                    System.out.println("testing " + courseInfo);
                    handleLinkedButtonAction(courseInfo);
                });

                hbox.getChildren().addAll(label, courseReviewButton);
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

    private void handleLinkedButtonAction(String courseInfo) {
        // Implement the linked button action using the course information
        // when button is clicked then go to the course review scene
        System.out.println("Linked button clicked for: " + courseInfo);
    }

}

