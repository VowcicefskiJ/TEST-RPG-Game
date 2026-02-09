package com.rpg;

public class CombatSystem {
    private static final int BASE_MELEE_DAMAGE = 12;
    private static final int BASE_MAGIC_DAMAGE = 14;
    private static final int MAGIC_SHIELD_DURABILITY_COST = 20;
    private static final int MIN_MELEE_DAMAGE = 6;
    private static final int MIN_MAGIC_DAMAGE = 8;

    public CombatResult resolveMeleeAttack(
            Combatant attacker,
            Combatant defender,
            CombatDirection attackDirection,
            CombatDirection parryDirection
    ) {
        if (!defender.isAlive()) {
            return new CombatResult(defender.getName() + " is already down.", 0, 0);
        }

        if (attackDirection == parryDirection) {
            return new CombatResult(
                    "You parry the strike from the " + attackDirection + ".",
                    0,
                    0
            );
        }

        int damage = calculateMeleeDamage(attacker, defender);
        defender.takeDamage(damage);
        return new CombatResult(
                "The strike lands from the " + attackDirection + ".",
                damage,
                0
        );
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

        if (attackDirection == parryDirection && defender.getShieldDurability() > 0) {
            defender.reduceShieldDurability(MAGIC_SHIELD_DURABILITY_COST);
            int reducedDamage = Math.max((int) Math.round(damage * 0.6), MIN_MAGIC_DAMAGE);
            defender.takeDamage(reducedDamage);
            return new CombatResult(
                    "You angle your shield into the " + attackDirection
                            + " bolt. It rattles the shield hard.",
                    reducedDamage,
                    MAGIC_SHIELD_DURABILITY_COST
            );
        }

        defender.takeDamage(damage);
        return new CombatResult(
                "The magic bolt hits from the " + attackDirection + ".",
                damage,
                0
        );
    }

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
