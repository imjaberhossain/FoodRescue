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
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final ReportService reportService;

    public AdminController(AdminService adminService, ReportService reportService) {
        this.adminService = adminService;
        this.reportService = reportService;
    }

    @GetMapping("/verifications")
    public String verifications(HttpSession session, Model model) {
        String guard = requireAdmin(session);
        if (guard != null) return guard;

        model.addAttribute("pending", adminService.pendingVerifications());
        return "admin-verifications";
    }

    @PostMapping("/verifications/organization/{id}")
    public String decideOrganization(@PathVariable Long id, @RequestParam VerificationStatus decision,
                                     HttpSession session, RedirectAttributes redirectAttributes) {
        String guard = requireAdmin(session);
        if (guard != null) return guard;
        adminService.decideOrganization(id, decision);
        redirectAttributes.addFlashAttribute("success", "NGO verification updated.");
        return "redirect:/admin/verifications";
    }

    @PostMapping("/verifications/volunteer/{id}")
    public String decideVolunteer(@PathVariable Long id, @RequestParam VerificationStatus decision,
                                  HttpSession session, RedirectAttributes redirectAttributes) {
        String guard = requireAdmin(session);
        if (guard != null) return guard;
        adminService.decideVolunteer(id, decision);
        redirectAttributes.addFlashAttribute("success", "Volunteer verification updated.");
        return "redirect:/admin/verifications";
    }

    @GetMapping("/reports")
    public String reports(HttpSession session, Model model) {
        String guard = requireAdmin(session);
        if (guard != null) return guard;
        model.addAttribute("reports", reportService.allReportsNewestFirst());
        return "admin-reports";
    }

    @PostMapping("/reports/{id}")
    public String decideReport(@PathVariable Long id, @RequestParam("decision") String decision,
                               HttpSession session, RedirectAttributes redirectAttributes) {
        String guard = requireAdmin(session);
        if (guard != null) return guard;
        reportService.markReviewed(id, com.foodrescue.model.Report.Status.valueOf(decision));
        redirectAttributes.addFlashAttribute("success", "Report updated.");
        return "redirect:/admin/reports";
    }

    /** Returns a redirect view name if the user is not an admin, or null if they may proceed. */
    private String requireAdmin(HttpSession session) {
        SessionUser user = SessionUser.from(session);
        if (user == null) return "redirect:/login";
        if (!user.isAdmin()) return "redirect:/dashboard";
        return null;
    }
}
