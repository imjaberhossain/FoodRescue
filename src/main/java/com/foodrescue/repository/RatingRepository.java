package com.foodrescue.repository;

import com.foodrescue.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    Optional<Rating> findByClaim_Id(Long claimId);

    List<Rating> findByRatedUser_IdOrderByCreatedAtDesc(Long ratedUserId);

    @Query("select avg(r.ratingValue) from Rating r where r.ratedUser.id = :userId")
    Double averageRatingFor(@Param("userId") Long userId);

    long countByRatedUser_Id(Long userId);
}
