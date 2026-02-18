package com.rpg.gui;

import com.rpg.*;

import java.util.List;
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
    private MapEntity currentCombatEntity;

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
        actionPanel.log("Use WASD/Arrows to move. Walk into entities to interact.");
        actionPanel.log("");

        // Show active quest
        List<Quest> active = world.getQuestLog().getActiveQuests();
        if (!active.isEmpty()) {
            Quest q = active.get(0);
            actionPanel.log("[QUEST] " + q.getName() + ": " + q.getDescription());
            actionPanel.log("");
        }
    }

    public void onPlayerMoved(int x, int y) {
        // Context-aware: show only nearby relevant actions
        TileMap map = gamePanel.getTileMap();
        if (map == null) return;

        // Check for adjacent resource nodes and show context actions
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                MapEntity e = map.getEntityAt(x + dx, y + dy);
                if (e != null && e.getType() == MapEntity.TYPE_RESOURCE) {
                    showContextActions(e);
                    return;
                }
            }
        }
        // Default: show all actions
        if (!inCombat) {
            actionPanel.showActions(world.getAvailableActions());
        }
    }

    private void showContextActions(MapEntity resourceEntity) {
        // Find the skill type this resource matches
        Area area = world.getAreas().get(0);
        for (ResourceNode node : area.getResources()) {
            if (node.getName().equals(resourceEntity.getName())) {
                List<SkillAction> contextActions = world.getActionsForSkill(node.getSkillType());
                if (!contextActions.isEmpty()) {
                    actionPanel.showActions(contextActions);
                    return;
                }
            }
        }
        actionPanel.showActions(world.getAvailableActions());
    }

    public void interact(int playerX, int playerY) {}

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

        // Find the matching resource node to get the item
        Area area = world.getAreas().get(0);
        for (ResourceNode node : area.getResources()) {
            if (node.getName().equals(entity.getName())) {
                String item = node.getResourceItem();
                player.getInventory().addItem(item, 1);
                actionPanel.log("  Gathered: " + item);

                // Train the matching skill
                List<SkillAction> actions = world.getActionsForSkill(node.getSkillType());
                if (!actions.isEmpty()) {
                    SkillAction action = actions.get(0);
                    player.trainSkill(action.getSkillType(), action.getExperienceReward());
                    actionPanel.log("  +" + action.getExperienceReward() + " XP in " + action.getSkillType());
                }

                // Update quest progress
                world.getQuestLog().onItemGathered(item, 1);
                world.getQuestLog().onSkillTrained(node.getSkillType(), 1);

                checkQuestCompletion();
                checkMilestones();
                statsPanel.refresh();
                showInventorySnapshot();
                actionPanel.log("");
                return;
            }
        }
        actionPanel.log("You examine the node but find nothing to gather.");
    }

    private void startCombat(MapEntity entity) {
        inCombat = true;
        currentCombatEntity = entity;
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
        actionPanel.log("Stamina: " + player.getStamina() + "/" + player.getMaxStamina());
        actionPanel.log("Choose your attack! (Vary directions for combos, use Feint to trick)");
        actionPanel.showCombatActions(entity);
    }

    private void talkToNpc(MapEntity entity) {
        actionPanel.log("");
        actionPanel.log("--- " + entity.getName() + " ---");
        actionPanel.log("\"" + entity.getDescription() + "\"");

        // Quest tracking
        world.getQuestLog().onNpcTalkedTo(entity.getName());

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

            // Show available quests
            List<Quest> available = world.getQuestLog().getAvailableQuests();
            if (!available.isEmpty()) {
                actionPanel.log("");
                actionPanel.log("Elowen has quests for you:");
                for (Quest q : available) {
                    q.activate();
                    actionPanel.log("  [NEW QUEST] " + q.getName() + ": " + q.getDescription());
                    for (Quest.QuestGoal goal : q.getGoals()) {
                        actionPanel.log("    - " + goal.describe());
                    }
                }
            }

            // Show craftable recipes
            List<Recipe> craftable = getCraftableRecipes();
            if (!craftable.isEmpty()) {
                actionPanel.log("");
                actionPanel.log("Recipes you can craft:");
                for (Recipe r : craftable) {
                    actionPanel.log("  [CRAFT] " + r.getName() + " -> " + r.getResultItem());
                }
            }
        }

        if (entity.getName().contains("Auction")) {
            actionPanel.log("");
            actionPanel.log("Your inventory:");
            showInventoryFull();
        }

        checkQuestCompletion();
    }

    public void performAction(SkillAction action) {
        player.trainSkill(action.getSkillType(), action.getExperienceReward());
        actionPanel.log(action.getName() + " - " + action.getNarrative());
        actionPanel.log("  +" + action.getExperienceReward() + " XP in " + action.getSkillType());

        Skill skill = player.getSkill(action.getSkillType());
        actionPanel.log("  " + action.getSkillType() + " Lv " + skill.getLevel()
                + " (" + skill.getExperience() + "/" + skill.experienceForNextLevel() + " XP)");

        world.getQuestLog().onSkillTrained(action.getSkillType(), action.getExperienceReward());
        checkQuestCompletion();
        checkMilestones();

        // Auto-craft available recipes after gathering
        autoCraft();

        actionPanel.log("");
        statsPanel.refresh();
    }

    public void performMeleeAttack(CombatDirection direction, MapEntity monster) {
        if (!inCombat || currentOpponent == null) return;

        if (!player.useStamina(combatSystem.getMeleeStaminaCost())) {
            actionPanel.log("Not enough stamina! Rest or retreat.");
            return;
        }

        // Tick bleed on opponent
        if (currentOpponent.getBleedTurns() > 0) {
            int oldHp = currentOpponent.getHealth();
            currentOpponent.tickStatusEffects();
            actionPanel.log("  " + currentOpponent.getName() + " bleeds for "
                    + (oldHp - currentOpponent.getHealth()) + " damage.");
        }

        CombatDirection enemyDir = CombatDirection.values()[random.nextInt(4)];
        CombatResult result = combatSystem.resolveMeleeAttack(player, currentOpponent,
                direction, enemyDir);
        actionPanel.log("You strike " + direction + ". Enemy guards " + enemyDir
                + ". [Stamina: " + player.getStamina() + "]");
        actionPanel.log("  " + result.getNarration());
        if (result.getDamageDealt() > 0) {
            actionPanel.log("  Enemy HP: " + currentOpponent.getHealth() + "/" + currentOpponent.getMaxHealth());
        }

        if (!currentOpponent.isAlive()) {
            onMonsterDefeated();
            return;
        }

        if (currentOpponent.wantsToFlee()) {
            actionPanel.log(currentOpponent.getName() + " cowers and tries to flee!");
            actionPanel.log("You finish it off as it turns to run.");
            currentOpponent.takeDamage(currentOpponent.getHealth());
            onMonsterDefeated();
            return;
        }

        enemyCounterattack();
    }

    public void performMagicAttack(CombatDirection direction, MapEntity monster) {
        if (!inCombat || currentOpponent == null) return;

        if (!player.useStamina(combatSystem.getMagicStaminaCost())) {
            actionPanel.log("Not enough stamina for magic! Rest or retreat.");
            return;
        }

        CombatDirection enemyDir = CombatDirection.values()[random.nextInt(4)];
        CombatResult result = combatSystem.resolveMagicBolt(player, currentOpponent,
                direction, enemyDir);
        actionPanel.log("You cast bolt " + direction + ". Enemy guards " + enemyDir
                + ". [Stamina: " + player.getStamina() + "]");
        actionPanel.log("  " + result.getNarration());
        if (result.getDamageDealt() > 0) {
            actionPanel.log("  Enemy HP: " + currentOpponent.getHealth() + "/" + currentOpponent.getMaxHealth());
        }

        if (!currentOpponent.isAlive()) {
            onMonsterDefeated();
            return;
        }

        if (currentOpponent.wantsToFlee()) {
            actionPanel.log(currentOpponent.getName() + " staggers back, broken.");
            currentOpponent.takeDamage(currentOpponent.getHealth());
            onMonsterDefeated();
            return;
        }

        enemyCounterattack();
    }

    public void performFeint(CombatDirection fakeDir, CombatDirection realDir, MapEntity monster) {
        if (!inCombat || currentOpponent == null) return;

        if (!player.useStamina(combatSystem.getFeintStaminaCost())) {
            actionPanel.log("Not enough stamina for a feint! [Need " + combatSystem.getFeintStaminaCost() + "]");
            return;
        }

        CombatDirection enemyGuard = CombatDirection.values()[random.nextInt(4)];
        CombatResult result = combatSystem.resolveFeint(player, currentOpponent,
                fakeDir, realDir, enemyGuard);
        actionPanel.log("You feint " + fakeDir + " -> strike " + realDir
                + ". Enemy guards " + enemyGuard + ". [Stamina: " + player.getStamina() + "]");
        actionPanel.log("  " + result.getNarration());

        if (!currentOpponent.isAlive()) {
            onMonsterDefeated();
            return;
        }

        enemyCounterattack();
    }

    private void onMonsterDefeated() {
        String name = currentOpponent.getName();
        actionPanel.log(name + " is defeated!");
        player.trainSkill(SkillType.FIGHTING, 40);
        actionPanel.log("  +40 XP in FIGHTING");
        player.recoverStamina(30);
        actionPanel.log("  Stamina recovered to " + player.getStamina());

        world.getQuestLog().onMonsterDefeated(name);
        checkQuestCompletion();
        checkMilestones();
        statsPanel.refresh();
        endCombat();
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

        actionPanel.log("Enemy attacks " + atkDir + "! You guard " + playerGuard + ".");
        actionPanel.log("  " + counter.getNarration());
        if (counter.getDamageDealt() > 0) {
            actionPanel.log("  Your HP: " + player.getHealth() + "/" + player.getMaxHealth());
        }

        // Player recovers some stamina each turn
        player.recoverStamina(8);
        actionPanel.log("  [Stamina: " + player.getStamina() + "/" + player.getMaxStamina() + "]");
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
        currentCombatEntity = null;
        player.recoverStamina(player.getMaxStamina()); // full recovery out of combat
        actionPanel.showActions(world.getAvailableActions());
        actionPanel.log("--- Combat ended ---");
        actionPanel.log("");
    }

    private void checkQuestCompletion() {
        for (Quest quest : world.getQuestLog().getActiveQuests()) {
            if (quest.checkCompletion()) {
                quest.complete(player);
                actionPanel.log("");
                actionPanel.log("[QUEST COMPLETE] " + quest.getName() + "!");
                actionPanel.log("  " + quest.getCompletionNarrative());
                for (Quest.QuestReward reward : quest.getRewards()) {
                    actionPanel.log("  Reward: " + reward.describe());
                }
                statsPanel.refresh();
            }
        }
    }

    private void checkMilestones() {
        for (Milestone m : world.getMilestones()) {
            if (m.isEligible(player)) {
                m.claim(player);
                actionPanel.log("");
                actionPanel.log("[MILESTONE] " + m.getTitle() + " (Lv " + m.getLevel() + " " + m.getSkill() + ")");
                actionPanel.log("  " + m.getUnlockDescription());
                if (m.getReward() != null) {
                    actionPanel.log("  Reward item: " + m.getReward());
                }
            }
        }
    }

    private List<Recipe> getCraftableRecipes() {
        return world.getRecipes().stream()
                .filter(r -> r.canCraft(player))
                .collect(java.util.stream.Collectors.toList());
    }

    private void autoCraft() {
        for (Recipe recipe : getCraftableRecipes()) {
            String result = recipe.craft(player);
            if (result != null) {
                actionPanel.log("  [CRAFTED] " + recipe.getName() + " -> " + recipe.getResultItem());
                actionPanel.log("  " + result);
                world.getQuestLog().onItemGathered(recipe.getResultItem(), recipe.getResultQuantity());
            }
        }
    }

    private void showInventorySnapshot() {
        var items = player.getInventory().getAllItems();
        if (!items.isEmpty()) {
            StringBuilder sb = new StringBuilder("  Inventory: ");
            items.forEach((item, qty) -> sb.append(item).append(" x").append(qty).append(", "));
            actionPanel.log(sb.substring(0, sb.length() - 2));
        }
    }

    private void showInventoryFull() {
        var items = player.getInventory().getAllItems();
        if (items.isEmpty()) {
            actionPanel.log("  (empty)");
        } else {
            items.forEach((item, qty) ->
                    actionPanel.log("  " + item + " x" + qty));
        }
    }
}
