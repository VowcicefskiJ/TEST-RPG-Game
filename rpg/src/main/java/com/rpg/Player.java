package com.rpg;

import java.util.EnumMap;
import java.util.Map;

public class Player extends Combatant {
    private final Map<SkillType, Skill> skills;
    private final Inventory inventory;
    private int stamina;
    private int maxStamina;

    public Player(String name) {
        super(name, 120, 80, 14, 9, 13);
        this.skills = new EnumMap<>(SkillType.class);
        for (SkillType type : SkillType.values()) {
            skills.put(type, new Skill(type));
        }
        this.inventory = new Inventory();
        this.maxStamina = 100;
        this.stamina = maxStamina;
    }

    public Skill getSkill(SkillType type) {
        return skills.get(type);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public int getStamina() { return stamina; }
    public int getMaxStamina() { return maxStamina; }

    public boolean useStamina(int amount) {
        if (stamina < amount) return false;
        stamina = Math.max(0, stamina - amount);
        return true;
    }

    public void recoverStamina(int amount) {
        stamina = Math.min(maxStamina, stamina + amount);
    }

    public void trainSkill(SkillType type, int experience) {
        Skill skill = skills.get(type);
        if (skill != null) {
            int oldLevel = skill.getLevel();
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
