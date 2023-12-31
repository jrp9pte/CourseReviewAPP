package edu.virginia.sde.reviews;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;


public class LoginScreenController {
    @FXML
    private Label messageLabel;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    private Stage stage;

    private User user;

    @FXML
    private void authenticateUser(ActionEvent event) {
            String username = usernameField.getText();
            String password = passwordField.getText();

            boolean isAuthenticated = DatabaseManager.login(username, password);

            if (isAuthenticated) {
                // User is authenticated, navigate to the course search screen
                try {
                    // Load the course search screen
//                    Parent courseSearchRoot = FXMLLoader.load(getClass().getResource("course-search.fxml"));
//                    Scene courseSearchScene = new Scene(courseSearchRoot);

                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("course-search.fxml"));
                    Scene scene = new Scene(fxmlLoader.load());

                    // Get the stage from the event source
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                    // Set the course search scene on the stage
                    stage.setScene(scene);
                    stage.setTitle("Course Search");
                    CourseSearchController controller = fxmlLoader.getController();
                    controller.setStage(stage);
                    User user = new User(username, password);
                    controller.setUser(user);
//                    controller.initialize(username);
                    stage.show();





                    stage.show();

                    // Assuming MyReviewsController has a method to set the main application reference
//                    CourseSearchController controller = fxmlLoader.getController();
//                    controller.setStage(stage);
//                    controller.initialize(user);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // Authentication failed, display an error message.
                messageLabel.setText("Incorrect username or password.");
            }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    public void setUser(User user){
        this.user = user;
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

