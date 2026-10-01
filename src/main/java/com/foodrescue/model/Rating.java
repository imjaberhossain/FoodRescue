package com.foodrescue.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** A provider's 1-5 star rating of an NGO/Volunteer after a completed pickup. */
@Entity
@Table(name = "ratings")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "claim_id", nullable = false, unique = true)
    private FoodClaim claim;

    /** The NGO/Volunteer user being rated. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "rated_user_id", nullable = false)
    private User ratedUser;

    /** The provider user who gave the rating. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "rated_by_user_id", nullable = false)
    private User ratedByUser;

    @Column(name = "rating_value", nullable = false)
    private int ratingValue;

    @Column(length = 255)
    private String comment;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public FoodClaim getClaim() { return claim; }
    public void setClaim(FoodClaim claim) { this.claim = claim; }
    public User getRatedUser() { return ratedUser; }
    public void setRatedUser(User ratedUser) { this.ratedUser = ratedUser; }
    public User getRatedByUser() { return ratedByUser; }
    public void setRatedByUser(User ratedByUser) { this.ratedByUser = ratedByUser; }
    public int getRatingValue() { return ratingValue; }
    public void setRatingValue(int ratingValue) { this.ratingValue = ratingValue; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
