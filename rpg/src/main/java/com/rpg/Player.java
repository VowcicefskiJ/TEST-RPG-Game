package com.rpg;

import java.util.EnumMap;
import java.util.Map;

public class Player extends Combatant {
    private final Map<SkillType, Skill> skills;

    public Player(String name) {
        super(name, 120, 80, 14, 9, 13);
        this.skills = new EnumMap<>(SkillType.class);
        for (SkillType type : SkillType.values()) {
            skills.put(type, new Skill(type));
        }
    }

    public Skill getSkill(SkillType type) {
        return skills.get(type);
    }

    public void trainSkill(SkillType type, int experience) {
        Skill skill = skills.get(type);
        if (skill != null) {
            skill.addExperience(experience);
        }
    }

    public String skillSummary() {
        StringBuilder builder = new StringBuilder();
        builder.append("Skills for ").append(getName()).append(":\n");
        for (SkillType type : SkillType.values()) {
            builder.append("- ").append(skills.get(type)).append("\n");
        }
        return builder.toString();
    }

    @Override
    public int adjustDebuffDuration(int baseDurationSeconds, boolean usingShield) {
        if (baseDurationSeconds <= 0) {
            return 0;
        }
        if (!usingShield) {
            return baseDurationSeconds;
        }
        return Math.max(1, (int) Math.round(baseDurationSeconds * 0.7));
    }
}
