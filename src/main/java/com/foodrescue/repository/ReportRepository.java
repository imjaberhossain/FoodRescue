package com.foodrescue.repository;

import com.foodrescue.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByStatusOrderByCreatedAtDesc(Report.Status status);
    List<Report> findAllByOrderByCreatedAtDesc();
}
