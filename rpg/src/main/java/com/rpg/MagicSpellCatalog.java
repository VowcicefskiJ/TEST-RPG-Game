package com.rpg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class MagicSpellCatalog {
    private final Map<MagicSchool, List<Spell>> spellsBySchool;

    public MagicSpellCatalog() {
        spellsBySchool = new EnumMap<>(MagicSchool.class);
        for (MagicSchool school : MagicSchool.values()) {
            spellsBySchool.put(school, new ArrayList<>());
        }
        seedSpells();
    }

    public List<Spell> getSpellsForSchool(MagicSchool school) {
        return Collections.unmodifiableList(spellsBySchool.getOrDefault(school, List.of()));
    }

    public List<Spell> getAllSpells() {
        List<Spell> all = new ArrayList<>();
        for (List<Spell> spells : spellsBySchool.values()) {
            all.addAll(spells);
        }
        return Collections.unmodifiableList(all);
    }

    private void seedSpells() {
        add(new Spell("Hex Lash", MagicSchool.WITCHCRAFT, 1, 8, 12, 1.1, 6,
                "Deals shadow damage and applies a 10% damage taken increase."));
        add(new Spell("Coven Ward", MagicSchool.WITCHCRAFT, 6, 14, 0, 0.0, 12,
                "Creates a ward that blocks 18 damage and reflects 5 back."));
        add(new Spell("Briar Bind", MagicSchool.WITCHCRAFT, 12, 18, 8, 0.8, 10,
                "Roots a target and deals nature damage over time."));
        add(new Spell("Night Cauldron", MagicSchool.WITCHCRAFT, 20, 26, 0, 0.0, 15,
                "Area haze reduces enemy accuracy by 15%."));
        add(new Spell("Blood Harvest", MagicSchool.WITCHCRAFT, 28, 35, 24, 1.4, 0,
                "High damage and heals caster for 35% of damage dealt."));

        add(new Spell("Spark", MagicSchool.FIRE, 1, 6, 10, 1.0, 0,
                "Quick fire bolt for single-target damage."));
        add(new Spell("Cinder Burst", MagicSchool.FIRE, 8, 15, 18, 1.2, 4,
                "Explodes for damage and leaves a burning patch."));
        add(new Spell("Flame Wall", MagicSchool.FIRE, 14, 22, 16, 1.0, 8,
                "Creates a wall that damages enemies crossing it."));
        add(new Spell("Inferno Lance", MagicSchool.FIRE, 22, 32, 30, 1.6, 0,
                "Piercing line attack with heavy damage."));
        add(new Spell("Phoenix Nova", MagicSchool.FIRE, 30, 45, 40, 1.9, 10,
                "Large blast with lingering burn and minor self-heal."));

        add(new Spell("Gale Dart", MagicSchool.AIR, 1, 5, 9, 0.9, 0,
                "Fast-moving air bolt with a high crit chance."));
        add(new Spell("Vacuum Pull", MagicSchool.AIR, 7, 14, 6, 0.7, 3,
                "Pulls enemies inward and briefly slows them."));
        add(new Spell("Zephyr Step", MagicSchool.AIR, 12, 18, 0, 0.0, 5,
                "Grants 25% movement speed and evasion."));
        add(new Spell("Storm Javelin", MagicSchool.AIR, 20, 28, 26, 1.3, 0,
                "Charged spear of wind that ignores 20% armor."));
        add(new Spell("Tempest Ring", MagicSchool.AIR, 28, 38, 20, 1.1, 8,
                "Whirling winds deal damage and knock back."));

        add(new Spell("Frost Needle", MagicSchool.WATER, 1, 6, 10, 1.0, 2,
                "Chills a target, slowing them by 15%."));
        add(new Spell("Tide Lash", MagicSchool.WATER, 9, 16, 18, 1.1, 0,
                "Whip of water that splashes for minor area damage."));
        add(new Spell("Healing Rain", MagicSchool.WATER, 14, 22, 16, 0.9, 8,
                "Heals allies in an area each second."));
        add(new Spell("Ice Prison", MagicSchool.WATER, 21, 30, 14, 0.8, 6,
                "Encases target, preventing movement and attacks."));
        add(new Spell("Glacial Surge", MagicSchool.WATER, 29, 42, 36, 1.7, 4,
                "Heavy damage with a strong slow aftershock."));

        add(new Spell("Stone Shard", MagicSchool.EARTH, 1, 7, 11, 1.0, 0,
                "Launches a shard that deals blunt damage."));
        add(new Spell("Rooted Bulwark", MagicSchool.EARTH, 8, 16, 0, 0.0, 10,
                "Increases armor by 20% and grants stagger resistance."));
        add(new Spell("Seismic Pulse", MagicSchool.EARTH, 15, 24, 20, 1.2, 0,
                "Ground shockwave that interrupts casting."));
        add(new Spell("Granite Spikes", MagicSchool.EARTH, 22, 33, 28, 1.4, 6,
                "Spikes erupt, damaging and slowing enemies."));
        add(new Spell("Mountain's Grasp", MagicSchool.EARTH, 30, 46, 34, 1.6, 8,
                "Massive slam with a long-lasting slow field."));

        add(new Spell("Arcane Bolt", MagicSchool.ARCANE, 1, 7, 12, 1.1, 0,
                "Pure arcane damage with consistent output."));
        add(new Spell("Mana Siphon", MagicSchool.ARCANE, 10, 18, 8, 0.9, 0,
                "Deals damage and restores 8 mana on hit."));
        add(new Spell("Phase Shift", MagicSchool.ARCANE, 16, 26, 0, 0.0, 4,
                "Briefly become untargetable and cleanse debuffs."));
        add(new Spell("Runic Barrage", MagicSchool.ARCANE, 24, 36, 30, 1.4, 0,
                "Fires multiple bolts that can chain to nearby foes."));
        add(new Spell("Astral Convergence", MagicSchool.ARCANE, 32, 50, 42, 1.8, 6,
                "Area burst that amplifies magic damage by 12%."));

        add(new Spell("Soothing Touch", MagicSchool.HEALING, 1, 6, 14, 1.0, 0,
                "Single-target heal with low mana cost."));
        add(new Spell("Mending Wave", MagicSchool.HEALING, 8, 16, 22, 1.1, 0,
                "Heals up to three allies in a chain."));
        add(new Spell("Renew", MagicSchool.HEALING, 14, 22, 10, 0.7, 10,
                "Applies a heal-over-time effect."));
        add(new Spell("Sanctuary", MagicSchool.HEALING, 22, 34, 0, 0.0, 8,
                "Creates a zone reducing incoming damage by 18%."));
        add(new Spell("Revivify", MagicSchool.HEALING, 30, 48, 40, 1.5, 0,
                "Massive heal that also clears one negative effect."));

        add(new Spell("Mirage Veil", MagicSchool.ILLUSIONS, 1, 7, 0, 0.0, 6,
                "Increases evasion by 15% and lowers threat."));
        add(new Spell("Phantom Arrow", MagicSchool.ILLUSIONS, 9, 16, 16, 1.0, 0,
                "Illusory projectile that ignores 25% armor."));
        add(new Spell("Doublesight", MagicSchool.ILLUSIONS, 15, 24, 0, 0.0, 12,
                "Reveals hidden targets and increases crit by 10%."));
        add(new Spell("Vanishing Act", MagicSchool.ILLUSIONS, 22, 32, 0, 0.0, 4,
                "Brief invisibility and movement burst."));
        add(new Spell("Hall of Echoes", MagicSchool.ILLUSIONS, 30, 45, 22, 1.2, 8,
                "Creates decoys that redirect enemy attacks."));

        add(new Spell("Battle Hymn", MagicSchool.BUFFS, 1, 8, 0, 0.0, 12,
                "Increases party damage by 8%."));
        add(new Spell("Iron Nerve", MagicSchool.BUFFS, 10, 18, 0, 0.0, 10,
                "Boosts armor and stun resistance by 15%."));
        add(new Spell("Quickstep", MagicSchool.BUFFS, 16, 24, 0, 0.0, 8,
                "Grants 20% attack speed and movement speed."));
        add(new Spell("Arc Guard", MagicSchool.BUFFS, 24, 34, 0, 0.0, 10,
                "Absorbs 25 damage and returns 10% of blocked damage."));
        add(new Spell("Hero's Crest", MagicSchool.BUFFS, 32, 48, 0, 0.0, 15,
                "Large stat boost to strength, defense, and focus."));

        add(new Spell("Wither", MagicSchool.DEBUFFS, 1, 7, 8, 0.9, 8,
                "Reduces target attack power by 12%."));
        add(new Spell("Sunder Armor", MagicSchool.DEBUFFS, 9, 16, 12, 1.0, 6,
                "Deals damage and reduces armor by 18%."));
        add(new Spell("Crippling Mist", MagicSchool.DEBUFFS, 15, 24, 10, 0.8, 8,
                "AoE slow and accuracy reduction."));
        add(new Spell("Mana Lock", MagicSchool.DEBUFFS, 22, 34, 14, 1.1, 5,
                "Silences and increases mana costs by 25%."));
        add(new Spell("Doom Mark", MagicSchool.DEBUFFS, 30, 46, 20, 1.3, 10,
                "Marks target to take 20% more damage from all sources."));
    }

    private void add(Spell spell) {
        spellsBySchool.get(spell.getSchool()).add(spell);
    }
}
