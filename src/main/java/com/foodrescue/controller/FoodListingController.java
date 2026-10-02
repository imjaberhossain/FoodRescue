package com.foodrescue.controller;

import com.foodrescue.dto.ClaimRequestForm;
import com.foodrescue.dto.ListingForm;
import com.foodrescue.dto.RatingForm;
import com.foodrescue.model.FoodListing;
import com.foodrescue.model.FoodProvider;
import com.foodrescue.repository.FoodCategoryRepository;
import com.foodrescue.security.SessionUser;
import com.foodrescue.service.FoodListingService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Core logic for posting food (providers) and browsing / requesting / deciding on food
 * (NGOs and Volunteers). One dashboard page shows the right screen for the logged-in role.
 */
@Controller
public class FoodListingController {

    private final FoodListingService listingService;
    private final FoodCategoryRepository categoryRepo;
    private final com.foodrescue.service.ReportService reportService;

    public FoodListingController(FoodListingService listingService,
                                 FoodCategoryRepository categoryRepo,
                                 com.foodrescue.service.ReportService reportService) {
        this.listingService = listingService;
        this.categoryRepo = categoryRepo;
        this.reportService = reportService;
    }

    // ---------- Dashboard (provider, NGO or volunteer) ----------

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(defaultValue = "") String keyword,
                            HttpSession session,
                            Model model) {
<<<<<<< HEAD
        SessionUser user = SessionUser.from(session);
        if (user == null) return "redirect:/login";

=======
        // Check if user is logged in
        SessionUser user = SessionUser.from(session);
        if (user == null) return "redirect:/login";

        // Display Provider dashboard or Claimant (NGO/Volunteer) dashboard based on role
>>>>>>> b3947b32b8c1031c4bab383c7ff83f179a94df59
        if (user.isProvider()) {
            if (!model.containsAttribute("listingForm")) {
                model.addAttribute("listingForm", newFormWithDefaults(user));
            }
            fillProviderModel(user, model);
        } else {
            if (!model.containsAttribute("claimRequestForm")) {
                model.addAttribute("claimRequestForm", new ClaimRequestForm());
            }
            fillClaimantModel(user, keyword, model);
        }
        return "dashboard";
    }

<<<<<<< HEAD
    // ---------- Provider: post food ----------
=======
// ---------- Provider: post food ----------

    /**
     * Handles new food donation submissions created by Food Providers.
     */
>>>>>>> b3947b32b8c1031c4bab383c7ff83f179a94df59

    @PostMapping("/listings")
    public String createListing(@Valid @ModelAttribute("listingForm") ListingForm form,
                                BindingResult bindingResult,
                                HttpSession session,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        SessionUser user = SessionUser.from(session);
        if (user == null) return "redirect:/login";
<<<<<<< HEAD
=======
        // Ensure only food providers can access this endpoint
>>>>>>> b3947b32b8c1031c4bab383c7ff83f179a94df59
        if (!user.isProvider()) {
            redirectAttributes.addFlashAttribute("error", "Only food providers can post food.");
            return "redirect:/dashboard";
        }

<<<<<<< HEAD
=======
        // Return to dashboard with error states if form validation fails
>>>>>>> b3947b32b8c1031c4bab383c7ff83f179a94df59
        if (bindingResult.hasErrors()) {
            fillProviderModel(user, model);
            return "dashboard";
        }

        try {
            listingService.createListing(user.getId(), form);
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            fillProviderModel(user, model);
            return "dashboard";
        }

        redirectAttributes.addFlashAttribute("success", "Your food listing is live.");
        return "redirect:/dashboard";
    }

    // ---------- Provider: decide on requests ----------

<<<<<<< HEAD
=======
    /**
     * Accepts a specific claim request for a food item and auto-rejects other pending requests.
     */

