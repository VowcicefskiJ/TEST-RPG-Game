package com.rpg;

import java.util.List;
import java.util.Map;

/**
 * Comprehensive test suite for all game systems.
 * Runs without GUI - tests logic only.
 */
public class GameSystemsTest {
    private static int totalTests = 0;
    private static int passedTests = 0;

    public static void main(String[] args) {
        System.out.println("=== RPG Game Systems Test Suite ===\n");

        testInventorySystem();
        testRecipeSystem();
        testSkillAndXPSystem();
        testCombatSystem();
        testQuestSystem();
        testMilestoneSystem();
        testStaminaSystem();

        System.out.println("\n=== Test Results ===");
        System.out.println("Passed: " + passedTests + "/" + totalTests);
        if (passedTests == totalTests) {
            System.out.println("ALL TESTS PASSED");
            System.exit(0);
        } else {
            System.out.println("SOME TESTS FAILED");
            System.exit(1);
        }
    }

    // ---------- Inventory ----------

    private static void testInventorySystem() {
        System.out.println("Testing Inventory System...");
        Inventory inv = new Inventory();

        check("Empty inventory", inv.isEmpty());
        inv.addItem("Iron Ore", 5);
        check("Add items", inv.getCount("Iron Ore") == 5);

        inv.addItem("Iron Ore", 10);
        check("Stack items", inv.getCount("Iron Ore") == 15);

        boolean removed = inv.removeItem("Iron Ore", 5);
        check("Remove items", removed && inv.getCount("Iron Ore") == 10);

        inv.addItem("Gold Coin", 999);
        inv.addItem("Gold Coin", 500);
        check("Max stack limit", inv.getCount("Gold Coin") == 999);

        inv.addItem("Fish", 3);
        // At this point: Iron Ore (10), Gold Coin (999), Fish (3) = 3 unique items
        check("Unique items count", inv.uniqueItemCount() == 3);

        System.out.println();
    }

    // ---------- Recipes / Crafting ----------

    private static void testRecipeSystem() {
        System.out.println("Testing Recipe System...");
        Player player = new Player("TestChef");
        Inventory inv = player.getInventory();

        Recipe fishDish = new Recipe(
                "Grilled Fish",
                SkillType.COOKING,
                1,
                Map.of("Fish", 2),
                "Cooked Fish",
                1,
                50,
                "You grill the fish."
        );

        check("Can't craft without ingredients", !fishDish.canCraft(player));

        inv.addItem("Fish", 2);
        check("Can craft with ingredients", fishDish.canCraft(player));

        int xpBefore = player.getSkill(SkillType.COOKING).getExperience();
        String narrative = fishDish.craft(player);
        int xpAfter = player.getSkill(SkillType.COOKING).getExperience();

        check("Crafting removes ingredients", inv.getCount("Fish") == 0);
        check("Crafting adds result item", inv.getCount("Cooked Fish") == 1);
        check("Crafting grants XP", xpAfter > xpBefore);
        check("Crafting returns narrative", narrative != null && narrative.length() > 0);

        System.out.println();
    }

    // ---------- Skills & XP ----------

    private static void testSkillAndXPSystem() {
        System.out.println("Testing Skill & XP System...");
        Player player = new Player("TestSkiller");
        Skill cooking = player.getSkill(SkillType.COOKING);

        check("Starts at level 1", cooking.getLevel() == 1);
        check("Starts with 0 XP", cooking.getExperience() == 0);

        int xpFor2 = cooking.experienceForNextLevel();
        check("Exponential curve > 0", xpFor2 > 0);

        player.trainSkill(SkillType.COOKING, 10);
        check("Small XP doesn't level up", cooking.getLevel() == 1);
        check("XP is stored", cooking.getExperience() == 10);

        player.trainSkill(SkillType.COOKING, xpFor2);
        check("Level increases after enough XP", cooking.getLevel() == 2);
        check("Overflow XP preserved", cooking.getExperience() < cooking.experienceForNextLevel());

        player.trainSkill(SkillType.MINING, 100);
        player.trainSkill(SkillType.FISHING, 100);
        check("Multiple skills track independently",
               player.getSkill(SkillType.MINING).getExperience() > 0
               && player.getSkill(SkillType.FISHING).getExperience() > 0);

        System.out.println();
    }

