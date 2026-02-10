package com.rpg.gui;

import com.rpg.*;

import java.util.Random;

public class GameController {
    private final GameWorld world;
    private final Player player;
    private final CombatSystem combatSystem;
    private final GamePanel gamePanel;
    private final StatsPanel statsPanel;
    private final ActionPanel actionPanel;
    private final Random random = new Random();

    private boolean inCombat = false;
    private Npc currentOpponent;

    public GameController(GameWorld world, Player player, GamePanel gamePanel,
                          StatsPanel statsPanel, ActionPanel actionPanel) {
        this.world = world;
        this.player = player;
        this.combatSystem = new CombatSystem();
        this.gamePanel = gamePanel;
        this.statsPanel = statsPanel;
        this.actionPanel = actionPanel;
    }

    public void start() {
        statsPanel.setPlayer(player);
        actionPanel.showActions(world.getAvailableActions());
        actionPanel.log("You pass through the Ashen Gate into Gloamcrest Rise.");
        actionPanel.log("A fortress crowns the jagged rise. Mist coils around the chapel terraces.");
        actionPanel.log("Use WASD/Arrows to move. Press E or Space near entities to interact.");
        actionPanel.log("");
    }

    public void onPlayerMoved(int x, int y) {
        // Check for nearby entities and hint
        for (MapEntity e : gamePanel.getParent() != null ?
                ((GamePanel) gamePanel).getTileMap().getEntities() :
                java.util.List.<MapEntity>of()) {
            // handled by tooltip in panel
        }
    }

