package com.foodrescue.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** An NGO's or Volunteer's upcoming distribution event/campaign, shown on their profile. */
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "host_user_id", nullable = false)
    private User hostUser;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(length = 1000)
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public boolean isUpcoming() { return eventDate.isAfter(LocalDateTime.now()); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getHostUser() { return hostUser; }
    public void setHostUser(User hostUser) { this.hostUser = hostUser; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public LocalDateTime getEventDate() { return eventDate; }
    public void setEventDate(LocalDateTime eventDate) { this.eventDate = eventDate; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
