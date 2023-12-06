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

//    private Stage stage;
//
//    public void setStage(Stage stage){
//        this.stage = stage;
//    }

    @FXML
    protected void handleLoginButtonAction(ActionEvent event) {
        try {
            // Load the login screen
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login-screen.fxml"));
            Scene loginScene = new Scene(fxmlLoader.load());

//            LoginScreenController controller = fxmlLoader.getController();

            // Get the stage from the event source
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//            controller.setStage(stage);

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
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("create-account.fxml"));
            Scene loginScene = new Scene(fxmlLoader.load());

            CreateAccountController controller = fxmlLoader.getController();

            // Get the stage from the event source
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            controller.setStage(stage);

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
