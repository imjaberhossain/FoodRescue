package com.foodrescue.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** A "flag this" report against a listing or a claim - scam, fake post, or misuse. */
@Entity
@Table(name = "reports")
public class Report {

    public enum Status { OPEN, REVIEWED, DISMISSED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "reporter_user_id", nullable = false)
    private User reporterUser;

    @ManyToOne
    @JoinColumn(name = "listing_id")
    private FoodListing listing;

    @ManyToOne
    @JoinColumn(name = "claim_id")
    private FoodClaim claim;

    @Column(nullable = false, length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.OPEN;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getReporterUser() { return reporterUser; }
    public void setReporterUser(User reporterUser) { this.reporterUser = reporterUser; }
    public FoodListing getListing() { return listing; }
    public void setListing(FoodListing listing) { this.listing = listing; }
    public FoodClaim getClaim() { return claim; }
    public void setClaim(FoodClaim claim) { this.claim = claim; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