>>>>>>> b3947b32b8c1031c4bab383c7ff83f179a94df59
    @PostMapping("/claims/{id}/accept")
    public String acceptClaim(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        SessionUser user = SessionUser.from(session);
        if (user == null) return "redirect:/login";
        try {
            listingService.acceptClaim(id, user.getId());
            redirectAttributes.addFlashAttribute("success", "Request accepted. The other requests for this food were declined.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/dashboard";
    }
<<<<<<< HEAD

=======
    //Declines a claim request submitted by an NGO or volunteer.
>>>>>>> b3947b32b8c1031c4bab383c7ff83f179a94df59
    @PostMapping("/claims/{id}/reject")
    public String rejectClaim(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        SessionUser user = SessionUser.from(session);
        if (user == null) return "redirect:/login";
        try {
            listingService.rejectClaim(id, user.getId());
            redirectAttributes.addFlashAttribute("success", "Request declined.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/dashboard";
    }
<<<<<<< HEAD

=======
    /**
     * Confirms that the claimant has picked up the donated food items.
     */
>>>>>>> b3947b32b8c1031c4bab383c7ff83f179a94df59
    @PostMapping("/claims/{id}/pickup")
    public String confirmPickup(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        SessionUser user = SessionUser.from(session);
        if (user == null) return "redirect:/login";
        try {
            listingService.confirmPickup(id, user.getId());
            redirectAttributes.addFlashAttribute("success", "Pickup confirmed. You can now rate this claimant.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/dashboard";
    }
<<<<<<< HEAD

=======
    /**
     * Allows a provider to rate the claimant after a completed pickup.
     */
>>>>>>> b3947b32b8c1031c4bab383c7ff83f179a94df59
    @PostMapping("/claims/{id}/rate")
    public String rateClaim(@PathVariable Long id,
                            @Valid @ModelAttribute("ratingForm") RatingForm form,
                            BindingResult bindingResult,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        SessionUser user = SessionUser.from(session);
        if (user == null) return "redirect:/login";
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please choose a star rating from 1 to 5.");
            return "redirect:/dashboard";
        }
        try {
            listingService.rateClaimant(id, user.getId(), form);
            redirectAttributes.addFlashAttribute("success", "Thanks - your rating has been saved.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/dashboard";
    }

    // ---------- NGO / Volunteer: request food ----------

<<<<<<< HEAD
=======
    /**
     * Submits a claim request from an NGO or volunteer to request a specific food listing.
     */

>>>>>>> b3947b32b8c1031c4bab383c7ff83f179a94df59
    @PostMapping("/listings/{id}/claim")
    public String requestClaim(@PathVariable Long id,
                               @Valid @ModelAttribute("claimRequestForm") ClaimRequestForm form,
                               BindingResult bindingResult,
                               HttpSession session,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        SessionUser user = SessionUser.from(session);
        if (user == null) return "redirect:/login";

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please describe how and where you will distribute this food.");
            return "redirect:/dashboard";
        }

        try {
            listingService.requestClaim(id, user.getId(), form);
            redirectAttributes.addFlashAttribute("success",
                    "Request sent. The provider will review it and choose who gets the food.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/dashboard";
    }

    // ---------- Anyone: report a listing or a claim ----------

<<<<<<< HEAD
=======
    /**
     * Allows users to report problematic or inappropriate listings.
     */

>>>>>>> b3947b32b8c1031c4bab383c7ff83f179a94df59
    @PostMapping("/listings/{id}/report")
    public String reportListing(@PathVariable Long id,
                                @Valid @ModelAttribute("reportForm") com.foodrescue.dto.ReportForm form,
                                BindingResult bindingResult,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        SessionUser user = SessionUser.from(session);
        if (user == null) return "redirect:/login";
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please describe the problem before reporting.");
            return "redirect:/dashboard";
        }
        try {
            reportService.reportListing(id, user.getId(), form);
            redirectAttributes.addFlashAttribute("success", "Thanks - our team will review this.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/dashboard";
    }

    // ---------- NGO / Volunteer: mandatory proof after distributing the food ----------

<<<<<<< HEAD
=======
    /**
     * Allows NGOs/volunteers to upload/submit mandatory proof of food distribution after pickup.
     */
>>>>>>> b3947b32b8c1031c4bab383c7ff83f179a94df59
    @PostMapping("/claims/{id}/distribution-proof")
    public String submitDistributionProof(@PathVariable Long id,
                                          @Valid @ModelAttribute("distributionProofForm") com.foodrescue.dto.DistributionProofForm form,
                                          BindingResult bindingResult,
                                          HttpSession session,
                                          RedirectAttributes redirectAttributes) {
        SessionUser user = SessionUser.from(session);
        if (user == null) return "redirect:/login";
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please describe who received the food before submitting.");
            return "redirect:/dashboard";
        }
        try {
            listingService.submitDistributionProof(id, user.getId(), form);
            redirectAttributes.addFlashAttribute("success", "Thanks - your distribution proof has been recorded.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/dashboard";
    }

<<<<<<< HEAD
    // ---------- Anyone involved: view the food journey / timeline for a claim ----------

    @GetMapping("/claims/{id}/timeline")
    public String claimTimeline(@PathVariable Long id, HttpSession session, Model model) {
        SessionUser user = SessionUser.from(session);
        if (user == null) return "redirect:/login";
        try {
            model.addAttribute("steps", listingService.buildTimeline(id, user.getId()));
            model.addAttribute("claimId", id);
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "claim-timeline";
    }

    // ---------- Helpers ----------

=======
    // ---------- Helpers ----------

    /**
     * Pre-populates default location details for a food provider's new listing form.
     */

>>>>>>> b3947b32b8c1031c4bab383c7ff83f179a94df59
    private ListingForm newFormWithDefaults(SessionUser user) {
        ListingForm form = new ListingForm();
        listingService.findProvider(user.getId()).ifPresent((FoodProvider p) -> {
            form.setPickupLocation(p.getAddress());
            form.setCity(p.getCity());
        });
        return form;
    }
<<<<<<< HEAD

=======
    /**
     * Populates model attributes required for the Food Provider view (listings, claims, statistics).
     */
>>>>>>> b3947b32b8c1031c4bab383c7ff83f179a94df59
    private void fillProviderModel(SessionUser user, Model model) {
        List<FoodListing> listings = listingService.listingsForProvider(user.getId());
        model.addAttribute("categories", categoryRepo.findAllByOrderByNameAsc());
        model.addAttribute("myListings", listings);
        model.addAttribute("providerClaims", listingService.claimsForProvider(user.getId()));
        model.addAttribute("statTotal", listings.size());
        model.addAttribute("statAvailable", count(listings, FoodListing.Status.AVAILABLE));
        model.addAttribute("statClaimed", count(listings, FoodListing.Status.CLAIMED));
        model.addAttribute("statPickedUp", count(listings, FoodListing.Status.PICKED_UP));
    }
<<<<<<< HEAD

=======
    /**
     * Populates model attributes required for the Claimant view (browse available food, search filters, my claims).
     */
>>>>>>> b3947b32b8c1031c4bab383c7ff83f179a94df59
    private void fillClaimantModel(SessionUser user, String keyword, Model model) {
        model.addAttribute("keyword", keyword);
        model.addAttribute("availableListings", listingService.browseAvailable(keyword));
        model.addAttribute("myClaims", listingService.claimsForClaimant(user.getId()));
    }
<<<<<<< HEAD

=======
    /**
     * Helper method to count food listings matching a specific status (AVAILABLE, CLAIMED, PICKED_UP).
     */
>>>>>>> b3947b32b8c1031c4bab383c7ff83f179a94df59
    private long count(List<FoodListing> listings, FoodListing.Status status) {
        return listings.stream().filter(l -> l.getStatus() == status).count();
    }
}
