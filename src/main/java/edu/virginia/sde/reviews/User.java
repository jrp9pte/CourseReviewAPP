package edu.virginia.sde.reviews;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "Users")
public class User {
    @Id
    private String username;

    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Review> reviews;

    // Constructors, getters, setters

    public User() {
        reviews = new ArrayList<>();

    }
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void addReview(Review review) {
        reviews.add(review);
        review.setUser(this);
    }
    public List<Review> getReviews() {
        return reviews;
    }
    public String getUsername() {
        return this.username;
    }
    public String getPassword() {
        return password;
    }


    public String getId() {
        return username;
    }
}
