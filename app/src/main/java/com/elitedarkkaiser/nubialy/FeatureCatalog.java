package com.elitedarkkaiser.nubialy;

import java.util.List;

/** Catalog entries are proposals, never commands. No engine or module is installed in this milestone. */
public final class FeatureCatalog {
    public enum State { PLANNED, BLOCKED }
    public record Feature(String id, String title, String description, State state) {}
    public static final List<Feature> FEATURES = List.of(
        new Feature("status_bar", "Status bar", "Clock, mobile service icons and battery appearance.", State.PLANNED),
        new Feature("quick_settings", "Quick Settings", "Independent large and small tile shapes, labels and placement.", State.PLANNED),
        new Feature("lock_screen", "Lock screen", "Clock and date visibility, size and placement.", State.PLANNED),
        new Feature("notifications", "Notifications", "Explore notification panel styling.", State.PLANNED),
        new Feature("qs_four_rows", "Four-row Quick Settings", "This layout has not passed compatibility validation. No row override is available.", State.BLOCKED)
    );
    private FeatureCatalog() {}
    public static boolean canApply(String featureId, CompatibilityPolicy.Status status) {
        // Recovery and a feature-specific validated implementation must precede any apply path.
        return false;
    }
}
