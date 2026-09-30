package com.foodrescue.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ListingForm {

    @NotNull(message = "Please choose a food type")
    private Long categoryId;

    @NotBlank(message = "Please enter a title")
    @Size(max = 150, message = "Title is too long")
    private String title;

    @Size(max = 1000, message = "Description is too long")
    private String description;

    @NotNull(message = "Please enter the quantity")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotBlank(message = "Please enter a unit, like plates or kg")
    @Size(max = 30, message = "Unit is too long")
    private String unit;

    @NotBlank(message = "Please enter the pickup location")
    @Size(max = 255, message = "Location is too long")
    private String pickupLocation;

    @NotBlank(message = "Please enter the city")
    @Size(max = 100, message = "City name is too long")
    private String city;

    /** Optional. If given, lets NGOs/Volunteers be sorted by real distance. */
    private BigDecimal latitude;
    private BigDecimal longitude;

    @NotNull(message = "Please choose the food's freshness")
    private com.foodrescue.model.QualityTag qualityTag = com.foodrescue.model.QualityTag.COOKED;

    @Min(value = 1, message = "Must be at least 1")
    private Integer estimatedBeneficiaries;

    @NotNull(message = "Please choose when the food is ready")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime availableFrom;

    @NotNull(message = "Please choose the pickup deadline")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime pickupDeadline;

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public String getPickupLocation() { return pickupLocation; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
    public com.foodrescue.model.QualityTag getQualityTag() { return qualityTag; }
    public void setQualityTag(com.foodrescue.model.QualityTag qualityTag) { this.qualityTag = qualityTag; }
    public Integer getEstimatedBeneficiaries() { return estimatedBeneficiaries; }
    public void setEstimatedBeneficiaries(Integer estimatedBeneficiaries) { this.estimatedBeneficiaries = estimatedBeneficiaries; }
    public LocalDateTime getAvailableFrom() { return availableFrom; }
    public void setAvailableFrom(LocalDateTime availableFrom) { this.availableFrom = availableFrom; }
    public LocalDateTime getPickupDeadline() { return pickupDeadline; }
    public void setPickupDeadline(LocalDateTime pickupDeadline) { this.pickupDeadline = pickupDeadline; }
}
