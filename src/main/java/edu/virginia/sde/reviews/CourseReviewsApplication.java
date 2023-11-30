package edu.virginia.sde.reviews;

import javafx.application.Application;

public abstract class CourseReviewsApplication extends Application {
    public static void main(String[] args) {
        DatabaseManager.initializeHibernate();
        System.out.println("Hello, world!");
    }
}
