package com.rpg;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class SkillAnimationCatalog {
    private final Map<SkillType, SkillAnimation> animations;

    public SkillAnimationCatalog() {
        animations = new EnumMap<>(SkillType.class);
        seedAnimations();
    }

    public SkillAnimation getAnimationForSkill(SkillType skillType) {
        return animations.get(skillType);
    }

    public Map<SkillType, SkillAnimation> getAllAnimations() {
        return Collections.unmodifiableMap(animations);
    }

    private void seedAnimations() {
        animations.put(SkillType.COOKING, new SkillAnimation(
                SkillType.COOKING,
                "Hearty Stew",
                "Hearty Stew",
                List.of("Clay Pot", "Boar Meat", "Root Vegetables", "Spring Water", "Herb Bundle"),
                List.of(
                        new AnimationStep("Set the clay pot over a low flame.", new String[]{"Clay Pot", "Campfire"}, 2.0),
                        new AnimationStep("Pour spring water into the pot and bring it to a simmer.",
                                new String[]{"Spring Water"}, 3.5),
                        new AnimationStep("Add chopped root vegetables and stir with a wooden ladle.",
                                new String[]{"Root Vegetables", "Wooden Ladle"}, 4.0),
                        new AnimationStep("Slide in boar meat chunks and let them sear on the surface.",
                                new String[]{"Boar Meat"}, 3.0),
                        new AnimationStep("Crush herbs between your palms and season the stew.",
                                new String[]{"Herb Bundle"}, 2.0),
                        new AnimationStep("Cover the pot, let it bubble, then ladle into a bowl.",
                                new String[]{"Bowl", "Hearty Stew"}, 4.5)
                )
        ));

        animations.put(SkillType.FARMING, new SkillAnimation(
                SkillType.FARMING,
                "Harvest Sunroot",
                "Sunroot Crop",
                List.of("Seed Satchel", "Watering Can", "Sunroot Plot"),
                List.of(
                        new AnimationStep("Mark a neat furrow and press seeds into the soil.",
                                new String[]{"Seed Satchel", "Soil"}, 3.0),
                        new AnimationStep("Water the row until the earth darkens.", new String[]{"Watering Can"}, 2.5),
                        new AnimationStep("Pull weeds and pat the soil around the sprout.", new String[]{"Sprout"}, 2.0),
                        new AnimationStep("Wait as the sunroot blooms with golden leaves.", new String[]{"Sunroot Plant"}, 3.5),
                        new AnimationStep("Twist and lift the ripe sunroot free.", new String[]{"Sunroot Crop"}, 2.5)
                )
        ));

        animations.put(SkillType.FISHING, new SkillAnimation(
                SkillType.FISHING,
                "River Pike",
                "River Pike",
                List.of("Reed Rod", "Hooked Bait", "Riverbank"),
                List.of(
                        new AnimationStep("Cast the baited hook into the flowing current.",
                                new String[]{"Reed Rod", "Hooked Bait"}, 2.5),
                        new AnimationStep("The line tugs hard; you set the hook.", new String[]{"Fishing Line"}, 1.5),
                        new AnimationStep("Reel steadily as the fish fights downstream.", new String[]{"River Pike"}, 3.5),
                        new AnimationStep("Lift the pike onto a damp cloth.", new String[]{"River Pike", "Damp Cloth"}, 2.0)
                )
        ));

        animations.put(SkillType.FIGHTING, new SkillAnimation(
                SkillType.FIGHTING,
                "Training Strike",
                "Balanced Footwork",
                List.of("Practice Blade", "Buckler"),
                List.of(
                        new AnimationStep("Raise the buckler to guard your chest.", new String[]{"Buckler"}, 1.5),
                        new AnimationStep("Step forward and slash across the target dummy.",
                                new String[]{"Practice Blade"}, 2.0),
                        new AnimationStep("Re-center your stance and breathe out.", new String[]{"Training Dummy"}, 1.5)
                )
        ));

        animations.put(SkillType.FORAGING, new SkillAnimation(
                SkillType.FORAGING,
                "Gather Wild Berries",
                "Basket of Berries",
                List.of("Wicker Basket", "Wild Berry Bush"),
                List.of(
                        new AnimationStep("Brush aside leaves to reveal ripe berries.", new String[]{"Wild Berry Bush"}, 2.0),
                        new AnimationStep("Pluck berries gently, avoiding thorns.", new String[]{"Wild Berries"}, 2.5),
                        new AnimationStep("Set the berries into a wicker basket.", new String[]{"Wicker Basket"}, 1.5)
                )
        ));

        animations.put(SkillType.MAPPING, new SkillAnimation(
                SkillType.MAPPING,
                "Trail Survey",
                "Updated Trail Map",
                List.of("Parchment Map", "Charcoal Stylus", "Landmark Stone"),
                List.of(
                        new AnimationStep("Study the landmark stone for markings.", new String[]{"Landmark Stone"}, 2.0),
                        new AnimationStep("Sketch the ridge line on the parchment.",
                                new String[]{"Parchment Map", "Charcoal Stylus"}, 3.0),
                        new AnimationStep("Mark a safe crossing with a small rune.",
                                new String[]{"Updated Trail Map"}, 2.0)
                )
        ));

        animations.put(SkillType.ALCHEMY, new SkillAnimation(
                SkillType.ALCHEMY,
                "Healing Tonic",
                "Healing Tonic",
                List.of("Copper Still", "Bitterroot", "Clear Vial", "Spring Water"),
                List.of(
                        new AnimationStep("Grind bitterroot into a coarse paste.", new String[]{"Bitterroot", "Mortar"}, 3.0),
                        new AnimationStep("Combine paste with spring water in the still.",
                                new String[]{"Copper Still", "Spring Water"}, 2.5),
                        new AnimationStep("Heat gently as vapors condense.", new String[]{"Copper Still"}, 4.0),
                        new AnimationStep("Decant the tonic into a clear vial.",
                                new String[]{"Clear Vial", "Healing Tonic"}, 2.0)
                )
        ));

        animations.put(SkillType.MINING, new SkillAnimation(
                SkillType.MINING,
                "Copper Ore",
                "Copper Ore",
                List.of("Pickaxe", "Copper Vein"),
                List.of(
                        new AnimationStep("Clear loose stone from the vein face.", new String[]{"Copper Vein"}, 2.0),
                        new AnimationStep("Strike with a pickaxe until ore flakes free.",
                                new String[]{"Pickaxe", "Copper Ore"}, 3.5),
                        new AnimationStep("Collect the ore in a sturdy sack.", new String[]{"Copper Ore", "Sack"}, 2.0)
                )
        ));

        animations.put(SkillType.GEOLOGY, new SkillAnimation(
                SkillType.GEOLOGY,
                "Identify Quartz",
                "Quartz Sample",
                List.of("Rock Hammer", "Strata Wall"),
                List.of(
                        new AnimationStep("Scan the strata for crystalline glints.", new String[]{"Strata Wall"}, 2.0),
                        new AnimationStep("Tap the layer with a rock hammer.", new String[]{"Rock Hammer"}, 1.5),
                        new AnimationStep("Extract a clean quartz sample.", new String[]{"Quartz Sample"}, 2.0)
                )
        ));

        animations.put(SkillType.ARCHAEOLOGY, new SkillAnimation(
                SkillType.ARCHAEOLOGY,
                "Relic Tablet",
                "Relic Tablet",
                List.of("Brush", "Chisel", "Buried Tablet"),
                List.of(
                        new AnimationStep("Brush away loose sand from the tablet edge.",
                                new String[]{"Brush", "Buried Tablet"}, 2.5),
                        new AnimationStep("Use a chisel to free the tablet.", new String[]{"Chisel"}, 3.0),
                        new AnimationStep("Lift the relic tablet and wrap it in cloth.",
                                new String[]{"Relic Tablet", "Cloth Wrap"}, 2.0)
                )
        ));

        animations.put(SkillType.LOREKEEPING, new SkillAnimation(
                SkillType.LOREKEEPING,
                "Record Oral Legend",
                "Lore Entry",
                List.of("Ink Quill", "Travel Journal", "Elder's Tale"),
                List.of(
                        new AnimationStep("Listen as the elder recounts the legend.", new String[]{"Elder's Tale"}, 3.0),
                        new AnimationStep("Write key names and places with the quill.",
                                new String[]{"Ink Quill", "Travel Journal"}, 3.5),
                        new AnimationStep("Seal the entry with a wax stamp.", new String[]{"Lore Entry", "Wax Seal"}, 2.0)
                )
        ));

        animations.put(SkillType.MAGIC_SCHOOLS, new SkillAnimation(
                SkillType.MAGIC_SCHOOLS,
                "Channel Arcane Bolt",
                "Arcane Bolt",
                List.of("Focus Crystal", "Channeling Sigil"),
                List.of(
                        new AnimationStep("Trace a channeling sigil in the air.", new String[]{"Channeling Sigil"}, 2.0),
                        new AnimationStep("Hold a focus crystal and draw in mana.", new String[]{"Focus Crystal"}, 2.5),
                        new AnimationStep("Release a controlled arcane bolt.", new String[]{"Arcane Bolt"}, 1.5)
                )
        ));

        animations.put(SkillType.BLACKSMITHING, new SkillAnimation(
                SkillType.BLACKSMITHING,
                "Forged Nails",
                "Forged Nails",
                List.of("Iron Ingot", "Anvil", "Hammer"),
                List.of(
                        new AnimationStep("Heat the iron ingot until it glows orange.",
                                new String[]{"Iron Ingot", "Forge"}, 3.0),
                        new AnimationStep("Hammer the ingot into a narrow bar.", new String[]{"Hammer", "Anvil"}, 2.5),
                        new AnimationStep("Cut and shape the bar into nails.", new String[]{"Forged Nails"}, 2.0),
                        new AnimationStep("Quench the nails in a bucket of water.", new String[]{"Water Bucket"}, 1.5)
                )
        ));

        animations.put(SkillType.ARMOR_MAKING, new SkillAnimation(
                SkillType.ARMOR_MAKING,
                "Leather Bracers",
                "Leather Bracers",
                List.of("Tanned Hide", "Rivet Kit", "Leather Pattern"),
                List.of(
                        new AnimationStep("Lay the pattern over the tanned hide.",
                                new String[]{"Leather Pattern", "Tanned Hide"}, 2.0),
                        new AnimationStep("Cut the hide into bracer shapes.", new String[]{"Cut Leather"}, 2.5),
                        new AnimationStep("Punch rivet holes and set the rivets.", new String[]{"Rivet Kit"}, 2.5),
                        new AnimationStep("Flex the bracers to test the fit.", new String[]{"Leather Bracers"}, 1.5)
                )
        ));

        animations.put(SkillType.WEAPON_MAKING, new SkillAnimation(
                SkillType.WEAPON_MAKING,
                "Training Sword",
                "Training Sword",
                List.of("Steel Blank", "Grindstone", "Grip Wrap"),
                List.of(
                        new AnimationStep("Heat and hammer the steel blank to shape.",
                                new String[]{"Steel Blank", "Hammer"}, 3.0),
                        new AnimationStep("Grind the edge until it is even.", new String[]{"Grindstone"}, 2.5),
                        new AnimationStep("Attach the grip wrap and pommel.", new String[]{"Grip Wrap", "Pommel"}, 2.0),
                        new AnimationStep("Test balance with a slow flourish.", new String[]{"Training Sword"}, 1.5)
                )
        ));

        animations.put(SkillType.STAFF_MAKING, new SkillAnimation(
                SkillType.STAFF_MAKING,
                "Runed Staff",
                "Runed Staff",
                List.of("Ash Wood", "Rune Chisel", "Binding Cord"),
                List.of(
                        new AnimationStep("Select a straight ash branch and trim it.", new String[]{"Ash Wood"}, 2.5),
                        new AnimationStep("Carve runes with a rune chisel.",
                                new String[]{"Rune Chisel", "Carved Staff"}, 3.0),
                        new AnimationStep("Bind the grip with cord and seal it.", new String[]{"Binding Cord"}, 2.0),
                        new AnimationStep("Raise the staff and let the runes glow.", new String[]{"Runed Staff"}, 1.5)
                )
        ));
    }
}
