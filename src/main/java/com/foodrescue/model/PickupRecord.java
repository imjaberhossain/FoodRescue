package com.foodrescue.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pickup_records")
public class PickupRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "claim_id", nullable = false, unique = true)
    private FoodClaim claim;

    @Column(name = "picked_up_at", nullable = false)
    private LocalDateTime pickedUpAt = LocalDateTime.now();

    @Column(name = "picked_up_by", length = 100)
    private String pickedUpBy;

    @Column(length = 255)
    private String notes;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public FoodClaim getClaim() { return claim; }
    public void setClaim(FoodClaim claim) { this.claim = claim; }
    public LocalDateTime getPickedUpAt() { return pickedUpAt; }
    public void setPickedUpAt(LocalDateTime pickedUpAt) { this.pickedUpAt = pickedUpAt; }
    public String getPickedUpBy() { return pickedUpBy; }
    public void setPickedUpBy(String pickedUpBy) { this.pickedUpBy = pickedUpBy; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
