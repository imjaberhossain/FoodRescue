package com.foodrescue.repository;

import com.foodrescue.model.Organization;
import com.foodrescue.model.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    Optional<Organization> findByUser_Id(Long userId);
    List<Organization> findByVerificationStatusOrderByIdAsc(VerificationStatus status);
<<<<<<< HEAD

    /** Used to alert nearby NGOs when a provider posts new food. */
    List<Organization> findByCityIgnoreCase(String city);
=======
>>>>>>> b3947b32b8c1031c4bab383c7ff83f179a94df59
}
