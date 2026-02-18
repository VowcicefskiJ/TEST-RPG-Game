package com.rpg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class QuestLog {
    private final List<Quest> quests = new ArrayList<>();

    public void addQuest(Quest quest) {
        quests.add(quest);
    }

    public List<Quest> getActiveQuests() {
        return quests.stream()
                .filter(q -> q.getStatus() == Quest.Status.ACTIVE)
                .collect(Collectors.toUnmodifiableList());
    }

    public List<Quest> getAvailableQuests() {
        return quests.stream()
                .filter(q -> q.getStatus() == Quest.Status.AVAILABLE)
                .collect(Collectors.toUnmodifiableList());
    }

    public List<Quest> getCompletedQuests() {
        return quests.stream()
                .filter(q -> q.getStatus() == Quest.Status.COMPLETED)
                .collect(Collectors.toUnmodifiableList());
    }

    public List<Quest> getAllQuests() {
        return Collections.unmodifiableList(quests);
    }

    public void onSkillTrained(SkillType type, int amount) {
        for (Quest quest : getActiveQuests()) {
            for (Quest.QuestGoal goal : quest.getGoals()) {
                if (goal.getType() == Quest.GoalType.TRAIN_SKILL
                        && goal.getTarget().equals(type.name())) {
                    goal.addProgress(amount);
                }
            }
        }
    }

    public void onMonsterDefeated(String monsterName) {
        for (Quest quest : getActiveQuests()) {
            for (Quest.QuestGoal goal : quest.getGoals()) {
                if (goal.getType() == Quest.GoalType.DEFEAT_MONSTER
                        && goal.getTarget().equals(monsterName)) {
                    goal.addProgress(1);
                }
            }
        }
    }

    public void onItemGathered(String itemName, int amount) {
        for (Quest quest : getActiveQuests()) {
            for (Quest.QuestGoal goal : quest.getGoals()) {
                if (goal.getType() == Quest.GoalType.GATHER_ITEM
                        && goal.getTarget().equals(itemName)) {
                    goal.addProgress(amount);
                }
            }
        }
    }

    public void onNpcTalkedTo(String npcName) {
        for (Quest quest : getActiveQuests()) {
            for (Quest.QuestGoal goal : quest.getGoals()) {
                if (goal.getType() == Quest.GoalType.TALK_TO_NPC
                        && goal.getTarget().equals(npcName)) {
                    goal.addProgress(1);
                }
            }
        }
    }
}
