package com.rpg;

import java.util.List;

public class GraphicsProfile {
    private final String name;
    private final String description;
    private final int targetFps;
    private final int tileSize;
    private final List<String> visualNotes;

    public GraphicsProfile(
            String name,
            String description,
            int targetFps,
            int tileSize,
            List<String> visualNotes
    ) {
        this.name = name;
        this.description = description;
        this.targetFps = targetFps;
        this.tileSize = tileSize;
        this.visualNotes = List.copyOf(visualNotes);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getTargetFps() {
        return targetFps;
    }

    public int getTileSize() {
        return tileSize;
    }

    public List<String> getVisualNotes() {
        return visualNotes;
    }
}
