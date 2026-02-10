package com.rpg.gui;

import com.rpg.Area;
import com.rpg.Npc;
import com.rpg.ResourceNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TileMap {
    public static final int WIDTH = 32;
    public static final int HEIGHT = 24;

    public static final int TILE_GRASS = 0;
    public static final int TILE_STONE = 1;
    public static final int TILE_WATER = 2;
    public static final int TILE_TREE = 3;
    public static final int TILE_PATH = 4;
    public static final int TILE_WALL = 5;
    public static final int TILE_DOOR = 6;

    private final int[][] tiles;
    private final List<MapEntity> entities = new ArrayList<>();

    public TileMap(Area area) {
        tiles = new int[HEIGHT][WIDTH];
        generate(area);
    }

    public int getTile(int x, int y) {
        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) return TILE_WALL;
        return tiles[y][x];
    }

    public boolean isWalkable(int x, int y) {
        int tile = getTile(x, y);
        return tile != TILE_WALL && tile != TILE_WATER && tile != TILE_TREE;
    }

    public List<MapEntity> getEntities() {
        return entities;
    }

    public MapEntity getEntityAt(int x, int y) {
        for (MapEntity e : entities) {
            if (e.getX() == x && e.getY() == y) return e;
        }
        return null;
    }

    private void generate(Area area) {
        Random rng = new Random(area.getName().hashCode());

        // Fill with grass
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                tiles[y][x] = TILE_GRASS;
            }
        }

        // Border walls
        for (int x = 0; x < WIDTH; x++) {
            tiles[0][x] = TILE_WALL;
            tiles[HEIGHT - 1][x] = TILE_WALL;
        }
        for (int y = 0; y < HEIGHT; y++) {
            tiles[y][0] = TILE_WALL;
            tiles[y][WIDTH - 1] = TILE_WALL;
        }

        // Gate entrance
        tiles[HEIGHT - 1][WIDTH / 2] = TILE_DOOR;
        tiles[HEIGHT - 1][WIDTH / 2 + 1] = TILE_DOOR;

        // Stone path from gate to center
        for (int y = HEIGHT - 2; y > HEIGHT / 2 - 2; y--) {
            tiles[y][WIDTH / 2] = TILE_PATH;
            tiles[y][WIDTH / 2 + 1] = TILE_PATH;
        }

        // Horizontal path through center
        for (int x = 4; x < WIDTH - 4; x++) {
            tiles[HEIGHT / 2][x] = TILE_PATH;
        }

        // Water feature (river/runoff on the left)
        for (int y = 3; y < HEIGHT - 3; y++) {
            tiles[y][5] = TILE_WATER;
            if (rng.nextInt(3) == 0) tiles[y][6] = TILE_WATER;
        }

        // Scatter trees
        for (int i = 0; i < 30; i++) {
            int tx = 2 + rng.nextInt(WIDTH - 4);
            int ty = 2 + rng.nextInt(HEIGHT - 4);
            if (tiles[ty][tx] == TILE_GRASS) {
                tiles[ty][tx] = TILE_TREE;
            }
        }

        // Stone clusters (cliffs)
        for (int i = 0; i < 12; i++) {
            int sx = 2 + rng.nextInt(WIDTH - 4);
            int sy = 2 + rng.nextInt(HEIGHT - 4);
            if (tiles[sy][sx] == TILE_GRASS) {
                tiles[sy][sx] = TILE_STONE;
            }
        }

        // Place resource nodes
        int rx = 7;
        for (ResourceNode node : area.getResources()) {
            int ny = 3 + rng.nextInt(HEIGHT - 7);
            int nx = rx;
            // Find walkable spot near target
            for (int attempt = 0; attempt < 20; attempt++) {
                int cx = nx + rng.nextInt(5) - 2;
                int cy = ny + rng.nextInt(5) - 2;
                if (cx > 1 && cx < WIDTH - 2 && cy > 1 && cy < HEIGHT - 2 && tiles[cy][cx] == TILE_GRASS) {
                    tiles[cy][cx] = TILE_GRASS; // keep walkable
                    entities.add(new MapEntity(cx, cy, MapEntity.TYPE_RESOURCE, node.getName(),
                            node.getSkillType() + " - " + node.getResourceItem()));
                    break;
                }
            }
            rx += 4;
        }

        // Place monsters
        for (Npc monster : area.getMonsters()) {
            for (int attempt = 0; attempt < 30; attempt++) {
                int mx = 2 + rng.nextInt(WIDTH - 4);
                int my = 2 + rng.nextInt(HEIGHT - 4);
                if (tiles[my][mx] == TILE_GRASS && getEntityAt(mx, my) == null) {
                    entities.add(new MapEntity(mx, my, MapEntity.TYPE_MONSTER, monster.getName(),
                            "HP: " + monster.getHealth() + " | ATK: " + monster.getAttackPower()));
                    break;
                }
            }
        }

        // Place skill tutor near center
        entities.add(new MapEntity(WIDTH / 2 + 3, HEIGHT / 2 - 1, MapEntity.TYPE_NPC,
                "Warden-Scribe Elowen", "Talk to learn skills and receive starter items."));

        // Place auction house
        if (area.getAuctionHouse() != null) {
            int ahx = WIDTH / 2 - 4;
            int ahy = HEIGHT / 2 - 3;
            // Small building footprint
            for (int bx = ahx; bx < ahx + 4; bx++) {
                tiles[ahy][bx] = TILE_WALL;
                tiles[ahy + 2][bx] = TILE_WALL;
            }
            tiles[ahy + 1][ahx] = TILE_WALL;
            tiles[ahy + 1][ahx + 3] = TILE_WALL;
            tiles[ahy + 2][ahx + 1] = TILE_DOOR;
            entities.add(new MapEntity(ahx + 1, ahy + 1, MapEntity.TYPE_NPC,
                    area.getAuctionHouse().getName(), area.getAuctionHouse().getDescription()));
        }
    }
}