    // ---------- Combat ----------

    private static void testCombatSystem() {
        System.out.println("Testing Combat System...");
        CombatSystem combat = new CombatSystem();

        Combatant attacker = new Combatant("Hero", 100, 50);
        Combatant enemy    = new Combatant("Goblin", 50, 20);

        // Unmatched direction -> should deal damage
        CombatResult result = combat.resolveMeleeAttack(
                attacker, enemy, CombatDirection.NORTH, CombatDirection.SOUTH);
        check("Melee attack resolves without crash", result != null);
        check("Unparried melee hits", result.getDamageDealt() > 0);

        // Matched direction -> parry, 0 damage
        Combatant defender = new Combatant("Defender", 80, 40);
        result = combat.resolveMeleeAttack(
                attacker, defender, CombatDirection.NORTH, CombatDirection.NORTH);
        check("Parried attack deals 0 damage", result.getDamageDealt() == 0);

        // Magic bolt
        result = combat.resolveMagicBolt(
                attacker, enemy, CombatDirection.EAST, CombatDirection.WEST);
        check("Magic bolt resolves", result != null);

        // Stamina costs
        int meleeCost = combat.getMeleeStaminaCost();
        int magicCost = combat.getMagicStaminaCost();
        int feintCost = combat.getFeintStaminaCost();
        check("All stamina costs positive", meleeCost > 0 && magicCost > 0 && feintCost > 0);
        check("Feint costs more than melee", feintCost > meleeCost);

        // Feint: defender parries fake direction -> feint succeeds (bonus damage + stagger)
        Combatant feintTarget = new Combatant("Target", 80, 35);
        result = combat.resolveFeint(attacker, feintTarget,
                CombatDirection.NORTH, CombatDirection.SOUTH, CombatDirection.NORTH);
        check("Successful feint deals damage", result.getDamageDealt() > 0);
        check("Successful feint staggers", feintTarget.isStaggered());

        // Feint: defender reads real direction -> parry
        Combatant smartDef = new Combatant("Smart", 80, 35);
        result = combat.resolveFeint(attacker, smartDef,
                CombatDirection.NORTH, CombatDirection.SOUTH, CombatDirection.SOUTH);
        check("Read feint is parried", result.getDamageDealt() == 0);

        // Combo system
        Combatant c = new Combatant("Combo", 100, 40);
        check("Combo starts at 0", c.getComboCount() == 0);
        c.incrementCombo();
        check("Combo increments", c.getComboCount() == 1);
        c.resetCombo();
        check("Combo resets to 0", c.getComboCount() == 0);

        // Status effects
        check("Not staggered initially", !c.isStaggered());
        c.setStaggered(true);
        check("Can apply stagger", c.isStaggered());
        c.applyBleed(3);
        check("Bleed turns tracked", c.getBleedTurns() == 3);

        // Morale / flee at < 20% HP
        Combatant lowHp = new Combatant("LowHP", 100, 30);
        lowHp.takeDamage(85); // leaves 15 HP = 15% of 100
        check("Unit flees below 20% HP", lowHp.wantsToFlee());

        Combatant highHp = new Combatant("HighHP", 100, 30);
        highHp.takeDamage(50); // leaves 50 HP = 50%
        check("Unit doesn't flee above 20% HP", !highHp.wantsToFlee());

        System.out.println();
    }

    // ---------- Quests ----------

