package edu.virginia.sde.reviews;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

import java.io.IOException;

public class HelloWorldApplication extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-world.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Hello World");
        stage.setScene(scene);
        stage.show();
    }
}

//package edu.virginia.sde.reviews;
//
//        import javafx.application.Application;
//        import javafx.fxml.FXML;
//        import javafx.fxml.FXMLLoader;
//        import javafx.scene.Scene;
//        import javafx.scene.control.Label;
//        import javafx.stage.Stage;
//
//public class HelloWorldApplication extends Application {
//    public static void main(String[] args) {
//        launch(args);
//    }
//
//    @Override
//    public void start(Stage stage) throws Exception {
////        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-world.fxml"));
////        Scene scene = new Scene(fxmlLoader.load());
////        stage.setTitle("Hello World");
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("course-search.fxml"));
//        Scene scene = new Scene(fxmlLoader.load());
//        stage.setTitle("Course Search");
//        stage.setScene(scene);
//        stage.show();
//    }
//
//}


//import java.io.IOException;
//
//public class HelloWorldApplication extends Application {
//    private Stage stage;
//    public static void main(String[] args) {
//        launch(args);
//    }
//
//    @Override
//    public void start(Stage stage) throws Exception {
//        this.stage = stage;
////        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-world.fxml"));
////        Scene scene = new Scene(fxmlLoader.load());
////        stage.setTitle("Hello World");
////
////        FXMLLoader fxmlLoader1 = new FXMLLoader(getClass().getResource("MyReviews.fxml"));
////        Scene scene1 = new Scene(fxmlLoader1.load());
////        stage.setScene(scene1);
////        stage.show();
////
////        MyReviewsController controller = fxmlLoader.getController();
////        controller.setMainApp(this);
//        loadHelloWorldScene();
//
//    }
//
//
//    public void loadHelloWorldScene() throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-world.fxml"));
//        Scene scene = new Scene(fxmlLoader.load());
//        stage.setTitle("Hello World");
//        stage.setScene(scene);
//        stage.show();
//
//        // Assuming HelloWorldController has a method to set the main application reference
//        HelloWorldController controller = fxmlLoader.getController();
//        controller.setMainApp(this);
//    }
//
//    public void switchToMyReviewsScene() throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MyReviews.fxml"));
//        Scene scene = new Scene(fxmlLoader.load());
//        stage.setScene(scene);
//        stage.setTitle("My Reviews");
//
//        // Assuming MyReviewsController has a method to set the main application reference
//        MyReviewsController controller = fxmlLoader.getController();
//        controller.setMainApp(this);
//    }
//}
