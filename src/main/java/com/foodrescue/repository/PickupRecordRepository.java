package com.foodrescue.repository;

import com.foodrescue.model.PickupRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PickupRecordRepository extends JpaRepository<PickupRecord, Long> {
    Optional<PickupRecord> findByClaim_Id(Long claimId);
}
