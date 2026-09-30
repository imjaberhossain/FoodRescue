package com.foodrescue.repository;

import com.foodrescue.model.Organization;
import com.foodrescue.model.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    Optional<Organization> findByUser_Id(Long userId);
    List<Organization> findByVerificationStatusOrderByIdAsc(VerificationStatus status);
}
