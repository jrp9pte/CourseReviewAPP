package edu.virginia.sde.reviews;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class HelloWorldApplication extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-world.fxml"));
//        Scene scene = new Scene(fxmlLoader.load());
//        stage.setTitle("Hello World");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("course-search.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Course Search");
        stage.setScene(scene);
        stage.show();
    }

}
