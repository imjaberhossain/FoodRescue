package com.foodrescue.repository;

import com.foodrescue.model.PickupRecord;
import org.springframework.data.jpa.repository.JpaRepository;

<<<<<<< HEAD
import java.util.Optional;

public interface PickupRecordRepository extends JpaRepository<PickupRecord, Long> {
    Optional<PickupRecord> findByClaim_Id(Long claimId);
=======
public interface PickupRecordRepository extends JpaRepository<PickupRecord, Long> {
>>>>>>> b3947b32b8c1031c4bab383c7ff83f179a94df59
}
