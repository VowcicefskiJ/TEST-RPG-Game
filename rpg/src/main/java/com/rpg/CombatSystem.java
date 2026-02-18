package com.rpg;

import java.util.Random;

public class CombatSystem {
    private static final int BASE_MELEE_DAMAGE = 12;
    private static final int BASE_MAGIC_DAMAGE = 14;
    private static final int MAGIC_SHIELD_DURABILITY_COST = 20;
    private static final int MIN_MELEE_DAMAGE = 6;
    private static final int MIN_MAGIC_DAMAGE = 8;
    private static final int MELEE_STAMINA_COST = 15;
    private static final int MAGIC_STAMINA_COST = 20;
    private static final int FEINT_STAMINA_COST = 10;
    private static final int COMBO_BONUS_THRESHOLD = 2;
    private static final double COMBO_DAMAGE_MULTIPLIER = 1.5;
    private static final double STAGGER_CHANCE = 0.25;
    private static final double BLEED_CHANCE = 0.15;

    private final Random random = new Random();

    public CombatResult resolveMeleeAttack(
            Combatant attacker,
            Combatant defender,
            CombatDirection attackDirection,
            CombatDirection parryDirection
    ) {
        if (!defender.isAlive()) {
            return new CombatResult(defender.getName() + " is already down.", 0, 0);
        }

        // Staggered defenders can't parry
        boolean parrySuccess = !defender.isStaggered() && attackDirection == parryDirection;

        if (parrySuccess) {
            attacker.resetCombo();
            if (random.nextDouble() < STAGGER_CHANCE) {
                attacker.setStaggered(true);
                return new CombatResult(
                        "You parry the strike from the " + attackDirection
                                + " and stagger your opponent!",
                        0, 0);
            }
            return new CombatResult(
                    "You parry the strike from the " + attackDirection + ".",
                    0, 0);
        }

        int damage = calculateMeleeDamage(attacker, defender);

        // Combo: hitting from a different direction than last time builds combo
        if (attacker.getLastAttackDirection() != null
                && attacker.getLastAttackDirection() != attackDirection) {
            attacker.incrementCombo();
        } else {
            attacker.resetCombo();
        }
        attacker.setLastAttackDirection(attackDirection);

        if (attacker.getComboCount() >= COMBO_BONUS_THRESHOLD) {
            damage = (int) Math.round(damage * COMBO_DAMAGE_MULTIPLIER);
            attacker.resetCombo();
            defender.takeDamage(damage);

            String msg = "COMBO STRIKE from the " + attackDirection
                    + "! Devastating blow for " + damage + " damage!";
            if (random.nextDouble() < BLEED_CHANCE * 2) {
                defender.applyBleed(3);
                msg += " The wound bleeds!";
            }
            return new CombatResult(msg, damage, 0);
        }

        defender.takeDamage(damage);
        String msg = "The strike lands from the " + attackDirection + ".";
        if (random.nextDouble() < BLEED_CHANCE) {
            defender.applyBleed(2);
            msg += " A gash opens - bleeding!";
        }
        return new CombatResult(msg, damage, 0);
    }

    public CombatResult resolveMagicBolt(
            Combatant attacker,
            Combatant defender,
            CombatDirection attackDirection,
            CombatDirection parryDirection
    ) {
        if (!defender.isAlive()) {
            return new CombatResult(defender.getName() + " is already down.", 0, 0);
        }

        int damage = calculateMagicDamage(attacker, defender);

        boolean shieldBlock = !defender.isStaggered()
                && attackDirection == parryDirection
                && defender.getShieldDurability() > 0;

        if (shieldBlock) {
            defender.reduceShieldDurability(MAGIC_SHIELD_DURABILITY_COST);
            int reducedDamage = Math.max((int) Math.round(damage * 0.6), MIN_MAGIC_DAMAGE);
            defender.takeDamage(reducedDamage);
            attacker.resetCombo();
            return new CombatResult(
                    "You angle your shield into the " + attackDirection
                            + " bolt. It rattles the shield hard.",
                    reducedDamage,
                    MAGIC_SHIELD_DURABILITY_COST
            );
        }

        defender.takeDamage(damage);
        if (random.nextDouble() < STAGGER_CHANCE) {
            defender.setStaggered(true);
            return new CombatResult(
                    "The magic bolt hits from the " + attackDirection
                            + " and staggers the target!",
                    damage, 0);
        }
        return new CombatResult(
                "The magic bolt hits from the " + attackDirection + ".",
                damage, 0);
    }

    public CombatResult resolveFeint(
            Combatant attacker,
            Combatant defender,
            CombatDirection fakeDirection,
            CombatDirection realDirection,
            CombatDirection parryDirection
    ) {
        if (!defender.isAlive()) {
            return new CombatResult(defender.getName() + " is already down.", 0, 0);
        }

        // If defender parried the fake direction, feint succeeds
        if (parryDirection == fakeDirection && parryDirection != realDirection) {
            int damage = (int) Math.round(calculateMeleeDamage(attacker, defender) * 1.3);
            defender.takeDamage(damage);
            defender.setStaggered(true);
            return new CombatResult(
                    "You feint " + fakeDirection + " then strike from " + realDirection
                            + "! " + damage + " damage and stagger!",
                    damage, 0);
        }

        // If they guessed the real direction, parry
        if (parryDirection == realDirection) {
            attacker.resetCombo();
            return new CombatResult(
                    "You feint " + fakeDirection + " but they read the real strike from "
                            + realDirection + " and parry!",
                    0, 0);
        }

        // Guessed neither - normal hit
        int damage = calculateMeleeDamage(attacker, defender);
        defender.takeDamage(damage);
        return new CombatResult(
                "The feinted strike from " + realDirection + " connects.",
                damage, 0);
    }

    public int getMeleeStaminaCost() { return MELEE_STAMINA_COST; }
    public int getMagicStaminaCost() { return MAGIC_STAMINA_COST; }
    public int getFeintStaminaCost() { return FEINT_STAMINA_COST + MELEE_STAMINA_COST; }

    private int calculateMeleeDamage(Combatant attacker, Combatant defender) {
        int raw = BASE_MELEE_DAMAGE + attacker.getAttackPower();
        int mitigated = raw - defender.getDefenseRating();
        return Math.max(mitigated, MIN_MELEE_DAMAGE);
    }

    private int calculateMagicDamage(Combatant attacker, Combatant defender) {
        int raw = BASE_MAGIC_DAMAGE + attacker.getMagicPower();
        int mitigated = raw - (defender.getDefenseRating() / 2);
        return Math.max(mitigated, MIN_MAGIC_DAMAGE);
    }
}
