package com.foodrescue.service;

import com.foodrescue.model.FoodClaim;
import com.foodrescue.model.FoodListing;
import com.foodrescue.model.User;
import com.foodrescue.repository.FoodClaimRepository;
import com.foodrescue.repository.FoodListingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Simple, honest leaderboard: counts finished work (completed pickups for NGOs/Volunteers,
 * picked-up listings for Providers) directly from existing data - no extra "points" table needed.
 */
@Service
public class LeaderboardService {

    /** A Provider earns the "Recurring Donor" badge once they hit this many picked-up donations. */
    public static final int RECURRING_DONOR_THRESHOLD = 5;

    private final FoodClaimRepository claimRepo;
    private final FoodListingRepository listingRepo;

    public LeaderboardService(FoodClaimRepository claimRepo, FoodListingRepository listingRepo) {
        this.claimRepo = claimRepo;
        this.listingRepo = listingRepo;
    }

    /** Top NGOs/Volunteers by number of completed (picked-up) food claims. */
    @Transactional(readOnly = true)
    public List<Entry> topClaimants(int limit) {
        List<FoodClaim> completed = claimRepo.findByStatus(FoodClaim.Status.COMPLETED);

        Map<User, Long> counts = completed.stream()
                .collect(Collectors.groupingBy(FoodClaim::getClaimantUser, Collectors.counting()));

        return counts.entrySet().stream()
                .sorted(Map.Entry.<User, Long>comparingByValue().reversed())
                .limit(limit)
                .map(e -> new Entry(e.getKey().getFullName(), e.getKey().getRole().name(), e.getValue()))
                .toList();
    }

    /** How many donations (picked-up listings) this provider has made - used for the donor badge. */
    @Transactional(readOnly = true)
    public long completedDonationsFor(Long providerId) {
        return listingRepo.findByStatus(FoodListing.Status.PICKED_UP).stream()
                .filter(l -> l.getProvider().getId().equals(providerId))
                .count();
    }

    public record Entry(String name, String role, long completedPickups) {}
}
