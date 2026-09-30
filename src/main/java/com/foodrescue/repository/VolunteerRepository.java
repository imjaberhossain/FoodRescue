package com.foodrescue.repository;

import com.foodrescue.model.VerificationStatus;
import com.foodrescue.model.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {
    Optional<Volunteer> findByUser_Id(Long userId);
    List<Volunteer> findByVerificationStatusOrderByIdAsc(VerificationStatus status);
}
