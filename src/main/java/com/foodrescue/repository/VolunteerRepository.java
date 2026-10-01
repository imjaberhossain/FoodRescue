package com.foodrescue.repository;

import com.foodrescue.model.VerificationStatus;
import com.foodrescue.model.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {
    Optional<Volunteer> findByUser_Id(Long userId);
    List<Volunteer> findByVerificationStatusOrderByIdAsc(VerificationStatus status);

    /** Used to alert nearby Volunteers when a provider posts new food. */
    List<Volunteer> findByCityIgnoreCase(String city);
}
