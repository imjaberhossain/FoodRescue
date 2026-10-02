package com.foodrescue.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "food_claims")
public class FoodClaim {

    /**
     * PENDING   - NGO/Volunteer sent a request, provider has not decided yet
     * ACCEPTED  - provider chose this request; food is now reserved for them
     * REJECTED  - provider chose someone else, or rejected this one directly
     * COMPLETED - the accepted claimant actually picked up the food
     * CANCELLED - the claimant withdrew their own request
     */
    public enum Status {
        PENDING("Waiting for provider", "bg-turmeric/25 text-ink"),
        ACCEPTED("Accepted - pending pickup", "bg-leaf-tint text-leaf-dark"),
        REJECTED("Not selected", "bg-gray-200 text-gray-600"),
        COMPLETED("Picked up", "bg-ink text-white"),
        CANCELLED("Cancelled", "bg-gray-200 text-gray-600");

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
    @JoinColumn(name = "listing_id", nullable = false)
    private FoodListing listing;

    /** The NGO or Volunteer user who requested this food (not their profile row). */
    @ManyToOne(optional = false)
    @JoinColumn(name = "claimant_user_id", nullable = false)
    private User claimantUser;

    /** How and where they plan to distribute the food. Required, to discourage misuse. */
    @Column(name = "distribution_plan", nullable = false, length = 1000)
    private String distributionPlan;

    @Column(name = "claimed_at", nullable = false)
    private LocalDateTime claimedAt = LocalDateTime.now();

<<<<<<< HEAD
    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

=======
>>>>>>> b3947b32b8c1031c4bab383c7ff83f179a94df59
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.PENDING;

    @Column(length = 255)
    private String note;

    /** Mandatory proof (photo/report) the claimant uploads after distributing the food. */
    @Column(name = "distribution_proof_path", length = 255)
    private String distributionProofPath;

    @Column(name = "distribution_report", length = 1000)
    private String distributionReport;

    @Column(name = "distributed_at")
    private LocalDateTime distributedAt;

    // ---------- View-only fields, filled in by the service layer for display ----------
    // These are NOT database columns (@Transient). They save every template from having
    // to work out "is this claimant an NGO or a Volunteer" on its own.

    @Transient private String claimantName;
    @Transient private String claimantRoleLabel;
    @Transient private String claimantCity;
    @Transient private String claimantPhone;
    @Transient private VerificationStatus claimantVerification;
    @Transient private Double claimantAverageRating;
    @Transient private Long claimantRatingCount;
    @Transient private Double distanceKm;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public FoodListing getListing() { return listing; }
    public void setListing(FoodListing listing) { this.listing = listing; }
    public User getClaimantUser() { return claimantUser; }
    public void setClaimantUser(User claimantUser) { this.claimantUser = claimantUser; }
    public String getDistributionPlan() { return distributionPlan; }
    public void setDistributionPlan(String distributionPlan) { this.distributionPlan = distributionPlan; }
    public LocalDateTime getClaimedAt() { return claimedAt; }
    public void setClaimedAt(LocalDateTime claimedAt) { this.claimedAt = claimedAt; }
<<<<<<< HEAD
    public LocalDateTime getAcceptedAt() { return acceptedAt; }
    public void setAcceptedAt(LocalDateTime acceptedAt) { this.acceptedAt = acceptedAt; }
=======
>>>>>>> b3947b32b8c1031c4bab383c7ff83f179a94df59
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getDistributionProofPath() { return distributionProofPath; }
    public void setDistributionProofPath(String distributionProofPath) { this.distributionProofPath = distributionProofPath; }
    public String getDistributionReport() { return distributionReport; }
    public void setDistributionReport(String distributionReport) { this.distributionReport = distributionReport; }
    public LocalDateTime getDistributedAt() { return distributedAt; }
    public void setDistributedAt(LocalDateTime distributedAt) { this.distributedAt = distributedAt; }

    public String getClaimantName() { return claimantName; }
    public void setClaimantName(String claimantName) { this.claimantName = claimantName; }
    public String getClaimantRoleLabel() { return claimantRoleLabel; }
    public void setClaimantRoleLabel(String claimantRoleLabel) { this.claimantRoleLabel = claimantRoleLabel; }
    public String getClaimantCity() { return claimantCity; }
    public void setClaimantCity(String claimantCity) { this.claimantCity = claimantCity; }
    public String getClaimantPhone() { return claimantPhone; }
    public void setClaimantPhone(String claimantPhone) { this.claimantPhone = claimantPhone; }
    public VerificationStatus getClaimantVerification() { return claimantVerification; }
    public void setClaimantVerification(VerificationStatus claimantVerification) { this.claimantVerification = claimantVerification; }
    public Double getClaimantAverageRating() { return claimantAverageRating; }
    public void setClaimantAverageRating(Double claimantAverageRating) { this.claimantAverageRating = claimantAverageRating; }
    public Long getClaimantRatingCount() { return claimantRatingCount; }
    public void setClaimantRatingCount(Long claimantRatingCount) { this.claimantRatingCount = claimantRatingCount; }
    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }
}
