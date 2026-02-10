package com.rpg;

public class Combatant {
    private final String name;
    private int health;
    private int shieldDurability;
    private final int attackPower;
    private final int defenseRating;
    private final int magicPower;

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
        this.shieldDurability = shieldDurability;
        this.attackPower = attackPower;
        this.defenseRating = defenseRating;
        this.magicPower = magicPower;
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public int getShieldDurability() {
        return shieldDurability;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public int getDefenseRating() {
        return defenseRating;
    }

    public int getMagicPower() {
        return magicPower;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public void takeDamage(int amount) {
        if (amount <= 0) {
            return;
        }
        health = Math.max(health - amount, 0);
    }

    public void reduceShieldDurability(int amount) {
        if (amount <= 0) {
            return;
        }
        shieldDurability = Math.max(shieldDurability - amount, 0);
    }

    public int adjustDebuffDuration(int baseDurationSeconds, boolean usingShield) {
        if (baseDurationSeconds <= 0) {
            return 0;
        }
        return baseDurationSeconds;
    }
}
