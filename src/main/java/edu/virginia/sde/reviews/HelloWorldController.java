package edu.virginia.sde.reviews;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;


public class HelloWorldController {
    @FXML
    private Label messageLabel;

    public void handleButton() {
        messageLabel.setText("You pressed the button!");
    }
}

//public class HelloWorldController {
//    @FXML
//    private Label messageLabel;
//    @FXML
//    private Label ReviewsLabel;
//    private HelloWorldApplication mainApp;
//    public void handleButton() {
//        messageLabel.setText("You pressed the button!");
//    }
//    public void setMainApp(HelloWorldApplication mainApp) {
//        this.mainApp = mainApp;
//    }
//
//    @FXML
//    private void handleGoToReviews() throws IOException {
//        try {
//            mainApp.switchToMyReviewsScene();
//        } catch (IOException e) {
//            e.printStackTrace();
//            // Handle exceptions
//        }
//    }
//}