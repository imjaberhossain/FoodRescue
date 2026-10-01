package com.foodrescue.service;

import com.foodrescue.dto.EventForm;
import com.foodrescue.model.Event;
import com.foodrescue.model.User;
import com.foodrescue.repository.EventRepository;
import com.foodrescue.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepo;
    private final UserRepository userRepo;
    private final NotificationService notificationService;

    public EventService(EventRepository eventRepo, UserRepository userRepo, NotificationService notificationService) {
        this.eventRepo = eventRepo;
        this.userRepo = userRepo;
        this.notificationService = notificationService;
    }

    @Transactional
    public Event createEvent(Long hostUserId, EventForm form) {
        User host = userRepo.findById(hostUserId)
                .orElseThrow(() -> new IllegalStateException("Your account could not be found. Please log in again."));
        if (host.getRole() == User.Role.PROVIDER) {
            throw new IllegalStateException("Only NGOs and Volunteers can post events.");
        }
        if (!form.getEventDate().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("The event date must be in the future.");
        }

        Event event = new Event();
        event.setHostUser(host);
        event.setTitle(form.getTitle().trim());
        event.setEventDate(form.getEventDate());
        event.setLocation(form.getLocation().trim());
        event.setCity(form.getCity().trim());
        event.setDescription(form.getDescription() == null || form.getDescription().isBlank()
                ? null : form.getDescription().trim());
        Event saved = eventRepo.save(event);

        notificationService.notifyNearbyProvidersOfEvent(saved);
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Event> eventsFor(Long hostUserId) {
        return eventRepo.findByHostUser_IdOrderByEventDateAsc(hostUserId);
    }

    @Transactional(readOnly = true)
    public List<Event> browseUpcoming(String keyword) {
        return eventRepo.findUpcoming(LocalDateTime.now(), keyword == null ? "" : keyword.trim());
    }

    @Transactional
    public void deleteEvent(Long eventId, Long hostUserId) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found."));
        if (!event.getHostUser().getId().equals(hostUserId)) {
            throw new IllegalStateException("You can only remove your own events.");
        }
        eventRepo.delete(event);
    }
}
