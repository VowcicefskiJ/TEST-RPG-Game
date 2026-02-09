package com.rpg;

public class AnimationStep {
    private final String description;
    private final String[] requiredItems;
    private final double durationSeconds;

    public AnimationStep(String description, String[] requiredItems, double durationSeconds) {
        this.description = description;
        this.requiredItems = requiredItems.clone();
        this.durationSeconds = durationSeconds;
    }

    public String getDescription() {
        return description;
    }

    public String[] getRequiredItems() {
        return requiredItems.clone();
    }

    public double getDurationSeconds() {
        return durationSeconds;
    }
}
