package com.foodrescue.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ReportForm {

    @NotBlank(message = "Please describe the problem")
    @Size(max = 500, message = "Please keep this under 500 characters")
    private String reason;

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
