package com.foodrescue.service;

import com.foodrescue.model.*;
import com.foodrescue.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Simplified "location-based alert": this project has no email/SMS/push service
 * configured, so an "alert" is a row in the notifications table that the recipient
 * sees via the bell icon. "Nearby" is approximated by matching city text, since
 * true GPS-radius alerts would need a geocoding service this class project doesn't have.
 */
@Service
public class NotificationService {

    private final NotificationRepository notificationRepo;
    private final OrganizationRepository orgRepo;
    private final VolunteerRepository volunteerRepo;
    private final FoodProviderRepository providerRepo;

    public NotificationService(NotificationRepository notificationRepo,
                               OrganizationRepository orgRepo,
                               VolunteerRepository volunteerRepo,
                               FoodProviderRepository providerRepo) {
        this.notificationRepo = notificationRepo;
        this.orgRepo = orgRepo;
        this.volunteerRepo = volunteerRepo;
        this.providerRepo = providerRepo;
    }

    /** Called right after a provider posts new food - alerts NGOs/Volunteers in the same city. */
    @Transactional
    public void notifyNearbyClaimantsOfListing(FoodListing listing) {
        String message = "New food posted nearby: " + listing.getTitle() + " in " + listing.getCity();
        for (Organization o : orgRepo.findByCityIgnoreCase(listing.getCity())) {
            save(o.getUser(), message, "/dashboard");
        }
        for (Volunteer v : volunteerRepo.findByCityIgnoreCase(listing.getCity())) {
            save(v.getUser(), message, "/dashboard");
        }
    }

    /** Called right after an NGO/Volunteer posts a new event - alerts providers in the same city. */
    @Transactional
    public void notifyNearbyProvidersOfEvent(Event event) {
        String message = event.getHostUser().getFullName() + " has an upcoming event: " + event.getTitle()
                + " in " + event.getCity();
        for (FoodProvider p : providerRepo.findByCityIgnoreCase(event.getCity())) {
            save(p.getUser(), message, "/events");
        }
    }

    @Transactional(readOnly = true)
    public List<Notification> allFor(Long userId) {
        return notificationRepo.findByRecipientUser_IdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public long unreadCountFor(Long userId) {
        return notificationRepo.countByRecipientUser_IdAndReadFalse(userId);
    }

    @Transactional
    public void markAllRead(Long userId) {
        for (Notification n : notificationRepo.findByRecipientUser_IdAndReadFalse(userId)) {
            n.setRead(true);
        }
    }

    private void save(User recipient, String message, String link) {
        Notification n = new Notification();
        n.setRecipientUser(recipient);
        n.setMessage(message);
        n.setLink(link);
        notificationRepo.save(n);
    }
}
