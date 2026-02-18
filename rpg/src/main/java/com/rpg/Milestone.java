package com.rpg;

public class Milestone {
    private final SkillType skill;
    private final int level;
    private final String title;
    private final String reward;
    private final String unlockDescription;
    private boolean claimed;

    public Milestone(SkillType skill, int level, String title, String reward, String unlockDescription) {
        this.skill = skill;
        this.level = level;
        this.title = title;
        this.reward = reward;
        this.unlockDescription = unlockDescription;
        this.claimed = false;
    }

    public SkillType getSkill() { return skill; }
    public int getLevel() { return level; }
    public String getTitle() { return title; }
    public String getReward() { return reward; }
    public String getUnlockDescription() { return unlockDescription; }
    public boolean isClaimed() { return claimed; }

    public void claim(Player player) {
        if (claimed) return;
        claimed = true;
        if (reward != null && !reward.isEmpty()) {
            player.getInventory().addItem(reward, 1);
        }
    }

    public boolean isEligible(Player player) {
        return !claimed && player.getSkill(skill).getLevel() >= level;
    }
}
