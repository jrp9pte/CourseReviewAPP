package edu.virginia.sde.reviews;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginScreenController {
    @FXML
    private Label messageLabel;

    public void handleButton() {
        messageLabel.setText("Incorrect username or password.");
    }
}
