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



public class LoginScreenController {
    @FXML
    private Label messageLabel;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private void authenticateUser(ActionEvent event) {
            String username = usernameField.getText();
            String password = passwordField.getText();

            boolean isAuthenticated = DatabaseManager.login(username, password);

            if (isAuthenticated) {
                // User is authenticated, navigate to the course search screen
                try {
                    // Load the course search screen
                    Parent courseSearchRoot = FXMLLoader.load(getClass().getResource("course-search.fxml"));
                    Scene courseSearchScene = new Scene(courseSearchRoot);

                    // Get the stage from the event source
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                    // Set the course search scene on the stage
                    stage.setScene(courseSearchScene);
                    stage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // Authentication failed, display an error message.
                messageLabel.setText("Incorrect username or password.");
            }
        }

    }
