package com.rpg;

public interface AiAgent {
    SkillAction chooseAction(Player player, GameWorld world);
}
