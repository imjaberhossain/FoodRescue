package com.foodrescue.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class EventForm {

    @NotBlank(message = "Please enter a title for the event")
    @Size(max = 150, message = "Title is too long")
    private String title;

    @NotNull(message = "Please choose the event date and time")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime eventDate;

    @NotBlank(message = "Please enter the location")
    @Size(max = 255, message = "Location is too long")
    private String location;

    @NotBlank(message = "Please enter the city")
    @Size(max = 100, message = "City name is too long")
    private String city;

    @Size(max = 1000, message = "Description is too long")
    private String description;

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
}
