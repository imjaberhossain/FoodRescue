package com.foodrescue.service;

import com.foodrescue.dto.ReportForm;
import com.foodrescue.model.FoodClaim;
import com.foodrescue.model.FoodListing;
import com.foodrescue.model.Report;
import com.foodrescue.model.User;
import com.foodrescue.repository.FoodClaimRepository;
import com.foodrescue.repository.FoodListingRepository;
import com.foodrescue.repository.ReportRepository;
import com.foodrescue.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** "Flag this" - lets any logged-in user report a listing or a claim as a scam, fake, or misuse. */
@Service
public class ReportService {

    private final ReportRepository reportRepo;
    private final FoodListingRepository listingRepo;
    private final FoodClaimRepository claimRepo;
    private final UserRepository userRepo;

    public ReportService(ReportRepository reportRepo, FoodListingRepository listingRepo,
                         FoodClaimRepository claimRepo, UserRepository userRepo) {
        this.reportRepo = reportRepo;
        this.listingRepo = listingRepo;
        this.claimRepo = claimRepo;
        this.userRepo = userRepo;
    }

    @Transactional
    public void reportListing(Long listingId, Long reporterUserId, ReportForm form) {
        FoodListing listing = listingRepo.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found."));
        Report report = new Report();
        report.setReporterUser(requireUser(reporterUserId));
        report.setListing(listing);
        report.setReason(form.getReason().trim());
        reportRepo.save(report);
    }

    @Transactional
    public void reportClaim(Long claimId, Long reporterUserId, ReportForm form) {
        FoodClaim claim = claimRepo.findById(claimId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found."));
        Report report = new Report();
        report.setReporterUser(requireUser(reporterUserId));
        report.setClaim(claim);
        report.setReason(form.getReason().trim());
        reportRepo.save(report);
    }

    @Transactional(readOnly = true)
    public List<Report> allReportsNewestFirst() {
        return reportRepo.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public void markReviewed(Long reportId, Report.Status decision) {
        Report report = reportRepo.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found."));
        report.setStatus(decision);
    }

    private User requireUser(Long userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new IllegalStateException("Your account could not be found. Please log in again."));
    }
}
