package com.foodrescue.controller;

import com.foodrescue.service.LeaderboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LeaderboardController {

    // Dependency required for retrieving leaderboard rankings
    private final LeaderboardService leaderboardService;

    //// Injecting LeaderboardService via constructor
    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }
    //Handles GET requests to display the leaderboard page (/leaderboard).
    @GetMapping("/leaderboard")
    public String leaderboard(Model model) {
        model.addAttribute("topClaimants", leaderboardService.topClaimants(10));
        return "leaderboard";
    }
}
