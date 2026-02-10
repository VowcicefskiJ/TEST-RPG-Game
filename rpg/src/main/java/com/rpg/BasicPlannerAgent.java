package com.rpg;

import java.util.List;
import java.util.Random;

public class BasicPlannerAgent implements AiAgent {
    private final Random random = new Random();

    @Override
    public SkillAction chooseAction(Player player, GameWorld world) {
        List<SkillAction> actions = world.getAvailableActions();
        if (actions.isEmpty()) {
            return null;
        }
        SkillType lowestSkill = SkillType.COOKING;
        int lowestLevel = Integer.MAX_VALUE;
        for (SkillType type : SkillType.values()) {
            int level = player.getSkill(type).getLevel();
            if (level < lowestLevel) {
                lowestLevel = level;
                lowestSkill = type;
            }
        }
        SkillAction fallback = actions.get(random.nextInt(actions.size()));
        for (SkillAction action : actions) {
            if (action.getSkillType() == lowestSkill) {
                return action;
            }
        }
        return fallback;
    }
}
