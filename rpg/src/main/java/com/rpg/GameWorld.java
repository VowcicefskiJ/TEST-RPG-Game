package com.rpg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameWorld {
    private final List<SkillAction> availableActions = new ArrayList<>();
    private final MagicSpellCatalog spellCatalog = new MagicSpellCatalog();
    private final SkillAnimationCatalog animationCatalog = new SkillAnimationCatalog();
    private final List<Npc> trainingNpcs = new ArrayList<>();
    private final List<Area> areas = new ArrayList<>();

    public GameWorld() {
        seedActions();
    }

    public List<SkillAction> getAvailableActions() {
        return Collections.unmodifiableList(availableActions);
    }

    public MagicSpellCatalog getSpellCatalog() {
        return spellCatalog;
    }

    public SkillAnimationCatalog getAnimationCatalog() {
        return animationCatalog;
    }

    public List<Npc> getTrainingNpcs() {
        return Collections.unmodifiableList(trainingNpcs);
    }

    public List<Area> getAreas() {
        return Collections.unmodifiableList(areas);
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
