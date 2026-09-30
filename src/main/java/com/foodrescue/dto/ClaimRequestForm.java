package com.foodrescue.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Submitted by an NGO/Volunteer when they request a food listing. */
public class ClaimRequestForm {

    @NotBlank(message = "Please explain how and where you will distribute this food")
    @Size(max = 1000, message = "Please keep the plan under 1000 characters")
    private String distributionPlan;

    public String getDistributionPlan() { return distributionPlan; }
    public void setDistributionPlan(String distributionPlan) { this.distributionPlan = distributionPlan; }
}
