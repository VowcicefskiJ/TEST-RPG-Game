package com.rpg.gui;

public class MapEntity {
    public static final int TYPE_RESOURCE = 0;
    public static final int TYPE_MONSTER = 1;
    public static final int TYPE_NPC = 2;
    public static final int TYPE_PORTAL = 3;

    private int x;
    private int y;
    private final int type;
    private final String name;
    private final String description;
    private final String targetArea; // for portals: name of the destination area

    public MapEntity(int x, int y, int type, String name, String description) {
        this(x, y, type, name, description, null);
    }

    public MapEntity(int x, int y, int type, String name, String description, String targetArea) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.name = name;
        this.description = description;
        this.targetArea = targetArea;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getType() { return type; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getTargetArea() { return targetArea; }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
