package com.foodrescue.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "food_listings")
public class FoodListing {

    public enum Status {
        AVAILABLE("Available", "bg-leaf-tint text-leaf-dark"),
        CLAIMED("Claimed", "bg-turmeric/25 text-ink"),
        PICKED_UP("Picked up", "bg-ink text-white"),
        EXPIRED("Expired", "bg-gray-200 text-gray-600");

        private final String label;
        private final String badgeClass;

        Status(String label, String badgeClass) {
            this.label = label;
            this.badgeClass = badgeClass;
        }

        public String getLabel() { return label; }
        public String getBadgeClass() { return badgeClass; }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "provider_id", nullable = false)
    private FoodProvider provider;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private FoodCategory category;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, length = 30)
    private String unit;

    @Column(name = "pickup_location", nullable = false)
    private String pickupLocation;

    @Column(nullable = false, length = 100)
    private String city;

    private BigDecimal latitude;
    private BigDecimal longitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "quality_tag", nullable = false, length = 20)
    private QualityTag qualityTag = QualityTag.COOKED;

    @Column(name = "estimated_beneficiaries")
    private Integer estimatedBeneficiaries;

    @Column(name = "available_from", nullable = false)
    private LocalDateTime availableFrom;

    @Column(name = "pickup_deadline", nullable = false)
    private LocalDateTime pickupDeadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.AVAILABLE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // ---------- Urgency logic (calculated, not stored in the database) ----------

    public long getMinutesLeft() {
        long seconds = Duration.between(LocalDateTime.now(), pickupDeadline).getSeconds();
        return seconds <= 0 ? 0 : (seconds + 59) / 60;
    }

    public UrgencyLevel getUrgencyLevel() {
        return UrgencyLevel.fromMinutesLeft(getMinutesLeft());
    }

    public String getTimeLeftText() {
        long m = getMinutesLeft();
        if (m <= 0) return "Expired";
        long days = m / 1440;
        long hours = (m % 1440) / 60;
        long mins = m % 60;
        if (days > 0) return days + "d " + hours + "h left";
        if (hours > 0) return hours + "h " + mins + "m left";
        return mins + " min left";
    }

    // ---------- Getters and setters ----------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public FoodProvider getProvider() { return provider; }
    public void setProvider(FoodProvider provider) { this.provider = provider; }
    public FoodCategory getCategory() { return category; }
    public void setCategory(FoodCategory category) { this.category = category; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
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
    public QualityTag getQualityTag() { return qualityTag; }
    public void setQualityTag(QualityTag qualityTag) { this.qualityTag = qualityTag; }
    public Integer getEstimatedBeneficiaries() { return estimatedBeneficiaries; }
    public void setEstimatedBeneficiaries(Integer estimatedBeneficiaries) { this.estimatedBeneficiaries = estimatedBeneficiaries; }
    public LocalDateTime getAvailableFrom() { return availableFrom; }
    public void setAvailableFrom(LocalDateTime availableFrom) { this.availableFrom = availableFrom; }
    public LocalDateTime getPickupDeadline() { return pickupDeadline; }
    public void setPickupDeadline(LocalDateTime pickupDeadline) { this.pickupDeadline = pickupDeadline; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
