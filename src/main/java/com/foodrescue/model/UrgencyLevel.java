package com.foodrescue.model;

/**
 * How urgent is it to pick up a food listing?
 * The order below is also the sorting order: CRITICAL comes first.
 * (The badge classes are Tailwind CSS classes used by the HTML pages.)
 */
public enum UrgencyLevel {

    CRITICAL("Critical", "bg-chili text-white", "bg-chili/10 text-chili"),
    HIGH("High", "bg-turmeric text-ink", "bg-turmeric/30 text-ink"),
    MEDIUM("Medium", "bg-turmeric/20 text-ink", "bg-turmeric/15 text-ink"),
    LOW("Low", "bg-leaf-tint text-leaf-dark", "bg-leaf-tint text-leaf-dark"),
    EXPIRED("Expired", "bg-gray-200 text-gray-600", "bg-gray-200 text-gray-600");

    private final String label;
    private final String stripClass;
    private final String badgeClass;

    UrgencyLevel(String label, String stripClass, String badgeClass) {
        this.label = label;
        this.stripClass = stripClass;
        this.badgeClass = badgeClass;
    }

    public String getLabel() { return label; }
    public String getStripClass() { return stripClass; }
    public String getBadgeClass() { return badgeClass; }

    /** The urgency rules. Change the numbers here to tune the priority. */
    public static UrgencyLevel fromMinutesLeft(long minutesLeft) {
        if (minutesLeft <= 0) return EXPIRED;
        if (minutesLeft <= 120) return CRITICAL;   // 2 hours or less
        if (minutesLeft <= 360) return HIGH;       // 6 hours or less
        if (minutesLeft <= 1440) return MEDIUM;    // 24 hours or less
        return LOW;
    }
}
