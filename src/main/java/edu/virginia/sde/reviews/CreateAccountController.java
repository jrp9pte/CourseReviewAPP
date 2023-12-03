package edu.virginia.sde.reviews;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class CreateAccountController {
    @FXML
    private TextField usernameField; // TextField for username input

    @FXML
    private TextField passwordField; // TextField for password input

    @FXML
    protected void CreateAccount(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Call the method from DatabaseManager and handle the response

        String response = DatabaseManager.createNewUser(username, password);
        if (response == "User created successfully.") {
            try {
                // Load the login screen
                Parent loginRoot = FXMLLoader.load(getClass().getResource("login-screen.fxml"));
                Scene loginScene = new Scene(loginRoot);

                // Get the stage from the event source
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                // Set the login scene on the stage
                stage.setScene(loginScene);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Account Creation Status");
        alert.setHeaderText(null);
        alert.setContentText(response);
        alert.showAndWait();
    }
}
