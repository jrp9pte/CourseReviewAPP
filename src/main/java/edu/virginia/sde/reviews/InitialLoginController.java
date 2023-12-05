package edu.virginia.sde.reviews;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class InitialLoginController {
    @FXML
    protected void handleLoginButtonAction(ActionEvent event) {
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
    @FXML
    protected void handleCreateAccButtonAction(ActionEvent event) {
        try {
            // Load the login screen
            Parent loginRoot = FXMLLoader.load(getClass().getResource("create-account.fxml"));
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

    @FXML
    protected void handleExitAction(ActionEvent event) {
        Platform.exit();
    }
}
