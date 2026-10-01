package com.foodrescue.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

/** Mandatory report + photo the claimant submits after actually distributing the food. */
public class DistributionProofForm {

    @NotBlank(message = "Please describe who received the food and where")
    @Size(max = 1000, message = "Please keep this under 1000 characters")
    private String report;

    private MultipartFile proofPhoto;

    public String getReport() { return report; }
    public void setReport(String report) { this.report = report; }
    public MultipartFile getProofPhoto() { return proofPhoto; }
    public void setProofPhoto(MultipartFile proofPhoto) { this.proofPhoto = proofPhoto; }
}
