package com.foodrescue.controller;

import com.foodrescue.model.FoodListing;
import com.foodrescue.service.FoodListingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final FoodListingService listingService;

    public HomeController(FoodListingService listingService) {
        this.listingService = listingService;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<FoodListing> available = listingService.browseAvailable("");
        model.addAttribute("previewListings", available.stream().limit(5).toList());
        model.addAttribute("availableCount", available.size());
        return "index";
    }
}
