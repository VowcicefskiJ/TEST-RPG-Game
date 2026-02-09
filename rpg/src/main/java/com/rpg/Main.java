package com.rpg;

import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        LoginScreen loginScreen = new LoginScreen();
        UserDatabase userDatabase = new FileUserDatabase(Path.of("rpg", "data", "users.csv"));
        if (!loginScreen.authenticateAdmin(scanner, userDatabase)) {
            System.out.println("The Ashen Gate remains sealed. Exiting.");
            return;
        }

        GameWorld world = new GameWorld();
        Player player = new Player("Adventurer");
        AiAgent agent = new BasicPlannerAgent();
        GameEngine engine = new GameEngine(world, player, agent);
        engine.run(10);

        CombatSystem combatSystem = new CombatSystem();
        Npc opponent = world.getTrainingNpcs().get(0);
        System.out.println("You step into a first-person sparring ring.");
        CombatResult parryResult = combatSystem.resolveMeleeAttack(
                opponent,
                player,
                CombatDirection.NORTH,
                CombatDirection.NORTH
        );
        System.out.println(parryResult.getNarration());
        CombatResult boltResult = combatSystem.resolveMagicBolt(
                opponent,
                player,
                CombatDirection.EAST,
                CombatDirection.EAST
        );
        System.out.println(boltResult.getNarration());
        System.out.println("Your shield durability is now " + player.getShieldDurability() + ".");

        SkillAnimation cookingAnimation = world.getAnimationCatalog().getAnimationForSkill(SkillType.COOKING);
        System.out.println("Animation preview: " + cookingAnimation.getTitle());
        for (AnimationStep step : cookingAnimation.getSteps()) {
            System.out.println("- " + step.getDescription());
        }

        Area starterArea = world.getAreas().get(0);
        System.out.println("Area discovered: " + starterArea.getName());
        System.out.println(starterArea.getDescription());
        GraphicsProfile graphicsProfile = new GraphicsProfile(
                "Old-School RuneStones",
                "Low-poly silhouettes, crisp tiles, and readable silhouettes for smooth, lightweight play.",
                50,
                32,
                List.of(
                        "Fixed isometric camera with minimal sway.",
                        "Muted palette with high-contrast interactables.",
                        "Compact VFX to keep combat readable."
                )
        );
        System.out.println("Graphics profile: " + graphicsProfile.getName());
        System.out.println(graphicsProfile.getDescription());
        System.out.println("Target FPS: " + graphicsProfile.getTargetFps() + ", Tile Size: " + graphicsProfile.getTileSize());
        for (String note : graphicsProfile.getVisualNotes()) {
            System.out.println("- " + note);
        }
        System.out.println("Resources:");
        for (ResourceNode node : starterArea.getResources()) {
            System.out.println("- " + node.getName() + " (" + node.getResourceItem() + ")");
        }
        System.out.println("Monsters:");
        for (Npc monster : starterArea.getMonsters()) {
            System.out.println("- " + monster.getName());
        }

        if (!starterArea.getSkillTutors().isEmpty()) {
            System.out.println("Guides:");
            for (SkillTutor tutor : starterArea.getSkillTutors()) {
                System.out.println("- " + tutor.getName() + ": " + tutor.getDescription());
                for (SkillLesson lesson : tutor.getLessons()) {
                    System.out.println("  * " + lesson.getSkillType() + ": " + lesson.getOverview());
                    System.out.println("    Starter items: " + String.join(", ", lesson.getStarterItems()));
                }
            }
        }

        AuctionHouse auctionHouse = starterArea.getAuctionHouse();
        if (auctionHouse != null) {
            System.out.println("Auction House: " + auctionHouse.getName());
            System.out.println(auctionHouse.getDescription());
        }
    }
}
