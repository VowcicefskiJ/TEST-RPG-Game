package com.rpg;

public class Npc extends Combatant {
    private final boolean monster;

    public Npc(String name, int health, int shieldDurability) {
        this(name, health, shieldDurability, false);
    }

    public Npc(String name, int health, int shieldDurability, boolean monster) {
        super(name, health, shieldDurability);
        this.monster = monster;
    }

    public Npc(
            String name,
            int health,
            int shieldDurability,
            int attackPower,
            int defenseRating,
            int magicPower,
            boolean monster
    ) {
        super(name, health, shieldDurability, attackPower, defenseRating, magicPower);
        this.monster = monster;
    }

    public boolean isMonster() {
        return monster;
    }
}
