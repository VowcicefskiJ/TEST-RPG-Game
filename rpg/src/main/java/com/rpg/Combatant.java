package com.rpg;

public class Combatant {
    private final String name;
    private int health;
    private final int maxHealth;
    private int shieldDurability;
    private final int attackPower;
    private final int defenseRating;
    private final int magicPower;
    private int comboCount;
    private CombatDirection lastAttackDirection;
    private boolean staggered;
    private int bleedTurns;

    public Combatant(String name, int health, int shieldDurability) {
        this(name, health, shieldDurability, 12, 8, 10);
    }

    public Combatant(
            String name,
            int health,
            int shieldDurability,
            int attackPower,
            int defenseRating,
            int magicPower
    ) {
        this.name = name;
        this.health = health;
        this.maxHealth = health;
        this.shieldDurability = shieldDurability;
        this.attackPower = attackPower;
        this.defenseRating = defenseRating;
        this.magicPower = magicPower;
        this.comboCount = 0;
        this.lastAttackDirection = null;
        this.staggered = false;
        this.bleedTurns = 0;
    }

    public String getName() { return name; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public int getShieldDurability() { return shieldDurability; }
    public int getAttackPower() { return attackPower; }
    public int getDefenseRating() { return defenseRating; }
    public int getMagicPower() { return magicPower; }

    // Combo system
    public int getComboCount() { return comboCount; }
    public void incrementCombo() { comboCount++; }
    public void resetCombo() { comboCount = 0; }
    public CombatDirection getLastAttackDirection() { return lastAttackDirection; }
    public void setLastAttackDirection(CombatDirection dir) { this.lastAttackDirection = dir; }

    // Status effects
    public boolean isStaggered() { return staggered; }
    public void setStaggered(boolean staggered) { this.staggered = staggered; }
    public int getBleedTurns() { return bleedTurns; }
    public void applyBleed(int turns) { this.bleedTurns = Math.max(bleedTurns, turns); }

    public void tickStatusEffects() {
        if (bleedTurns > 0) {
            int bleedDmg = 3 + (maxHealth / 20);
            takeDamage(bleedDmg);
            bleedTurns--;
        }
        staggered = false; // stagger only lasts one turn
    }

    // Morale: monsters flee below 20% HP
    public boolean wantsToFlee() {
        return health > 0 && health < maxHealth * 0.2;
    }

    public boolean isAlive() { return health > 0; }

    public void takeDamage(int amount) {
        if (amount <= 0) return;
        health = Math.max(health - amount, 0);
    }

    public void reduceShieldDurability(int amount) {
        if (amount <= 0) return;
        shieldDurability = Math.max(shieldDurability - amount, 0);
    }

    public int adjustDebuffDuration(int baseDurationSeconds, boolean usingShield) {
        if (baseDurationSeconds <= 0) return 0;
        return baseDurationSeconds;
    }
}
