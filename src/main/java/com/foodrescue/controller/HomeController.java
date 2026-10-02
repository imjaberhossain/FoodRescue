package com.foodrescue.controller;

import com.foodrescue.model.FoodListing;
import com.foodrescue.service.FoodListingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

<<<<<<< HEAD
=======
    // Service dependency for retrieving food listings
>>>>>>> b3947b32b8c1031c4bab383c7ff83f179a94df59
    private final FoodListingService listingService;

    public HomeController(FoodListingService listingService) {
        this.listingService = listingService;
    }

<<<<<<< HEAD
=======
    //Handles GET requests for the home/landing page (/).
>>>>>>> b3947b32b8c1031c4bab383c7ff83f179a94df59
    @GetMapping("/")
    public String index(Model model) {
        List<FoodListing> available = listingService.browseAvailable("");
        model.addAttribute("previewListings", available.stream().limit(5).toList());
        model.addAttribute("availableCount", available.size());
        return "index";
    }
}
