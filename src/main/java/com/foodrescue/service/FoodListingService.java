package com.foodrescue.service;

import com.foodrescue.dto.ClaimRequestForm;
import com.foodrescue.dto.ListingForm;
import com.foodrescue.dto.RatingForm;
import com.foodrescue.model.*;
import com.foodrescue.repository.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class FoodListingService {

    /**
     * URGENCY PRIORITY RULE for the NGO/Volunteer browse list (most urgent food first):
     * 1. Higher urgency level first (CRITICAL, HIGH, MEDIUM, LOW)
     * 2. Same level: the earlier pickup deadline first
     * 3. Same deadline: the bigger quantity first (more meals saved)
     */
    private static final Comparator<FoodListing> URGENCY_ORDER =
            Comparator.comparing(FoodListing::getUrgencyLevel)
                    .thenComparing(FoodListing::getPickupDeadline)
                    .thenComparing(Comparator.comparingInt(FoodListing::getQuantity).reversed());

    /**
     * When a provider is choosing between several requests for the SAME food,
     * show the best-suited claimant first: higher average rating, then closer distance,
     * then whoever asked first.
     */
    private static final Comparator<FoodClaim> BEST_REQUEST_FIRST =
            Comparator.comparing((FoodClaim c) -> c.getClaimantAverageRating() == null ? -1 : c.getClaimantAverageRating(),
                            Comparator.reverseOrder())
                    .thenComparing(c -> c.getDistanceKm() == null ? Double.MAX_VALUE : c.getDistanceKm())
                    .thenComparing(FoodClaim::getClaimedAt);

    private final FoodListingRepository listingRepo;
    private final FoodProviderRepository providerRepo;
    private final OrganizationRepository orgRepo;
    private final VolunteerRepository volunteerRepo;
    private final FoodCategoryRepository categoryRepo;
    private final FoodClaimRepository claimRepo;
    private final PickupRecordRepository pickupRepo;
    private final RatingRepository ratingRepo;
    private final UserRepository userRepo;
    private final FileStorageService fileStorage;

    public FoodListingService(FoodListingRepository listingRepo,
                              FoodProviderRepository providerRepo,
                              OrganizationRepository orgRepo,
                              VolunteerRepository volunteerRepo,
                              FoodCategoryRepository categoryRepo,
                              FoodClaimRepository claimRepo,
                              PickupRecordRepository pickupRepo,
                              RatingRepository ratingRepo,
                              UserRepository userRepo,
                              FileStorageService fileStorage) {
        this.listingRepo = listingRepo;
        this.providerRepo = providerRepo;
        this.orgRepo = orgRepo;
        this.volunteerRepo = volunteerRepo;
        this.categoryRepo = categoryRepo;
        this.claimRepo = claimRepo;
        this.pickupRepo = pickupRepo;
        this.ratingRepo = ratingRepo;
        this.userRepo = userRepo;
        this.fileStorage = fileStorage;
    }

    // ------------------------------------------------------------------
    // Provider side: post food, review requests, accept/reject, confirm pickup, rate
    // ------------------------------------------------------------------

    @Transactional(readOnly = true)
    public Optional<FoodProvider> findProvider(Long userId) {
        return providerRepo.findByUser_Id(userId);
    }

    @Transactional
    public FoodListing createListing(Long userId, ListingForm form) {
        FoodProvider provider = providerRepo.findByUser_Id(userId)
                .orElseThrow(() -> new IllegalStateException("Only food providers can post food."));
        FoodCategory category = categoryRepo.findById(form.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Please choose a valid food type."));

        if (!form.getPickupDeadline().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("The pickup deadline must be in the future.");
        }
        if (!form.getPickupDeadline().isAfter(form.getAvailableFrom())) {
            throw new IllegalArgumentException("The pickup deadline must be after the time the food is ready.");
        }

        FoodListing listing = new FoodListing();
        listing.setProvider(provider);
        listing.setCategory(category);
        listing.setTitle(form.getTitle().trim());
        listing.setDescription(form.getDescription() == null || form.getDescription().isBlank()
                ? null : form.getDescription().trim());
        listing.setQuantity(form.getQuantity());
        listing.setUnit(form.getUnit().trim());
        listing.setPickupLocation(form.getPickupLocation().trim());
        listing.setCity(form.getCity().trim());
        listing.setLatitude(form.getLatitude());
        listing.setLongitude(form.getLongitude());
        listing.setQualityTag(form.getQualityTag());
        listing.setEstimatedBeneficiaries(form.getEstimatedBeneficiaries());
        listing.setAvailableFrom(form.getAvailableFrom());
        listing.setPickupDeadline(form.getPickupDeadline());
        return listingRepo.save(listing);
    }

    @Transactional(readOnly = true)
    public List<FoodListing> listingsForProvider(Long userId) {
        return providerRepo.findByUser_Id(userId)
                .map(p -> listingRepo.findByProvider_IdOrderByCreatedAtDesc(p.getId()))
                .orElseGet(List::of);
    }

    /** All requests received for this provider's listings, newest first, enriched for display. */
    @Transactional(readOnly = true)
    public List<FoodClaim> claimsForProvider(Long userId) {
        FoodProvider provider = providerRepo.findByUser_Id(userId).orElse(null);
        if (provider == null) return List.of();
        List<FoodClaim> claims = claimRepo.findByListing_Provider_IdOrderByClaimedAtDesc(provider.getId());
        enrich(claims);
        return claims;
    }

    /** The still-open requests for ONE listing, best-suited claimant first - what the provider picks from. */
    @Transactional(readOnly = true)
    public List<FoodClaim> pendingRequestsForListing(Long listingId) {
        List<FoodClaim> claims = new ArrayList<>(
                claimRepo.findByListing_IdAndStatusOrderByClaimedAtAsc(listingId, FoodClaim.Status.PENDING));
        enrich(claims);
        claims.sort(BEST_REQUEST_FIRST);
        return claims;
    }

    /** Provider accepts one request. All other pending requests for the same food are auto-rejected. */
    @Transactional
    public void acceptClaim(Long claimId, Long providerUserId) {
        FoodProvider provider = requireProvider(providerUserId);
        FoodClaim claim = claimRepo.findById(claimId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found."));
        requireOwnsListing(claim, provider);

        if (claim.getStatus() != FoodClaim.Status.PENDING) {
            throw new IllegalStateException("This request has already been decided.");
        }
        FoodListing listing = claim.getListing();
        if (listing.getStatus() != FoodListing.Status.AVAILABLE) {
            throw new IllegalStateException("This food is no longer available.");
        }

        claim.setStatus(FoodClaim.Status.ACCEPTED);
        listing.setStatus(FoodListing.Status.CLAIMED);

        // Anyone else who requested the same food did not get it this time.
        for (FoodClaim other : claimRepo.findByListing_IdAndStatusOrderByClaimedAtAsc(listing.getId(), FoodClaim.Status.PENDING)) {
            other.setStatus(FoodClaim.Status.REJECTED);
        }
    }

    /** Provider directly rejects one request (without accepting anyone else yet). */
    @Transactional
    public void rejectClaim(Long claimId, Long providerUserId) {
        FoodProvider provider = requireProvider(providerUserId);
        FoodClaim claim = claimRepo.findById(claimId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found."));
        requireOwnsListing(claim, provider);

        if (claim.getStatus() != FoodClaim.Status.PENDING) {
            throw new IllegalStateException("This request has already been decided.");
        }
        claim.setStatus(FoodClaim.Status.REJECTED);
    }

    /** The provider confirms that the accepted claimant really picked up the food. */
    @Transactional
    public void confirmPickup(Long claimId, Long providerUserId) {
        FoodProvider provider = requireProvider(providerUserId);
        FoodClaim claim = claimRepo.findById(claimId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found."));
        requireOwnsListing(claim, provider);

        if (claim.getStatus() != FoodClaim.Status.ACCEPTED) {
            throw new IllegalStateException("This request is not in an accepted state.");
        }

        claim.setStatus(FoodClaim.Status.COMPLETED);
        claim.getListing().setStatus(FoodListing.Status.PICKED_UP);

        PickupRecord record = new PickupRecord();
        record.setClaim(claim);
        record.setPickedUpAt(LocalDateTime.now());
        record.setPickedUpBy(claim.getClaimantUser().getFullName());
        pickupRepo.save(record);
    }

    /** Provider rates the claimant (1-5 stars) after a completed pickup. One rating per claim. */
    @Transactional
    public void rateClaimant(Long claimId, Long providerUserId, RatingForm form) {
        FoodProvider provider = requireProvider(providerUserId);
        FoodClaim claim = claimRepo.findById(claimId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found."));
        requireOwnsListing(claim, provider);

        if (claim.getStatus() != FoodClaim.Status.COMPLETED) {
            throw new IllegalStateException("You can only rate a claim after the pickup is confirmed.");
        }
        if (ratingRepo.findByClaim_Id(claimId).isPresent()) {
            throw new IllegalStateException("You have already rated this pickup.");
        }

        Rating rating = new Rating();
        rating.setClaim(claim);
        rating.setRatedUser(claim.getClaimantUser());
        rating.setRatedByUser(provider.getUser());
        rating.setRatingValue(form.getRatingValue());
        rating.setComment(form.getComment() == null || form.getComment().isBlank() ? null : form.getComment().trim());
        ratingRepo.save(rating);
    }

    // ------------------------------------------------------------------
    // NGO / Volunteer side: browse, request, track own claims
    // ------------------------------------------------------------------

    /** Available food, filtered by a city or location word, most urgent first. */
    @Transactional(readOnly = true)
    public List<FoodListing> browseAvailable(String keyword) {
        String text = keyword == null ? "" : keyword.trim();
        List<FoodListing> result = new ArrayList<>(
                listingRepo.searchAvailable(FoodListing.Status.AVAILABLE, LocalDateTime.now(), text));
        result.sort(URGENCY_ORDER);
        return result;
    }

    @Transactional(readOnly = true)
    public List<FoodClaim> claimsForClaimant(Long userId) {
        List<FoodClaim> claims = claimRepo.findByClaimantUser_IdOrderByClaimedAtDesc(userId);
        enrich(claims);
        return claims;
    }

    /** NGO/Volunteer requests a listing. Multiple people may request the same food; the
     *  provider later accepts one. A distribution plan is required, to discourage misuse. */
    @Transactional
    public FoodClaim requestClaim(Long listingId, Long claimantUserId, ClaimRequestForm form) {
        FoodListing listing = listingRepo.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("This food listing was not found."));
        if (listing.getStatus() != FoodListing.Status.AVAILABLE) {
            throw new IllegalStateException("This food is no longer available to request.");
        }
        if (!listing.getPickupDeadline().isAfter(LocalDateTime.now())) {
            throw new IllegalStateException("This listing has expired.");
        }

        User claimant = userRepo.findById(claimantUserId)
                .orElseThrow(() -> new IllegalStateException("Your account could not be found. Please log in again."));

        FoodClaim claim = new FoodClaim();
        claim.setListing(listing);
        claim.setClaimantUser(claimant);
        claim.setDistributionPlan(form.getDistributionPlan().trim());

        try {
            return claimRepo.save(claim);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("You have already requested this food.");
        }
    }

    /** The claimant uploads mandatory proof (report + optional photo) after actually distributing the food. */
    @Transactional
    public void submitDistributionProof(Long claimId, Long claimantUserId, com.foodrescue.dto.DistributionProofForm form) {
        FoodClaim claim = claimRepo.findById(claimId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found."));

        if (!claim.getClaimantUser().getId().equals(claimantUserId)) {
            throw new IllegalStateException("You can only submit proof for your own requests.");
        }
        if (claim.getStatus() != FoodClaim.Status.COMPLETED) {
            throw new IllegalStateException("You can only submit distribution proof after the pickup is confirmed.");
        }

        String path = fileStorage.store(form.getProofPhoto(), "distribution-proof");
        if (path != null) {
            claim.setDistributionProofPath(path);
        }
        claim.setDistributionReport(form.getReport().trim());
        claim.setDistributedAt(LocalDateTime.now());
    }

    // ------------------------------------------------------------------
    // Enrichment: fills FoodClaim's @Transient display fields (name, rating, distance, ...)
    // ------------------------------------------------------------------

    private void enrich(List<FoodClaim> claims) {
        for (FoodClaim claim : claims) {
            Long claimantUserId = claim.getClaimantUser().getId();

            Optional<Organization> org = orgRepo.findByUser_Id(claimantUserId);
            Optional<Volunteer> vol = org.isPresent() ? Optional.empty() : volunteerRepo.findByUser_Id(claimantUserId);

            String name;
            String roleLabel;
            String city;
            VerificationStatus verification;
            BigDecimal claimantLat = null;
            BigDecimal claimantLng = null;

            if (org.isPresent()) {
                Organization o = org.get();
                name = o.getOrgName();
                roleLabel = "NGO";
                city = o.getCity();
                verification = o.getVerificationStatus();
                claimantLat = o.getLatitude();
                claimantLng = o.getLongitude();
            } else if (vol.isPresent()) {
                Volunteer v = vol.get();
                name = v.getUser().getFullName();
                roleLabel = "Volunteer";
                city = v.getCity();
                verification = v.getVerificationStatus();
                claimantLat = v.getLatitude();
                claimantLng = v.getLongitude();
            } else {
                name = "Unknown user";
                roleLabel = "";
                city = "";
                verification = null;
            }

            claim.setClaimantName(name);
            claim.setClaimantRoleLabel(roleLabel);
            claim.setClaimantCity(city);
            claim.setClaimantPhone(claim.getClaimantUser().getPhone());
            claim.setClaimantVerification(verification);
            claim.setClaimantAverageRating(ratingRepo.averageRatingFor(claimantUserId));
            claim.setClaimantRatingCount(ratingRepo.countByRatedUser_Id(claimantUserId));

            FoodListing listing = claim.getListing();
            if (listing.getLatitude() != null && listing.getLongitude() != null
                    && claimantLat != null && claimantLng != null) {
                claim.setDistanceKm(distanceKm(listing.getLatitude(), listing.getLongitude(), claimantLat, claimantLng));
            }
        }
    }

    /** Straight-line distance between two lat/lng points, using the Haversine formula. */
    private double distanceKm(BigDecimal lat1, BigDecimal lon1, BigDecimal lat2, BigDecimal lon2) {
        final double R = 6371.0; // Earth's radius in km
        double dLat = Math.toRadians(lat2.doubleValue() - lat1.doubleValue());
        double dLon = Math.toRadians(lon2.doubleValue() - lon1.doubleValue());
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1.doubleValue())) * Math.cos(Math.toRadians(lat2.doubleValue()))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private FoodProvider requireProvider(Long userId) {
        return providerRepo.findByUser_Id(userId)
                .orElseThrow(() -> new IllegalStateException("Only food providers can do this."));
    }

    private void requireOwnsListing(FoodClaim claim, FoodProvider provider) {
        if (!claim.getListing().getProvider().getId().equals(provider.getId())) {
            throw new IllegalStateException("You can only manage requests for your own food listings.");
        }
    }

    // ------------------------------------------------------------------
    // Background job
    // ------------------------------------------------------------------

    /** Every minute: unclaimed food past its deadline becomes EXPIRED. */
    @Scheduled(initialDelay = 15_000, fixedRate = 60_000)
    @Transactional
    public void expireOverdueListings() {
        listingRepo.expireOverdue(FoodListing.Status.AVAILABLE,
                FoodListing.Status.EXPIRED, LocalDateTime.now());
    }
}
