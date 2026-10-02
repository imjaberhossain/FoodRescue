package com.foodrescue.repository;

import com.foodrescue.model.FoodClaim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodClaimRepository extends JpaRepository<FoodClaim, Long> {

    /** All requests (any status) sent for the listings of one provider... */
    List<FoodClaim> findByListing_Provider_IdOrderByClaimedAtDesc(Long providerId);

    /** The still-open (PENDING) requests on one specific listing - what the provider decides between. */
    List<FoodClaim> findByListing_IdAndStatusOrderByClaimedAtAsc(Long listingId, FoodClaim.Status status);

    /** Everything one NGO/Volunteer has requested. */
    List<FoodClaim> findByClaimantUser_IdOrderByClaimedAtDesc(Long claimantUserId);

    /** Every completed pickup - used to build the leaderboard and donor badges. */
    List<FoodClaim> findByStatus(FoodClaim.Status status);
}
