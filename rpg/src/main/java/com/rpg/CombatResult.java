package com.rpg;

public class CombatResult {
    private final String narration;
    private final int damageDealt;
    private final int shieldDamage;

    public CombatResult(String narration, int damageDealt, int shieldDamage) {
        this.narration = narration;
        this.damageDealt = damageDealt;
        this.shieldDamage = shieldDamage;
    }

    public String getNarration() {
        return narration;
    }

    public int getDamageDealt() {
        return damageDealt;
    }

    public int getShieldDamage() {
        return shieldDamage;
    }
}
