package com.foodrescue.controller;

import com.foodrescue.model.VerificationStatus;
import com.foodrescue.security.SessionUser;
import com.foodrescue.service.AdminService;
import com.foodrescue.service.ReportService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Admin-only screens: approve/reject NGO & Volunteer NID/document verification,
 * and review user-submitted reports (scam / fake post / misuse flags).
 * Access is gated on SessionUser.isAdmin(), which only reflects users.is_admin in
 * the database - there is no sign-up path that can set this flag (see schema.sql).
 */
@Controller
@RequestMapping("/admin") // Base URL mapping for all admin routes
public class AdminController {

    // Service dependencies for administrative tasks and safety reporting
    private final AdminService adminService;
    private final ReportService reportService;

    // Constructor injection
    public AdminController(AdminService adminService, ReportService reportService) {
        this.adminService = adminService;
        this.reportService = reportService;
    }

    // ---------- Verification Management ----------

    /**
     * Renders the page listing all pending verification requests for NGOs and Volunteers (/admin/verifications).
     */
    @GetMapping("/verifications")
    public String verifications(HttpSession session, Model model) {
        // Enforce admin authorization check
        String guard = requireAdmin(session);
        if (guard != null) return guard;

        // Retrieve and load pending organization and volunteer verification applications
        model.addAttribute("pending", adminService.pendingVerifications());
        return "admin-verifications"; // Renders admin-verifications.html
    }

    /**
     * Updates verification status (APPROVED / REJECTED) for an NGO/Organization.
     */
    @PostMapping("/verifications/organization/{id}")
    public String decideOrganization(@PathVariable Long id, @RequestParam VerificationStatus decision,
                                     HttpSession session, RedirectAttributes redirectAttributes) {
        String guard = requireAdmin(session);
        if (guard != null) return guard;

        adminService.decideOrganization(id, decision);
        redirectAttributes.addFlashAttribute("success", "NGO verification updated.");
        return "redirect:/admin/verifications";
    }

    /**
     * Updates verification status (APPROVED / REJECTED) for an individual Volunteer.
     */
    @PostMapping("/verifications/volunteer/{id}")
    public String decideVolunteer(@PathVariable Long id, @RequestParam VerificationStatus decision,
                                  HttpSession session, RedirectAttributes redirectAttributes) {
        String guard = requireAdmin(session);
        if (guard != null) return guard;

        adminService.decideVolunteer(id, decision);
        redirectAttributes.addFlashAttribute("success", "Volunteer verification updated.");
        return "redirect:/admin/verifications";
    }

    // ---------- Report Review ----------

    /**
     * Displays all platform misuse or safety reports submitted by users (/admin/reports).
     */
    @GetMapping("/reports")
    public String reports(HttpSession session, Model model) {
        String guard = requireAdmin(session);
        if (guard != null) return guard;

        // Fetch user reports ordered with the newest first
        model.addAttribute("reports", reportService.allReportsNewestFirst());
        return "admin-reports"; // Renders admin-reports.html
    }

    /**
     * Resolves a user-submitted report by marking its status (e.g., RESOLVED, DISMISSED).
     */
    @PostMapping("/reports/{id}")
    public String decideReport(@PathVariable Long id, @RequestParam("decision") String decision,
                               HttpSession session, RedirectAttributes redirectAttributes) {
        String guard = requireAdmin(session);
        if (guard != null) return guard;

        reportService.markReviewed(id, com.foodrescue.model.Report.Status.valueOf(decision));
        redirectAttributes.addFlashAttribute("success", "Report updated.");
        return "redirect:/admin/reports";
    }

    // ---------- Helper Security Guard ----------

    /**
     * Helper method to verify administrator authorization.
     *
     * @return Redirect view string if unauthenticated or non-admin; null if authorized.
     */
    private String requireAdmin(HttpSession session) {
        SessionUser user = SessionUser.from(session);
        if (user == null) return "redirect:/login"; // Redirect unauthenticated users
        if (!user.isAdmin()) return "redirect:/dashboard"; // Redirect regular users away from admin pages
        return null; // Proceed normal execution
    }
}