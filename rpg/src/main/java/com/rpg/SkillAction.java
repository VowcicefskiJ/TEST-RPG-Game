package com.rpg;

public class SkillAction {
    private final String name;
    private final SkillType skillType;
    private final int experienceReward;
    private final String narrative;

    public SkillAction(String name, SkillType skillType, int experienceReward, String narrative) {
        this.name = name;
        this.skillType = skillType;
        this.experienceReward = experienceReward;
        this.narrative = narrative;
    }

    public String getName() {
        return name;
    }

    public SkillType getSkillType() {
        return skillType;
    }

    public int getExperienceReward() {
        return experienceReward;
    }

    public String getNarrative() {
        return narrative;
    }
}