    private static void testQuestSystem() {
        System.out.println("Testing Quest System...");
        QuestLog log = new QuestLog();

        Quest quest = new Quest(
                "test_quest",
                "Test Quest",
                "A test quest",
                List.of(
                        new Quest.QuestGoal(Quest.GoalType.GATHER_ITEM, "Fish", 5),
                        new Quest.QuestGoal(Quest.GoalType.DEFEAT_MONSTER, "Goblin", 2)
                ),
                List.of(
                        new Quest.QuestReward("xp", "COOKING", 100),
                        new Quest.QuestReward("item", "Gold Coin", 50)
                ),
                "Quest complete!"
        );

        log.addQuest(quest);
        check("Quest added to log", log.getAllQuests().size() == 1);
        check("Quest starts AVAILABLE", quest.getStatus() == Quest.Status.AVAILABLE);
        check("Available list populated", log.getAvailableQuests().size() == 1);

        quest.activate();
        check("Quest becomes ACTIVE", quest.getStatus() == Quest.Status.ACTIVE);
        check("Active list populated", log.getActiveQuests().size() == 1);

        log.onItemGathered("Fish", 3);
        check("Item progress tracked (3/5)", quest.getGoals().get(0).getProgress() == 3);

        log.onMonsterDefeated("Goblin");
        check("Monster progress tracked (1/2)", quest.getGoals().get(1).getProgress() == 1);

        check("Quest not yet complete", !quest.checkCompletion());

        log.onItemGathered("Fish", 2);
        log.onMonsterDefeated("Goblin");
        check("Quest complete after all goals met", quest.checkCompletion());

        Player player = new Player("Quester");
        int xpBefore = player.getSkill(SkillType.COOKING).getExperience();
        quest.complete(player);
        check("Quest status set to COMPLETED", quest.getStatus() == Quest.Status.COMPLETED);
        check("XP reward granted", player.getSkill(SkillType.COOKING).getExperience() > xpBefore);
        check("Item reward granted", player.getInventory().getCount("Gold Coin") == 50);

        System.out.println();
    }

    // ---------- Milestones ----------

    private static void testMilestoneSystem() {
        System.out.println("Testing Milestone System...");
        Player player = new Player("Achiever");
        Milestone milestone = new Milestone(
                SkillType.COOKING, 5,
                "Apprentice Chef", "Recipe Book", "Unlock advanced recipes"
        );

        check("Not eligible below level 5", !milestone.isEligible(player));
        check("Not claimed initially", !milestone.isClaimed());

        // Train up to level 5
        while (player.getSkill(SkillType.COOKING).getLevel() < 5) {
            player.trainSkill(SkillType.COOKING,
                    player.getSkill(SkillType.COOKING).experienceForNextLevel() + 1);
        }
        check("Cooking reached level 5", player.getSkill(SkillType.COOKING).getLevel() >= 5);
        check("Now eligible for milestone", milestone.isEligible(player));

        milestone.claim(player);
        check("Milestone marked as claimed", milestone.isClaimed());
        check("Reward item in inventory", player.getInventory().getCount("Recipe Book") == 1);
        check("Can't claim same milestone twice", !milestone.isEligible(player));

        System.out.println();
    }

    // ---------- Stamina ----------

    private static void testStaminaSystem() {
        System.out.println("Testing Stamina System...");
        Player player = new Player("StaminaTester");

        check("Max stamina is 100", player.getMaxStamina() == 100);
        check("Starts at full stamina", player.getStamina() == 100);

        boolean used = player.useStamina(30);
        check("useStamina returns true when enough", used);
        check("Stamina decrements correctly", player.getStamina() == 70);

        boolean failed = player.useStamina(80);
        check("useStamina returns false when insufficient", !failed);
        check("Stamina unchanged on failed use", player.getStamina() == 70);

        player.recoverStamina(20);
        check("Stamina recovers by correct amount", player.getStamina() == 90);

        player.recoverStamina(50);
        check("Stamina capped at max", player.getStamina() == 100);

        CombatSystem combat = new CombatSystem();
        player.useStamina(combat.getMeleeStaminaCost());
        check("Stamina reduced by melee cost", player.getStamina() == 100 - combat.getMeleeStaminaCost());

        System.out.println();
    }

    // ---------- Helper ----------

    private static void check(String name, boolean condition) {
        totalTests++;
        if (condition) {
            passedTests++;
            System.out.println("  PASS  " + name);
        } else {
            System.out.println("  FAIL  " + name);
        }
    }
}
