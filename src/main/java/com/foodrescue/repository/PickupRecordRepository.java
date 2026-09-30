package com.foodrescue.repository;

import com.foodrescue.model.PickupRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PickupRecordRepository extends JpaRepository<PickupRecord, Long> {
}
