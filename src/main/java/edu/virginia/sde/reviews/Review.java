package edu.virginia.sde.reviews;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "Reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int rating;
    private Timestamp timestamp;
    private String comment;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    public Review() {

    }

    public Review(int rating, Timestamp timestamp, String comment, User user, Course course) {
        this.rating = rating;
        this.timestamp = timestamp;
        this.comment = comment;
        this.user = user;
        this.course = course;
    }

    public int getRating() {
        return rating;
    }

    public int getId() {
        return id;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
    @PrePersist
    protected void onCreate() {
        timestamp = new Timestamp(System.currentTimeMillis());
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

    public void setTimestamp(Timestamp timestamp) {
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

    public String toString() {
        return "Review{" +
                "rating=" + rating +
                ", timestamp=" + timestamp +
                ", comment='" + comment + '\'' +
                ", user=" + user +
                ", course=" + course +
                '}';
    }




}
