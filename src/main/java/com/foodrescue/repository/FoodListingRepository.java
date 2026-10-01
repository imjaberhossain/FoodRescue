package com.foodrescue.repository;

import com.foodrescue.model.FoodListing;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FoodListingRepository extends JpaRepository<FoodListing, Long> {

    /** Available listings that are not expired, filtered by city or pickup location text. */
    @Query("""
            select l from FoodListing l
            where l.status = :status
              and l.pickupDeadline > :now
              and (lower(l.city) like lower(concat('%', :keyword, '%'))
                   or lower(l.pickupLocation) like lower(concat('%', :keyword, '%')))
            """)
    List<FoodListing> searchAvailable(@Param("status") FoodListing.Status status,
                                      @Param("now") LocalDateTime now,
                                      @Param("keyword") String keyword);

    List<FoodListing> findByProvider_IdOrderByCreatedAtDesc(Long providerId);

    /** Used for leaderboard-style aggregates, e.g. counting each provider's picked-up listings. */
    List<FoodListing> findByStatus(FoodListing.Status status);

    /** Locks the row so two NGOs cannot claim the same food at the same moment. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from FoodListing l where l.id = :id")
    Optional<FoodListing> findByIdForUpdate(@Param("id") Long id);

    /** Marks old, unclaimed listings as EXPIRED. */
    @Modifying
    @Query("""
            update FoodListing l set l.status = :expired
            where l.status = :available and l.pickupDeadline <= :now
            """)
    int expireOverdue(@Param("available") FoodListing.Status available,
                      @Param("expired") FoodListing.Status expired,
                      @Param("now") LocalDateTime now);
}
