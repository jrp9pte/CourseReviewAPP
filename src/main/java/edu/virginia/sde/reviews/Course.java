package edu.virginia.sde.reviews;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String courseId;

    @Column(length = 4, nullable = false)
    private String mnemonic;

    @Column(nullable = false)
    private int courseNumber;

    @Column(length = 50, nullable = false)
    private String courseTitle;

    @Column(name = "course_rating")
    private double courseRating;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews;

    // Constructors
    public Course() {}

    public Course(String mnemonic, int courseNumber, String courseTitle) {
        this.mnemonic = mnemonic;
        this.courseNumber = courseNumber;
        this.courseTitle = courseTitle;
    }

    // Utility method to recalculate the average rating
    public void recalculateRating() {
        if (reviews == null || reviews.isEmpty()) {
            this.courseRating = 0.0;
        } else {
            this.courseRating = reviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);
        }
    }

    // Getters and Setters
    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public int getCourseNumber() {
        return courseNumber;
    }

    public void setCourseNumber(int courseNumber) {
        this.courseNumber = courseNumber;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public double getCourseRating() {
        return courseRating;
    }

    public void setCourseRating(double courseRating) {
        this.courseRating = courseRating;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
        recalculateRating();
    }
}
