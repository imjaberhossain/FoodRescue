package com.foodrescue.model;

/** How fresh the surplus food is, self-declared by the provider when posting. */
public enum QualityTag {
    COOKED("Freshly cooked", "bg-leaf-tint text-leaf-dark"),
    FEW_HOURS_OLD("A few hours old", "bg-turmeric/25 text-ink"),
    NEAR_EXPIRY("Near expiry - eat soon", "bg-chili/10 text-chili");

    private final String label;
    private final String badgeClass;

    QualityTag(String label, String badgeClass) {
        this.label = label;
        this.badgeClass = badgeClass;
    }

    public String getLabel() { return label; }
    public String getBadgeClass() { return badgeClass; }
}
