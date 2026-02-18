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

    public int experienceForNextLevel() {
        // Exponential curve: early levels are quick, high levels feel earned
        // Level 1->2: 83, Level 10->11: 388, Level 50->51: ~8000
        return (int) Math.floor(level + 300 * Math.pow(2, level / 7.0)) / 4;
    }

    @Override
    public String toString() {
        return type + " (lvl " + level + ", xp " + experience + ")";
    }
}
