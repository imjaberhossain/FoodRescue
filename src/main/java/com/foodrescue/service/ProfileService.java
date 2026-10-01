package com.foodrescue.service;

import com.foodrescue.dto.ProfileForm;
import com.foodrescue.model.User;
import com.foodrescue.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/**
 * Handles user profile data and profile-related business logic.
 */
@Service
public class ProfileService {

    private final UserRepository userRepo;
    private final FoodProviderRepository providerRepo;
    private final OrganizationRepository orgRepo;
    private final VolunteerRepository volunteerRepo;
    private final FoodListingRepository listingRepo;
    private final FoodClaimRepository claimRepo;
    private final RatingRepository ratingRepo;
    private final FileStorageService fileStorage;

    public ProfileService(UserRepository userRepo,
                          FoodProviderRepository providerRepo,
                          OrganizationRepository orgRepo,
                          VolunteerRepository volunteerRepo,
                          FoodListingRepository listingRepo,
                          FoodClaimRepository claimRepo,
                          RatingRepository ratingRepo,
                          FileStorageService fileStorage) {
        this.userRepo = userRepo;
        this.providerRepo = providerRepo;
        this.orgRepo = orgRepo;
        this.volunteerRepo = volunteerRepo;
        this.listingRepo = listingRepo;
        this.claimRepo = claimRepo;
        this.ratingRepo = ratingRepo;
        this.fileStorage = fileStorage;
    }

    /** Builds everything the profile page needs to show for the given user. */
    @Transactional(readOnly = true)
    public ProfileView loadProfile(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        ProfileView view = new ProfileView();
        view.user = user;
        view.averageRating = ratingRepo.averageRatingFor(userId);
        view.ratingCount = ratingRepo.countByRatedUser_Id(userId);

        switch (user.getRole()) {
            case PROVIDER -> providerRepo.findByUser_Id(userId).ifPresent(p -> {
                view.organizationName = p.getBusinessName();
                view.address = p.getAddress();
                view.city = p.getCity();
                view.totalListings = listingRepo.findByProvider_IdOrderByCreatedAtDesc(p.getId()).size();
            });
            case NGO -> orgRepo.findByUser_Id(userId).ifPresent(o -> {
                view.organizationName = o.getOrgName();
                view.address = o.getAddress();
                view.city = o.getCity();
                view.verificationStatus = o.getVerificationStatus();
                view.idDocumentType = o.getIdDocumentType();
                view.totalCompletedPickups = (int) claimRepo.findByClaimantUser_IdOrderByClaimedAtDesc(userId)
                        .stream().filter(c -> c.getStatus().name().equals("COMPLETED")).count();
            });
            case VOLUNTEER -> volunteerRepo.findByUser_Id(userId).ifPresent(v -> {
                view.address = v.getAddress();
                view.city = v.getCity();
                view.verificationStatus = v.getVerificationStatus();
                view.idDocumentType = v.getIdDocumentType();
                view.totalCompletedPickups = (int) claimRepo.findByClaimantUser_IdOrderByClaimedAtDesc(userId)
                        .stream().filter(c -> c.getStatus().name().equals("COMPLETED")).count();
            });
        }
        return view;
    }

    /** Updates the small, always-editable part of a profile: phone, bio, photo. */
    @Transactional
    public void updateProfile(Long userId, ProfileForm form) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        user.setPhone(form.getPhone() == null || form.getPhone().isBlank() ? null : form.getPhone().trim());
        user.setBio(form.getBio() == null || form.getBio().isBlank() ? null : form.getBio().trim());

        String newPhotoPath = fileStorage.store(form.getProfilePhoto(), "photos");
        if (newPhotoPath != null) {
            user.setProfilePhotoPath(newPhotoPath);
        }
    }

    /** Plain data holder passed to the profile template - keeps profile.html simple. */
    public static class ProfileView {
        public User user;
        public String organizationName;
        public String address;
        public String city;
        public com.foodrescue.model.VerificationStatus verificationStatus;
        public String idDocumentType;
        public Double averageRating;
        public long ratingCount;
        public int totalListings;
        public int totalCompletedPickups;
    }
}
