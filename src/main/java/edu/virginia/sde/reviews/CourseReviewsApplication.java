package edu.virginia.sde.reviews;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CourseReviewsApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {

            DatabaseManager.initializeHibernate();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("initial-login.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            primaryStage.setTitle("Initial Login Screen");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
