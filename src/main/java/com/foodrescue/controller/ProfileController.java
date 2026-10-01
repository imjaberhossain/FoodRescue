package com.foodrescue.controller;

import com.foodrescue.dto.ProfileForm;
import com.foodrescue.security.SessionUser;
import com.foodrescue.service.ProfileService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {

    private final ProfileService profileService;
    private final com.foodrescue.service.LeaderboardService leaderboardService;
    private final com.foodrescue.repository.FoodProviderRepository providerRepo;

    public ProfileController(ProfileService profileService,
                             com.foodrescue.service.LeaderboardService leaderboardService,
                             com.foodrescue.repository.FoodProviderRepository providerRepo) {
        this.profileService = profileService;
        this.leaderboardService = leaderboardService;
        this.providerRepo = providerRepo;
    }
    //Handles GET requests to display the user profile page (/profile).
    @GetMapping("/profile")
    public String viewProfile(HttpSession session, Model model) {
        SessionUser user = SessionUser.from(session);//check session
        if (user == null) return "redirect:/login";

            //// 2. Fetch profile data and add it to the view model
        model.addAttribute("profile", profileService.loadProfile(user.getId()));
        if (!model.containsAttribute("profileForm")) { //profile deta load
            model.addAttribute("profileForm", new ProfileForm());
        }

        if (user.isProvider()) {
            providerRepo.findByUser_Id(user.getId()).ifPresent(p -> {
                long donations = leaderboardService.completedDonationsFor(p.getId());
                model.addAttribute("recurringDonor", donations >= com.foodrescue.service.LeaderboardService.RECURRING_DONOR_THRESHOLD);
            });
        }
        return "profile";
    }
  //Handles POST requests for submitting profile updates (/profile).
    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("profileForm") ProfileForm form,
                                BindingResult bindingResult,
                                HttpSession session,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        //  Verify user authentication session
        SessionUser user = SessionUser.from(session);
        if (user == null) return "redirect:/login";

        if (bindingResult.hasErrors()) {
            model.addAttribute("profile", profileService.loadProfile(user.getId()));
            return "profile";
        }

        profileService.updateProfile(user.getId(), form);
        redirectAttributes.addFlashAttribute("success", "Your profile has been updated.");
        return "redirect:/profile";
    }
}
