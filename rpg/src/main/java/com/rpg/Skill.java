package com.rpg;

public class Skill {
    private final SkillType type;
    private int level;
    private int experience;

    public Skill(SkillType type) {
        this.type = type;
        this.level = 1;
        this.experience = 0;
    }

    public SkillType getType() {
        return type;
    }

    public int getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
    }

    public void addExperience(int amount) {
        if (amount <= 0) {
            return;
        }
        experience += amount;
        while (experience >= experienceForNextLevel()) {
            experience -= experienceForNextLevel();
            level += 1;
        }
    }

    private int experienceForNextLevel() {
        return 100 + (level - 1) * 25;
    }

    @Override
    public String toString() {
        return type + " (lvl " + level + ", xp " + experience + ")";
    }
}
