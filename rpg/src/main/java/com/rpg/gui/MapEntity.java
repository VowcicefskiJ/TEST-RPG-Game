package com.rpg.gui;

public class MapEntity {
    public static final int TYPE_RESOURCE = 0;
    public static final int TYPE_MONSTER = 1;
    public static final int TYPE_NPC = 2;

    private int x;
    private int y;
    private final int type;
    private final String name;
    private final String description;

    public MapEntity(int x, int y, int type, String name, String description) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getType() { return type; }
    public String getName() { return name; }
    public String getDescription() { return description; }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