    // Called by GamePanel - need access to tileMap
    public void onPlayerMoved2(int x, int y, TileMap map) {
        // Look for adjacent entities
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                MapEntity e = map.getEntityAt(x + dx, y + dy);
                if (e != null && (dx != 0 || dy != 0)) {
                    // Show hint in status
                }
            }
        }
    }

    public void interact(int playerX, int playerY) {
        // Not used directly - interaction happens on contact
    }

    public void onEntityContact(MapEntity entity) {
        switch (entity.getType()) {
            case MapEntity.TYPE_RESOURCE:
                harvestResource(entity);
                break;
            case MapEntity.TYPE_MONSTER:
                startCombat(entity);
                break;
            case MapEntity.TYPE_NPC:
                talkToNpc(entity);
                break;
        }
    }

    private void harvestResource(MapEntity entity) {
        actionPanel.log("--- " + entity.getName() + " ---");
        actionPanel.log(entity.getDescription());

        // Find a matching skill action
        for (SkillAction action : world.getAvailableActions()) {
            if (entity.getDescription().toUpperCase().contains(action.getSkillType().name())) {
                performAction(action);
                return;
            }
        }
        actionPanel.log("You examine the node but have no matching skill action ready.");
    }

    private void startCombat(MapEntity entity) {
        inCombat = true;
        // Find matching NPC from world
        Area area = world.getAreas().get(0);
        currentOpponent = null;
        for (Npc monster : area.getMonsters()) {
            if (monster.getName().equals(entity.getName())) {
                currentOpponent = new Npc(monster.getName(), monster.getHealth(),
                        monster.getShieldDurability(), monster.getAttackPower(),
                        monster.getDefenseRating(), monster.getMagicPower(), true);
                break;
            }
        }
        if (currentOpponent == null) {
            currentOpponent = new Npc(entity.getName(), 80, 20, 14, 6, 8, true);
        }

        actionPanel.log("");
        actionPanel.log("=== COMBAT: " + entity.getName() + " ===");
        actionPanel.log(entity.getDescription());
        actionPanel.log("Choose your attack direction!");
        actionPanel.showCombatActions(entity);
    }

    private void talkToNpc(MapEntity entity) {
        actionPanel.log("");
        actionPanel.log("--- " + entity.getName() + " ---");
        actionPanel.log("\"" + entity.getDescription() + "\"");

        if (entity.getName().contains("Elowen")) {
            actionPanel.log("");
            actionPanel.log("Elowen offers you a primer on each craft skill.");
            Area area = world.getAreas().get(0);
            if (!area.getSkillTutors().isEmpty()) {
                SkillTutor tutor = area.getSkillTutors().get(0);
                for (SkillLesson lesson : tutor.getLessons()) {
                    if (player.getSkill(lesson.getSkillType()).getLevel() <= 1) {
                        actionPanel.log("  " + lesson.getSkillType() + ": " + lesson.getOverview());
                    }
                }
            }
        }
    }

    public void performAction(SkillAction action) {
        player.trainSkill(action.getSkillType(), action.getExperienceReward());
        actionPanel.log(action.getName() + " - " + action.getNarrative());
        actionPanel.log("  +" + action.getExperienceReward() + " XP in " + action.getSkillType());

        Skill skill = player.getSkill(action.getSkillType());
        actionPanel.log("  " + action.getSkillType() + " is now level " + skill.getLevel()
                + " (" + skill.getExperience() + " XP)");
        actionPanel.log("");
        statsPanel.refresh();
    }

    public void performMeleeAttack(CombatDirection direction, MapEntity monster) {
        if (!inCombat || currentOpponent == null) return;

        CombatDirection enemyDir = CombatDirection.values()[random.nextInt(4)];
        CombatResult result = combatSystem.resolveMeleeAttack(player, currentOpponent,
                direction, enemyDir);
        actionPanel.log("You strike from " + direction + ". Enemy guards " + enemyDir + ".");
        actionPanel.log("  " + result.getNarration());
        if (result.getDamageDealt() > 0) {
            actionPanel.log("  Dealt " + result.getDamageDealt() + " damage. Enemy HP: "
                    + currentOpponent.getHealth());
        }

        if (!currentOpponent.isAlive()) {
            actionPanel.log(currentOpponent.getName() + " is defeated!");
            player.trainSkill(SkillType.FIGHTING, 40);
            actionPanel.log("  +40 XP in FIGHTING");
            statsPanel.refresh();
            endCombat();
            return;
        }

        // Enemy counterattack
        enemyCounterattack();
    }

    public void performMagicAttack(CombatDirection direction, MapEntity monster) {
        if (!inCombat || currentOpponent == null) return;

        CombatDirection enemyDir = CombatDirection.values()[random.nextInt(4)];
        CombatResult result = combatSystem.resolveMagicBolt(player, currentOpponent,
                direction, enemyDir);
        actionPanel.log("You cast a bolt from " + direction + ". Enemy guards " + enemyDir + ".");
        actionPanel.log("  " + result.getNarration());
        if (result.getDamageDealt() > 0) {
            actionPanel.log("  Dealt " + result.getDamageDealt() + " damage. Enemy HP: "
                    + currentOpponent.getHealth());
        }

        if (!currentOpponent.isAlive()) {
            actionPanel.log(currentOpponent.getName() + " is defeated!");
            player.trainSkill(SkillType.MAGIC_SCHOOLS, 35);
            actionPanel.log("  +35 XP in MAGIC_SCHOOLS");
            statsPanel.refresh();
            endCombat();
            return;
        }

        enemyCounterattack();
    }

    private void enemyCounterattack() {
        CombatDirection atkDir = CombatDirection.values()[random.nextInt(4)];
        CombatDirection playerGuard = CombatDirection.values()[random.nextInt(4)];

        boolean isMagic = random.nextBoolean();
        CombatResult counter;
        if (isMagic) {
            counter = combatSystem.resolveMagicBolt(currentOpponent, player, atkDir, playerGuard);
        } else {
            counter = combatSystem.resolveMeleeAttack(currentOpponent, player, atkDir, playerGuard);
        }

        actionPanel.log("Enemy attacks from " + atkDir + "! You guard " + playerGuard + ".");
        actionPanel.log("  " + counter.getNarration());
        if (counter.getDamageDealt() > 0) {
            actionPanel.log("  You took " + counter.getDamageDealt() + " damage. HP: "
                    + player.getHealth());
        }
        actionPanel.log("");
        statsPanel.refresh();

        if (!player.isAlive()) {
            actionPanel.log("You have fallen! The mist claims your vision...");
            endCombat();
        }
    }

    public void endCombat() {
        inCombat = false;
        currentOpponent = null;
        actionPanel.showActions(world.getAvailableActions());
        actionPanel.log("--- Combat ended ---");
        actionPanel.log("");
    }
}
