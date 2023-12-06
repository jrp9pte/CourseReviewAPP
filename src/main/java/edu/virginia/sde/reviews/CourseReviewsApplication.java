package edu.virginia.sde.reviews;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
public class CourseReviewsApplication extends Application {

    private User user;
    @Override
    public void start(Stage primaryStage) {
        try {

//            DatabaseManager.initializeHibernate();
//            switchToMyReviewsScene(primaryStage);
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

    public void switchToMyReviewsScene(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("my-reviews.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.setTitle("My Reviews");
        stage.show();

        // Assuming MyReviewsController has a method to set the main application reference
        MyReviewsController controller = fxmlLoader.getController();
        controller.setStage(stage);
        controller.initialize(user);
//        controller.setMainApp(this);
    }
}
