package com.foodrescue.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Submitted by a provider to rate an NGO/Volunteer after a completed pickup. */
public class RatingForm {

    @NotNull(message = "Please choose a star rating")
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Integer ratingValue;

    @Size(max = 255, message = "Comment is too long")
    private String comment;

    public Integer getRatingValue() { return ratingValue; }
    public void setRatingValue(Integer ratingValue) { this.ratingValue = ratingValue; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
