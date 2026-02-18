package com.rpg;

import java.util.List;
import java.util.Map;

public class Quest {
    public enum Status { AVAILABLE, ACTIVE, COMPLETED }
    public enum GoalType { TRAIN_SKILL, DEFEAT_MONSTER, GATHER_ITEM, CRAFT_ITEM, TALK_TO_NPC }

    private final String id;
    private final String name;
    private final String description;
    private final List<QuestGoal> goals;
    private final List<QuestReward> rewards;
    private final String completionNarrative;
    private Status status;

    public Quest(String id, String name, String description, List<QuestGoal> goals,
                 List<QuestReward> rewards, String completionNarrative) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.goals = List.copyOf(goals);
        this.rewards = List.copyOf(rewards);
        this.completionNarrative = completionNarrative;
        this.status = Status.AVAILABLE;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public List<QuestGoal> getGoals() { return goals; }
    public List<QuestReward> getRewards() { return rewards; }
    public String getCompletionNarrative() { return completionNarrative; }
    public Status getStatus() { return status; }

    public void activate() { status = Status.ACTIVE; }

    public boolean checkCompletion() {
        for (QuestGoal goal : goals) {
            if (!goal.isComplete()) return false;
        }
        return true;
    }

    public void complete(Player player) {
        status = Status.COMPLETED;
        for (QuestReward reward : rewards) {
            reward.grant(player);
        }
    }

    public static class QuestGoal {
        private final GoalType type;
        private final String target;
        private final int required;
        private int progress;

        public QuestGoal(GoalType type, String target, int required) {
            this.type = type;
            this.target = target;
            this.required = required;
            this.progress = 0;
        }

        public GoalType getType() { return type; }
        public String getTarget() { return target; }
        public int getRequired() { return required; }
        public int getProgress() { return progress; }
        public boolean isComplete() { return progress >= required; }

        public void addProgress(int amount) {
            progress = Math.min(progress + amount, required);
        }

        public String describe() {
            return target + ": " + progress + "/" + required;
        }
    }

    public static class QuestReward {
        private final String type; // "xp", "item", "gold"
        private final String target;
        private final int amount;

        public QuestReward(String type, String target, int amount) {
            this.type = type;
            this.target = target;
            this.amount = amount;
        }

        public String getType() { return type; }
        public String getTarget() { return target; }
        public int getAmount() { return amount; }

        public void grant(Player player) {
            switch (type) {
                case "xp":
                    try {
                        SkillType skill = SkillType.valueOf(target);
                        player.trainSkill(skill, amount);
                    } catch (IllegalArgumentException ignored) {}
                    break;
                case "item":
                    player.getInventory().addItem(target, amount);
                    break;
            }
        }

        public String describe() {
            return amount + " " + target + (type.equals("xp") ? " XP" : "");
        }
    }
}
