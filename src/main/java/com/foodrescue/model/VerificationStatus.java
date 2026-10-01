package com.foodrescue.model;

/** Verification state for an NGO's or Volunteer's submitted NID / document. */
public enum VerificationStatus {

    PENDING("Verification pending", "bg-turmeric/25 text-ink"),
    VERIFIED("Verified", "bg-leaf-tint text-leaf-dark"),
    REJECTED("Verification rejected", "bg-chili/10 text-chili");

    private final String label;
    private final String badgeClass;

    VerificationStatus(String label, String badgeClass) {
        this.label = label;
        this.badgeClass = badgeClass;
    }

    public String getLabel() { return label; }
    public String getBadgeClass() { return badgeClass; }
}
