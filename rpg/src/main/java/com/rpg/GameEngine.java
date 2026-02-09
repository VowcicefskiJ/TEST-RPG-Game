package com.rpg;

public class GameEngine {
    private final GameWorld world;
    private final Player player;
    private final AiAgent agent;

    public GameEngine(GameWorld world, Player player, AiAgent agent) {
        this.world = world;
        this.player = player;
        this.agent = agent;
    }

    public void run(int turns) {
        for (int turn = 1; turn <= turns; turn++) {
            SkillAction action = agent.chooseAction(player, world);
            if (action == null) {
                System.out.println("No available actions to perform.");
                break;
            }
            player.trainSkill(action.getSkillType(), action.getExperienceReward());
            System.out.println("Turn " + turn + ": " + action.getName());
            System.out.println(action.getNarrative());
            System.out.println("Gained " + action.getExperienceReward() + " XP in " + action.getSkillType() + ".");
            System.out.println();
        }
        System.out.println(player.skillSummary());
    }
}
