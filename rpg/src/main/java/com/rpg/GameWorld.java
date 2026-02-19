package com.rpg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GameWorld {
    private final List<SkillAction> availableActions = new ArrayList<>();
    private final MagicSpellCatalog spellCatalog = new MagicSpellCatalog();
    private final SkillAnimationCatalog animationCatalog = new SkillAnimationCatalog();
    private final List<Npc> trainingNpcs = new ArrayList<>();
    private final List<Area> areas = new ArrayList<>();
    private final List<Recipe> recipes = new ArrayList<>();
    private final QuestLog questLog = new QuestLog();
    private final List<Milestone> milestones = new ArrayList<>();

    public GameWorld() {
        seedActions();
        seedRecipes();
        seedQuests();
        seedMilestones();
    }

    public List<SkillAction> getAvailableActions() {
        return Collections.unmodifiableList(availableActions);
    }

    public MagicSpellCatalog getSpellCatalog() { return spellCatalog; }
    public SkillAnimationCatalog getAnimationCatalog() { return animationCatalog; }
    public List<Npc> getTrainingNpcs() { return Collections.unmodifiableList(trainingNpcs); }
    public List<Area> getAreas() { return Collections.unmodifiableList(areas); }
    public List<Recipe> getRecipes() { return Collections.unmodifiableList(recipes); }
    public QuestLog getQuestLog() { return questLog; }
    public List<Milestone> getMilestones() { return Collections.unmodifiableList(milestones); }

    // Get actions relevant to a specific skill (for context-aware actions)
    public List<SkillAction> getActionsForSkill(SkillType type) {
        List<SkillAction> result = new ArrayList<>();
        for (SkillAction action : availableActions) {
            if (action.getSkillType() == type) result.add(action);
        }
        return result;
    }

    private void seedActions() {
        availableActions.add(new SkillAction("Cook a hearth meal", SkillType.COOKING, 24, "You simmer a hearty stew."));
        availableActions.add(new SkillAction("Tend the crop rows", SkillType.FARMING, 22, "You water and weed the fields."));
        availableActions.add(new SkillAction("Cast a river net", SkillType.FISHING, 26, "You haul in fresh fish."));
        availableActions.add(new SkillAction("Spar with a training partner", SkillType.FIGHTING, 28, "You trade measured blows."));
        availableActions.add(new SkillAction("Forage the forest edge", SkillType.FORAGING, 18, "You gather herbs and berries."));
        availableActions.add(new SkillAction("Chart a woodland trail", SkillType.MAPPING, 16, "You mark landmarks on a parchment map."));
        availableActions.add(new SkillAction("Distill a healing tonic", SkillType.ALCHEMY, 30, "You refine reagents into a tonic."));
        availableActions.add(new SkillAction("Mine a copper seam", SkillType.MINING, 28, "You strike a copper vein."));
        availableActions.add(new SkillAction("Study the rock strata", SkillType.GEOLOGY, 20, "You read the layers of stone."));
        availableActions.add(new SkillAction("Examine ancient relics", SkillType.ARCHAEOLOGY, 24, "You document relic inscriptions."));
        availableActions.add(new SkillAction("Recite village lore", SkillType.LOREKEEPING, 18, "You share tales of old."));
        availableActions.add(new SkillAction("Practice evocation basics", SkillType.MAGIC_SCHOOLS, 32, "You channel elemental sparks."));
        availableActions.add(new SkillAction("Forge a set of nails", SkillType.BLACKSMITHING, 26, "You shape metal on the anvil."));
        availableActions.add(new SkillAction("Rivet leather armor", SkillType.ARMOR_MAKING, 24, "You craft protective gear."));
        availableActions.add(new SkillAction("Balance a training blade", SkillType.WEAPON_MAKING, 28, "You temper steel with care."));
        availableActions.add(new SkillAction("Carve a focus staff", SkillType.STAFF_MAKING, 26, "You carve runes into wood."));
        trainingNpcs.add(new Npc("Training Duelist", 90, 40));
        trainingNpcs.add(new Npc("Ward Adept", 70, 60));
        areas.add(createStarterVale());
        areas.add(createDarkwoodForest());
        areas.add(createHollowCaves());
        areas.add(createAshenRuins());
    }

    public Area getAreaByName(String name) {
        for (Area a : areas) {
            if (a.getName().equals(name)) return a;
        }
        return null;
    }

    public int getAreaIndex(String name) {
        for (int i = 0; i < areas.size(); i++) {
            if (areas.get(i).getName().equals(name)) return i;
        }
        return -1;
    }

    private void seedRecipes() {
        // Fishing -> Cooking: raw fish becomes cooked meal
        recipes.add(new Recipe("Grilled Carp", SkillType.COOKING, 1,
                Map.of("Windglass Carp", 1), "Grilled Carp", 1, 30,
                "You grill the carp over hot coals until the skin crisps."));

        // Farming -> Cooking: crops become food
        recipes.add(new Recipe("Root Stew", SkillType.COOKING, 3,
                Map.of("Ashen Root Crop", 2), "Hearty Root Stew", 1, 45,
                "You dice the roots and simmer them into a thick stew."));

        // Mining -> Blacksmithing: ore becomes ingots
        recipes.add(new Recipe("Smelt Ironstone", SkillType.BLACKSMITHING, 1,
                Map.of("Ironstone Shard", 2), "Iron Ingot", 1, 35,
                "You heat the shards until they fuse into a rough ingot."));

        // Blacksmithing -> Weapon Making: ingots become weapons
        recipes.add(new Recipe("Forge Iron Blade", SkillType.WEAPON_MAKING, 5,
                Map.of("Iron Ingot", 2), "Iron Blade", 1, 60,
                "You hammer the ingots into a sturdy blade."));

        // Blacksmithing -> Armor Making: ingots become armor
        recipes.add(new Recipe("Forge Chain Links", SkillType.ARMOR_MAKING, 5,
                Map.of("Iron Ingot", 3), "Chain Mail Piece", 1, 65,
                "You draw wire and link it into protective chain."));

        // Foraging -> Alchemy: herbs become potions
        recipes.add(new Recipe("Brew Healing Salve", SkillType.ALCHEMY, 1,
                Map.of("Grave Pine Resin", 1, "Bleak Thistle", 1), "Healing Salve", 1, 40,
                "You blend the resin and thistle into a soothing salve."));

        // Alchemy + Foraging -> stronger potion
        recipes.add(new Recipe("Brew Strength Tonic", SkillType.ALCHEMY, 8,
                Map.of("Bleak Thistle", 2, "Grave Pine Resin", 2), "Strength Tonic", 1, 55,
                "The reagents combine into a potent tonic that warms the blood."));

        // Staff Making from foraging
        recipes.add(new Recipe("Carve Resin Staff", SkillType.STAFF_MAKING, 3,
                Map.of("Grave Pine Resin", 3), "Resin-Coated Staff", 1, 50,
                "You coat a carved ash rod with pine resin, sealing the runes."));
    }

    private void seedQuests() {
        // Starter quest chain
        questLog.addQuest(new Quest("vale_arrival", "Warden's Welcome",
                "Speak to Warden-Scribe Elowen to learn the basics of Gloamcrest Rise.",
                List.of(new Quest.QuestGoal(Quest.GoalType.TALK_TO_NPC, "Warden-Scribe Elowen", 1)),
                List.of(new Quest.QuestReward("xp", "COOKING", 50),
                        new Quest.QuestReward("xp", "MINING", 50)),
                "Elowen nods. 'The vale will test you. Start small.'"));

        questLog.addQuest(new Quest("first_harvest", "Fruits of the Vale",
                "Gather resources from three different nodes in Gloamcrest Rise.",
                List.of(new Quest.QuestGoal(Quest.GoalType.GATHER_ITEM, "Windglass Carp", 2),
                        new Quest.QuestGoal(Quest.GoalType.GATHER_ITEM, "Ironstone Shard", 2)),
                List.of(new Quest.QuestReward("xp", "FISHING", 80),
                        new Quest.QuestReward("xp", "MINING", 80),
                        new Quest.QuestReward("item", "Healing Salve", 3)),
                "Your pack grows heavier. The vale provides."));

        questLog.addQuest(new Quest("mist_hunter", "Mistbound Menace",
                "The Mistbound Wretches stalk the lower paths. Defeat 3 of them.",
                List.of(new Quest.QuestGoal(Quest.GoalType.DEFEAT_MONSTER, "Mistbound Wretch", 3)),
                List.of(new Quest.QuestReward("xp", "FIGHTING", 120),
                        new Quest.QuestReward("item", "Iron Ingot", 2)),
                "The mist thins. The path is safer now."));

        questLog.addQuest(new Quest("forge_first", "The Smith's Trial",
                "Smelt ironstone into ingots and forge your first blade.",
                List.of(new Quest.QuestGoal(Quest.GoalType.CRAFT_ITEM, "Iron Ingot", 2),
                        new Quest.QuestGoal(Quest.GoalType.CRAFT_ITEM, "Iron Blade", 1)),
                List.of(new Quest.QuestReward("xp", "BLACKSMITHING", 100),
                        new Quest.QuestReward("xp", "WEAPON_MAKING", 100)),
                "The blade gleams. You are no longer unarmed."));

        questLog.addQuest(new Quest("skill_seeker", "Jack of All Trades",
                "Train 5 different skills to level 3 or higher.",
                List.of(new Quest.QuestGoal(Quest.GoalType.TRAIN_SKILL, "COOKING", 3),
                        new Quest.QuestGoal(Quest.GoalType.TRAIN_SKILL, "MINING", 3),
                        new Quest.QuestGoal(Quest.GoalType.TRAIN_SKILL, "FISHING", 3),
                        new Quest.QuestGoal(Quest.GoalType.TRAIN_SKILL, "FIGHTING", 3),
                        new Quest.QuestGoal(Quest.GoalType.TRAIN_SKILL, "FORAGING", 3)),
                List.of(new Quest.QuestReward("item", "Strength Tonic", 2),
                        new Quest.QuestReward("xp", "LOREKEEPING", 150)),
                "You're becoming well-rounded. The vale respects versatility."));

        // Auto-activate the first quest
        questLog.getAllQuests().get(0).activate();
    }

    private void seedMilestones() {
        milestones.add(new Milestone(SkillType.COOKING, 5, "Apprentice Cook",
                "Recipe Book", "Unlock advanced cooking recipes."));
        milestones.add(new Milestone(SkillType.FISHING, 5, "Angler",
                "Silver Hook", "Catch rarer fish at the Runoff."));
        milestones.add(new Milestone(SkillType.MINING, 5, "Prospector",
                "Reinforced Pick", "Mine harder veins for better ore."));
        milestones.add(new Milestone(SkillType.FIGHTING, 5, "Brawler",
                "Steel Gauntlets", "Melee attacks deal +2 base damage."));
        milestones.add(new Milestone(SkillType.FORAGING, 5, "Herbalist",
                "Herb Pouch", "Foraging yields double herbs."));
        milestones.add(new Milestone(SkillType.BLACKSMITHING, 5, "Journeyman Smith",
                "Master Hammer", "Unlock iron-tier weapon and armor recipes."));
        milestones.add(new Milestone(SkillType.ALCHEMY, 5, "Potioneer",
                "Alembic Flask", "Brew two potions per craft."));
        milestones.add(new Milestone(SkillType.MAGIC_SCHOOLS, 5, "Initiate",
                "Channeling Orb", "Unlock tier-2 spells."));
        milestones.add(new Milestone(SkillType.COOKING, 10, "Master Chef",
                "Golden Ladle", "Cooked food restores more HP."));
        milestones.add(new Milestone(SkillType.FIGHTING, 10, "Warrior",
                "War Banner", "Combo threshold reduced to 1."));
        milestones.add(new Milestone(SkillType.MINING, 10, "Deep Miner",
                "Mithril Pick", "Access to deep mine areas."));
        milestones.add(new Milestone(SkillType.MAGIC_SCHOOLS, 10, "Adept",
                "Arcane Focus", "Unlock tier-3 spells and reduce mana costs."));
    }

    private Area createDarkwoodForest() {
        List<ResourceNode> resources = List.of(
                new ResourceNode("Hollow Oak Grove", SkillType.FORAGING, "Grave Pine Resin"),
                new ResourceNode("Moonlit Glade", SkillType.ALCHEMY, "Bleak Thistle"),
                new ResourceNode("Darkwood Stream", SkillType.FISHING, "Shadow Trout"),
                new ResourceNode("Tangled Thicket", SkillType.FARMING, "Wild Nightshade")
        );
        List<Npc> monsters = List.of(
                new Npc("Timber Wolf", 110, 15, 22, 8, 4, true),
                new Npc("Blighted Treant", 160, 40, 18, 18, 6, true),
                new Npc("Forest Wraith", 90, 10, 14, 4, 22, true),
                new Npc("Dire Bear", 180, 20, 26, 12, 2, true)
        );
        return new Area(
                "Darkwood Forest",
                "Ancient trees blot out the sky. Their canopy weaves a perpetual twilight where predators stalk silently.",
                resources, monsters);
    }

    private Area createHollowCaves() {
        List<ResourceNode> resources = List.of(
                new ResourceNode("Crystal Vein", SkillType.MINING, "Ironstone Shard"),
                new ResourceNode("Deep Copper Seam", SkillType.MINING, "Copper Nugget"),
                new ResourceNode("Fossil Shelf", SkillType.GEOLOGY, "Ancient Fossil"),
                new ResourceNode("Underground Pool", SkillType.FISHING, "Blind Cavefish")
        );
        List<Npc> monsters = List.of(
                new Npc("Cave Spider", 75, 10, 20, 6, 2, true),
                new Npc("Stone Golem", 200, 50, 24, 22, 0, true),
                new Npc("Deep Crawler", 120, 15, 18, 10, 8, true),
                new Npc("Crystal Bat Swarm", 60, 5, 12, 2, 14, true)
        );
        return new Area(
                "Hollow Caves",
                "A labyrinth of dripping stone descends into the earth. Crystal veins glow faintly in the eternal dark.",
                resources, monsters);
    }

    private Area createAshenRuins() {
        List<ResourceNode> resources = List.of(
                new ResourceNode("Crumbling Archive", SkillType.ARCHAEOLOGY, "Ancient Scroll"),
                new ResourceNode("Fallen Obelisk", SkillType.LOREKEEPING, "Rune Fragment"),
                new ResourceNode("Rusted Forge", SkillType.BLACKSMITHING, "Old Iron Scrap"),
                new ResourceNode("Overgrown Altar", SkillType.MAGIC_SCHOOLS, "Arcane Residue")
        );
        List<Npc> monsters = List.of(
                new Npc("Skeletal Sentinel", 100, 30, 16, 14, 6, true),
                new Npc("Phantom Scholar", 80, 5, 10, 4, 26, true),
                new Npc("Ashen Revenant", 150, 25, 22, 10, 16, true),
                new Npc("Bound Wraith", 120, 15, 18, 8, 20, true)
        );
        return new Area(
                "Ashen Ruins",
                "Shattered columns rise from ash-covered ground. The remnants of a great civilization whisper forgotten secrets.",
                resources, monsters);
    }

    private Area createStarterVale() {
        List<ResourceNode> resources = List.of(
                new ResourceNode("Cliffside Runoff", SkillType.FISHING, "Windglass Carp"),
                new ResourceNode("Stormcut Ledge", SkillType.MINING, "Ironstone Shard"),
                new ResourceNode("Grave Pine Shelf", SkillType.FORAGING, "Grave Pine Resin"),
                new ResourceNode("Chapel Terrace", SkillType.FARMING, "Ashen Root Crop"),
                new ResourceNode("Warden's Herbary", SkillType.ALCHEMY, "Bleak Thistle"),
                new ResourceNode("Rookwatch Spur", SkillType.MAPPING, "Rookwatch Survey")
        );
        List<Npc> monsters = List.of(
                new Npc("Mistbound Wretch", 95, 20, 16, 6, 8, true),
                new Npc("Stoneveil Gargoyle", 130, 30, 20, 14, 6, true),
                new Npc("Gravemoor Hound", 85, 10, 18, 5, 4, true),
                new Npc("Gloamcrest Acolyte", 105, 25, 12, 8, 18, true)
        );
        SkillTutor skillTutor = new SkillTutor(
                "Warden-Scribe Elowen",
                "A lantern-bearing tutor who offers starter tools and a primer on each craft.",
                List.of(
                        new SkillLesson(SkillType.COOKING, "Cook to restore strength; gather ingredients from the vale.", List.of("Tin Ladle", "Traveling Spices")),
                        new SkillLesson(SkillType.FARMING, "Farming yields steady supplies; tend plots often.", List.of("Seed Pouch", "Iron Trowel")),
                        new SkillLesson(SkillType.FISHING, "Fish the runoff for food and trade.", List.of("Reed Hook", "Line Spool")),
                        new SkillLesson(SkillType.FIGHTING, "Fighting rewards timing and stamina.", List.of("Practice Blade", "Worn Buckler")),
                        new SkillLesson(SkillType.FORAGING, "Foraging uncovers herbs and reagents.", List.of("Gatherer's Satchel", "Herb Shears")),
                        new SkillLesson(SkillType.MAPPING, "Mapping reveals safe paths and hidden lanes.", List.of("Charcoal Stylus", "Folded Map")),
                        new SkillLesson(SkillType.ALCHEMY, "Alchemy refines reagents into tonics.", List.of("Glass Vial", "Mortar Stone")),
                        new SkillLesson(SkillType.MINING, "Mining supplies metal for crafting.", List.of("Iron Pick", "Ore Sack")),
                        new SkillLesson(SkillType.GEOLOGY, "Geology spots valuable strata.", List.of("Rock Hammer", "Strata Lens")),
                        new SkillLesson(SkillType.ARCHAEOLOGY, "Archaeology uncovers relics and lore.", List.of("Soft Brush", "Relic Wrap")),
                        new SkillLesson(SkillType.LOREKEEPING, "Lorekeeping records myths and blessings.", List.of("Ink Quill", "Travel Journal")),
                        new SkillLesson(SkillType.MAGIC_SCHOOLS, "Magic schools shape your spellcraft.", List.of("Focus Crystal", "Runed Chalk")),
                        new SkillLesson(SkillType.BLACKSMITHING, "Blacksmithing shapes metal goods.", List.of("Hammer Head", "Forge Tongs")),
                        new SkillLesson(SkillType.ARMOR_MAKING, "Armor-making protects against heavier foes.", List.of("Leather Pattern", "Rivet Kit")),
                        new SkillLesson(SkillType.WEAPON_MAKING, "Weapon-making crafts blades and hafts.", List.of("Steel Blank", "Grip Wrap")),
                        new SkillLesson(SkillType.STAFF_MAKING, "Staff-making channels spell power.", List.of("Ash Rod", "Binding Cord"))
                )
        );
        AuctionHouse auctionHouse = new AuctionHouse(
                "Gloamcrest Auction Hall",
                "A ledger-lit exchange where prices are set by players, not the crown. Beware: in certain war zones, death means losing everything you carry."
        );
        return new Area(
                "Gloamcrest Rise",
                "A fortress crowns the jagged rise, its towers watching a cold expanse of moor and cloud. Black stone cliffs bite into the wind, while mist coils around the gate road and chapel terraces.",
                resources,
                monsters,
                List.of(skillTutor),
                auctionHouse
        );
    }
}
