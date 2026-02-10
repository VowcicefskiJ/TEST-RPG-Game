package com.rpg;

public class Spell {
    private final String name;
    private final MagicSchool school;
    private final int requiredLevel;
    private final int manaCost;
    private final int basePower;
    private final double powerScaling;
    private final int durationSeconds;
    private final String effect;

    public Spell(
            String name,
            MagicSchool school,
            int requiredLevel,
            int manaCost,
            int basePower,
            double powerScaling,
            int durationSeconds,
            String effect
    ) {
        this.name = name;
        this.school = school;
        this.requiredLevel = requiredLevel;
        this.manaCost = manaCost;
        this.basePower = basePower;
        this.powerScaling = powerScaling;
        this.durationSeconds = durationSeconds;
        this.effect = effect;
    }

    public String getName() {
        return name;
    }

    public MagicSchool getSchool() {
        return school;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public int getManaCost() {
        return manaCost;
    }

    public int getBasePower() {
        return basePower;
    }

    public double getPowerScaling() {
        return powerScaling;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public String getEffect() {
        return effect;
    }

    @Override
    public String toString() {
        return name + " (" + school + ")";
    }
}
