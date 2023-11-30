package edu.virginia.sde.reviews;

import javax.persistence.*;

@Entity
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private int rating;
    private String timestamp;
    private String comment;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    public Review() {

    }

    public Review(int rating, String timestamp, String comment, User user, Course course) {
        this.rating = rating;
        this.timestamp = timestamp;
        this.comment = comment;
        this.user = user;
        this.course = course;
    }

    public int getRating() {
        return rating;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getComment() {
        return comment;
    }

    public User getUser() {
        return user;
    }

    public Course getCourse() {
        return course;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
    @PrePersist
    @PreUpdate
    @PreRemove
    private void updateCourseRating() {
        course.recalculateRating();
    }



}
