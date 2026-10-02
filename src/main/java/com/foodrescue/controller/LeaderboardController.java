package com.foodrescue.controller;

import com.foodrescue.service.LeaderboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LeaderboardController {

<<<<<<< HEAD
    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

=======
    // Dependency required for retrieving leaderboard rankings
    private final LeaderboardService leaderboardService;

    //// Injecting LeaderboardService via constructor
    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }
    //Handles GET requests to display the leaderboard page (/leaderboard).
>>>>>>> b3947b32b8c1031c4bab383c7ff83f179a94df59
    @GetMapping("/leaderboard")
    public String leaderboard(Model model) {
        model.addAttribute("topClaimants", leaderboardService.topClaimants(10));
        return "leaderboard";
    }
}
