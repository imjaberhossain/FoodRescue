package com.foodrescue.repository;

import com.foodrescue.model.VerificationStatus;
import com.foodrescue.model.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {
    Optional<Volunteer> findByUser_Id(Long userId);
    List<Volunteer> findByVerificationStatusOrderByIdAsc(VerificationStatus status);
<<<<<<< HEAD

    /** Used to alert nearby Volunteers when a provider posts new food. */
    List<Volunteer> findByCityIgnoreCase(String city);
=======
>>>>>>> b3947b32b8c1031c4bab383c7ff83f179a94df59
}
