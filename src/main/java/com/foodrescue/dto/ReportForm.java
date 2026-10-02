package com.foodrescue.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
<<<<<<< HEAD

=======
/**
 * Holds and validates the reason submitted when reporting a listing or claim.
 */
>>>>>>> b3947b32b8c1031c4bab383c7ff83f179a94df59
public class ReportForm {

    @NotBlank(message = "Please describe the problem")
    @Size(max = 500, message = "Please keep this under 500 characters")
    private String reason;

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
