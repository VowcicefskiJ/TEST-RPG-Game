package com.rpg.gui;

import com.rpg.Area;
import com.rpg.Npc;
import com.rpg.ResourceNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Procedural tile map with biomes, structures, and natural terrain.
 * Uses simplex-style noise for organic landscape generation.
 */
public class TileMap {
    public static final int WIDTH = 48;
    public static final int HEIGHT = 36;

    // Base terrain
    public static final int TILE_GRASS = 0;
    public static final int TILE_STONE = 1;
    public static final int TILE_WATER = 2;
    public static final int TILE_TREE = 3;
    public static final int TILE_PATH = 4;
    public static final int TILE_WALL = 5;
    public static final int TILE_DOOR = 6;
    // New terrain types
    public static final int TILE_DEEP_WATER = 7;
    public static final int TILE_SAND = 8;
    public static final int TILE_DIRT = 9;
    public static final int TILE_TALL_GRASS = 10;
    public static final int TILE_FLOWERS = 11;
    public static final int TILE_DARK_STONE = 12;
    public static final int TILE_BRIDGE = 13;
    public static final int TILE_ROOF = 14;
    public static final int TILE_FENCE = 15;
    public static final int TILE_PINE_TREE = 16;
    public static final int TILE_DEAD_TREE = 17;
    public static final int TILE_BUSH = 18;
    public static final int TILE_MUSHROOM = 19;
    public static final int TILE_GRAVE = 20;
    public static final int TILE_RUINS = 21;
    public static final int TILE_CAMPFIRE = 22;
    public static final int TILE_WELL = 23;
    public static final int TILE_TORCH = 24;
    public static final int TILE_COBBLE = 25;

    // Biome overlay (not a tile — stored separately for rendering hints)
    public static final int BIOME_VALE = 0;
    public static final int BIOME_FOREST = 1;
    public static final int BIOME_SWAMP = 2;
    public static final int BIOME_RUINS = 3;
    public static final int BIOME_TOWN = 4;

    private final int[][] tiles;
    private final int[][] biome;       // biome at each cell
    private final float[][] elevation; // 0..1 height for shading
    private final List<MapEntity> entities = new ArrayList<>();
    private final long seed;

    public TileMap(Area area) {
        tiles = new int[HEIGHT][WIDTH];
        biome = new int[HEIGHT][WIDTH];
        elevation = new float[HEIGHT][WIDTH];
        seed = area.getName().hashCode();
        generate(area);
    }

    public int getTile(int x, int y) {
        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) return TILE_WALL;
        return tiles[y][x];
    }

    public int getBiome(int x, int y) {
        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) return BIOME_VALE;
        return biome[y][x];
    }

    public float getElevation(int x, int y) {
        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) return 0;
        return elevation[y][x];
    }

    public boolean isWalkable(int x, int y) {
        int tile = getTile(x, y);
        return tile != TILE_WALL && tile != TILE_WATER && tile != TILE_DEEP_WATER
                && tile != TILE_TREE && tile != TILE_PINE_TREE && tile != TILE_DEAD_TREE
                && tile != TILE_ROOF && tile != TILE_FENCE;
    }

    public List<MapEntity> getEntities() { return entities; }

    public MapEntity getEntityAt(int x, int y) {
        for (MapEntity e : entities) {
            if (e.getX() == x && e.getY() == y) return e;
        }
        return null;
    }

    // ==================== GENERATION ====================

    private void generate(Area area) {
        Random rng = new Random(seed);

        // Step 1: Generate noise-based elevation
        generateElevation(rng);

        // Step 2: Assign biomes based on position
        assignBiomes();

        // Step 3: Lay base terrain from elevation + biome
        layBaseTerrain(rng);

        // Step 4: Carve a river
        carveRiver(rng);

        // Step 5: Build the town center
        buildTown(rng);

        // Step 6: Carve paths connecting key locations
        carvePaths(rng);

        // Step 7: Add ruins in the ruins biome
        buildRuins(rng);

        // Step 8: Scatter decoration (bushes, mushrooms, flowers, graves)
        scatterDecoration(rng);

        // Step 9: Border walls with gate
        buildBorders();

        // Step 10: Place entities from area data
        placeEntities(area, rng);
    }

    // --- Simplex-ish noise via layered random ---
    private void generateElevation(Random rng) {
        // Multi-octave noise approximation
        float[][] noise1 = randomGrid(rng, 8);   // large features
        float[][] noise2 = randomGrid(rng, 4);   // medium features
        float[][] noise3 = randomGrid(rng, 2);   // fine detail

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                float val = noise1[y][x] * 0.5f + noise2[y][x] * 0.3f + noise3[y][x] * 0.2f;
                // Gradient: lower at edges, higher in center-north
                float cx = (float) x / WIDTH - 0.5f;
                float cy = (float) y / HEIGHT - 0.6f; // offset north
                float dist = (float) Math.sqrt(cx * cx + cy * cy);
                val = val * (1.0f - dist * 1.2f);
                elevation[y][x] = Math.max(0, Math.min(1, val));
            }
        }
    }

    private float[][] randomGrid(Random rng, int scale) {
        // Create a small grid, then bilinearly interpolate to full size
        int sw = WIDTH / scale + 2;
        int sh = HEIGHT / scale + 2;
        float[][] small = new float[sh][sw];
        for (int y = 0; y < sh; y++)
            for (int x = 0; x < sw; x++)
                small[y][x] = rng.nextFloat();

        float[][] full = new float[HEIGHT][WIDTH];
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                float fx = (float) x / scale;
                float fy = (float) y / scale;
                int ix = (int) fx;
                int iy = (int) fy;
                float fracX = fx - ix;
                float fracY = fy - iy;
                ix = Math.min(ix, sw - 2);
                iy = Math.min(iy, sh - 2);
                float top = small[iy][ix] * (1 - fracX) + small[iy][ix + 1] * fracX;
                float bot = small[iy + 1][ix] * (1 - fracX) + small[iy + 1][ix + 1] * fracX;
                full[y][x] = top * (1 - fracY) + bot * fracY;
            }
        }
        return full;
    }

    private void assignBiomes() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                float nx = (float) x / WIDTH;
                float ny = (float) y / HEIGHT;

                if (nx > 0.35 && nx < 0.65 && ny > 0.35 && ny < 0.65) {
                    biome[y][x] = BIOME_TOWN;
                } else if (nx < 0.3 && ny < 0.45) {
                    biome[y][x] = BIOME_FOREST;
                } else if (nx > 0.7 && ny < 0.4) {
                    biome[y][x] = BIOME_RUINS;
                } else if (ny > 0.7 && nx < 0.4) {
                    biome[y][x] = BIOME_SWAMP;
                } else {
                    biome[y][x] = BIOME_VALE;
                }
            }
        }
    }

    private void layBaseTerrain(Random rng) {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                float e = elevation[y][x];
                int b = biome[y][x];

                if (e < 0.15f) {
                    tiles[y][x] = TILE_WATER;
                } else if (e < 0.2f) {
                    tiles[y][x] = TILE_SAND;
                } else if (e > 0.75f) {
                    tiles[y][x] = TILE_STONE;
                } else if (e > 0.85f) {
                    tiles[y][x] = TILE_DARK_STONE;
                } else {
                    // Biome-specific ground
                    switch (b) {
                        case BIOME_FOREST:
                            if (rng.nextFloat() < 0.35f) tiles[y][x] = TILE_TREE;
                            else if (rng.nextFloat() < 0.15f) tiles[y][x] = TILE_PINE_TREE;
                            else if (rng.nextFloat() < 0.05f) tiles[y][x] = TILE_BUSH;
                            else tiles[y][x] = rng.nextFloat() < 0.3f ? TILE_TALL_GRASS : TILE_GRASS;
                            break;
                        case BIOME_SWAMP:
                            if (rng.nextFloat() < 0.2f) tiles[y][x] = TILE_WATER;
                            else if (rng.nextFloat() < 0.15f) tiles[y][x] = TILE_DEAD_TREE;
                            else if (rng.nextFloat() < 0.1f) tiles[y][x] = TILE_MUSHROOM;
                            else tiles[y][x] = rng.nextFloat() < 0.4f ? TILE_DIRT : TILE_TALL_GRASS;
                            break;
                        case BIOME_RUINS:
                            if (rng.nextFloat() < 0.1f) tiles[y][x] = TILE_DARK_STONE;
                            else if (rng.nextFloat() < 0.05f) tiles[y][x] = TILE_RUINS;
                            else tiles[y][x] = rng.nextFloat() < 0.3f ? TILE_DIRT : TILE_GRASS;
                            break;
                        case BIOME_TOWN:
                            tiles[y][x] = TILE_GRASS; // town will be carved separately
                            break;
                        default: // VALE
                            if (rng.nextFloat() < 0.08f) tiles[y][x] = TILE_TREE;
                            else if (rng.nextFloat() < 0.04f) tiles[y][x] = TILE_FLOWERS;
                            else if (rng.nextFloat() < 0.1f) tiles[y][x] = TILE_TALL_GRASS;
                            else tiles[y][x] = TILE_GRASS;
                            break;
                    }
                }
            }
        }
    }

    private void carveRiver(Random rng) {
        int rx = WIDTH / 3 + rng.nextInt(4) - 2;
        for (int y = 2; y < HEIGHT - 2; y++) {
            int width = 1 + rng.nextInt(2);
            for (int w = 0; w < width; w++) {
                int xx = rx + w;
                if (xx > 1 && xx < WIDTH - 2) {
                    tiles[y][xx] = TILE_WATER;
                    if (y > 0) elevation[y][xx] = 0.1f;
                }
            }
            // Meander
            rx += rng.nextInt(3) - 1;
            rx = Math.max(3, Math.min(WIDTH / 2 - 2, rx));
        }

        // Place a bridge across the river
        int bridgeY = HEIGHT / 2 + rng.nextInt(3) - 1;
        for (int x = Math.max(1, rx - 2); x <= Math.min(WIDTH - 2, rx + 3); x++) {
            if (tiles[bridgeY][x] == TILE_WATER) {
                tiles[bridgeY][x] = TILE_BRIDGE;
            }
        }
    }

    private void buildTown(Random rng) {
        int cx = WIDTH / 2;
        int cy = HEIGHT / 2;

        // Town square with cobblestone
        for (int dy = -3; dy <= 3; dy++) {
            for (int dx = -4; dx <= 4; dx++) {
                int x = cx + dx, y = cy + dy;
                if (x > 0 && x < WIDTH - 1 && y > 0 && y < HEIGHT - 1) {
                    tiles[y][x] = TILE_COBBLE;
                    biome[y][x] = BIOME_TOWN;
                }
            }
        }

        // Well in center
        tiles[cy][cx] = TILE_WELL;

        // Torches around the square
        tiles[cy - 3][cx - 4] = TILE_TORCH;
        tiles[cy - 3][cx + 4] = TILE_TORCH;
        tiles[cy + 3][cx - 4] = TILE_TORCH;
        tiles[cy + 3][cx + 4] = TILE_TORCH;

        // Building: Auction Hall (northwest of square)
        buildHouse(cx - 8, cy - 5, 5, 4);

        // Building: Warden's Office (northeast of square)
        buildHouse(cx + 5, cy - 5, 5, 4);

        // Building: Inn (south of square)
        buildHouse(cx - 2, cy + 5, 6, 4);

        // Fences around gardens
        for (int x = cx + 5; x < cx + 10 && x < WIDTH - 1; x++) {
            if (cy + 3 < HEIGHT - 1) tiles[cy + 3][x] = TILE_FENCE;
        }

        // Campfire near inn
        if (cy + 9 < HEIGHT - 1 && cx + 2 < WIDTH - 1) {
            tiles[cy + 9][cx + 2] = TILE_CAMPFIRE;
        }
    }

    private void buildHouse(int hx, int hy, int w, int h) {
        // Roof
        for (int dx = 0; dx < w && hx + dx < WIDTH - 1; dx++) {
            if (hy > 0 && hx + dx > 0) tiles[hy][hx + dx] = TILE_ROOF;
        }
        // Walls
        for (int dy = 1; dy < h && hy + dy < HEIGHT - 1; dy++) {
            if (hx > 0) tiles[hy + dy][hx] = TILE_WALL;
            if (hx + w - 1 < WIDTH - 1) tiles[hy + dy][hx + w - 1] = TILE_WALL;
        }
        // Bottom wall with door
        if (hy + h < HEIGHT - 1) {
            for (int dx = 0; dx < w && hx + dx < WIDTH - 1; dx++) {
                if (hx + dx > 0) tiles[hy + h][hx + dx] = TILE_WALL;
            }
            // Door in the center
            int doorX = hx + w / 2;
            if (doorX > 0 && doorX < WIDTH - 1) tiles[hy + h][doorX] = TILE_DOOR;
        }
        // Interior floor
        for (int dy = 1; dy < h; dy++) {
            for (int dx = 1; dx < w - 1; dx++) {
                int x = hx + dx, y = hy + dy;
                if (x > 0 && x < WIDTH - 1 && y > 0 && y < HEIGHT - 1) {
                    tiles[y][x] = TILE_COBBLE;
                }
            }
        }
    }

    private void carvePaths(Random rng) {
        int cx = WIDTH / 2, cy = HEIGHT / 2;

        // Main road: gate (south) to town center
        carvePath(cx, HEIGHT - 2, cx, cy + 4);

        // Road east to ruins
        carvePath(cx + 5, cy, WIDTH - 3, cy - 2);

        // Road west to forest
        carvePath(cx - 5, cy, 3, cy + 2);

        // Road south to swamp
        carvePath(cx - 6, cy + 4, 4, HEIGHT - 3);

        // Branches off the main road
        carvePath(cx, cy - 4, cx - 8, cy - 5 + 4); // to auction hall door
        carvePath(cx, cy - 4, cx + 5 + 2, cy - 5 + 4); // to warden's door
    }

    private void carvePath(int x1, int y1, int x2, int y2) {
        // L-shaped path: horizontal then vertical
        int x = x1, y = y1;
        while (x != x2) {
            if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT) {
                if (tiles[y][x] == TILE_GRASS || tiles[y][x] == TILE_TALL_GRASS
                        || tiles[y][x] == TILE_DIRT || tiles[y][x] == TILE_FLOWERS) {
                    tiles[y][x] = TILE_PATH;
                }
            }
            x += Integer.compare(x2, x);
        }
        while (y != y2) {
            if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT) {
                if (tiles[y][x] == TILE_GRASS || tiles[y][x] == TILE_TALL_GRASS
                        || tiles[y][x] == TILE_DIRT || tiles[y][x] == TILE_FLOWERS
                        || tiles[y][x] == TILE_COBBLE) {
                    tiles[y][x] = TILE_PATH;
                }
            }
            y += Integer.compare(y2, y);
        }
    }

    private void buildRuins(Random rng) {
        // Scatter broken walls and graves in the ruins biome
        for (int y = 2; y < HEIGHT - 2; y++) {
            for (int x = 2; x < WIDTH - 2; x++) {
                if (biome[y][x] == BIOME_RUINS) {
                    if (tiles[y][x] == TILE_GRASS || tiles[y][x] == TILE_DIRT) {
                        float r = rng.nextFloat();
                        if (r < 0.04f) tiles[y][x] = TILE_RUINS;
                        else if (r < 0.06f) tiles[y][x] = TILE_GRAVE;
                        else if (r < 0.08f) tiles[y][x] = TILE_DARK_STONE;
                    }
                }
            }
        }
    }

    private void scatterDecoration(Random rng) {
        for (int y = 2; y < HEIGHT - 2; y++) {
            for (int x = 2; x < WIDTH - 2; x++) {
                if (tiles[y][x] == TILE_GRASS && biome[y][x] == BIOME_VALE) {
                    float r = rng.nextFloat();
                    if (r < 0.02f) tiles[y][x] = TILE_FLOWERS;
                    else if (r < 0.03f) tiles[y][x] = TILE_BUSH;
                }
                if (tiles[y][x] == TILE_TALL_GRASS && biome[y][x] == BIOME_SWAMP) {
                    if (rng.nextFloat() < 0.05f) tiles[y][x] = TILE_MUSHROOM;
                }
            }
        }
    }

    private void buildBorders() {
        for (int x = 0; x < WIDTH; x++) {
            tiles[0][x] = TILE_WALL;
            tiles[HEIGHT - 1][x] = TILE_WALL;
        }
        for (int y = 0; y < HEIGHT; y++) {
            tiles[y][0] = TILE_WALL;
            tiles[y][WIDTH - 1] = TILE_WALL;
        }
        // Gate entrance at bottom center
        tiles[HEIGHT - 1][WIDTH / 2] = TILE_DOOR;
        tiles[HEIGHT - 1][WIDTH / 2 + 1] = TILE_DOOR;
    }

    private void placeEntities(Area area, Random rng) {
        int cx = WIDTH / 2, cy = HEIGHT / 2;

        // Place resources in appropriate biomes
        ResourceNode[] nodes = area.getResources().toArray(new ResourceNode[0]);
        int[][] resourceTargets = {
                {cx - 12, cy + 8},  // fishing -> near river/swamp
                {cx + 12, cy - 2},  // mining -> near ruins
                {cx - 10, cy - 6},  // foraging -> in forest
                {cx + 6, cy + 8},   // farming -> south vale
                {cx - 6, cy - 2},   // alchemy -> near forest edge
                {cx + 8, cy - 8},   // mapping -> in ruins
        };

        for (int i = 0; i < nodes.length && i < resourceTargets.length; i++) {
            int tx = resourceTargets[i][0];
            int ty = resourceTargets[i][1];
            if (placeEntityNear(rng, tx, ty, MapEntity.TYPE_RESOURCE, nodes[i].getName(),
                    nodes[i].getSkillType() + " - " + nodes[i].getResourceItem())) {
                // placed
            }
        }

        // Place monsters in dangerous areas (ruins and swamp edges)
        for (Npc monster : area.getMonsters()) {
            int mx, my;
            if (monster.getName().contains("Mist") || monster.getName().contains("Grave")) {
                mx = 4 + rng.nextInt(WIDTH / 3);
                my = HEIGHT * 2 / 3 + rng.nextInt(HEIGHT / 4);
            } else if (monster.getName().contains("Stone") || monster.getName().contains("Acolyte")) {
                mx = WIDTH * 2 / 3 + rng.nextInt(WIDTH / 4 - 2);
                my = 4 + rng.nextInt(HEIGHT / 3);
            } else {
                mx = 4 + rng.nextInt(WIDTH - 8);
                my = 4 + rng.nextInt(HEIGHT - 8);
            }
            placeEntityNear(rng, mx, my, MapEntity.TYPE_MONSTER, monster.getName(),
                    "HP: " + monster.getHealth() + " | ATK: " + monster.getAttackPower());
        }

        // NPC: Warden-Scribe Elowen near the warden's office
        placeEntityNear(rng, cx + 7, cy - 2, MapEntity.TYPE_NPC,
                "Warden-Scribe Elowen", "Talk to learn skills and receive starter items.");

        // Auction house NPC
        if (area.getAuctionHouse() != null) {
            placeEntityNear(rng, cx - 6, cy - 3, MapEntity.TYPE_NPC,
                    area.getAuctionHouse().getName(), area.getAuctionHouse().getDescription());
        }
    }

    private boolean placeEntityNear(Random rng, int tx, int ty, int type, String name, String desc) {
        for (int attempt = 0; attempt < 40; attempt++) {
            int x = tx + rng.nextInt(5) - 2;
            int y = ty + rng.nextInt(5) - 2;
            if (x > 1 && x < WIDTH - 2 && y > 1 && y < HEIGHT - 2
                    && isWalkable(x, y) && getEntityAt(x, y) == null
                    && tiles[y][x] != TILE_COBBLE && tiles[y][x] != TILE_PATH) {
                // Clear the tile so entity is on grass
                tiles[y][x] = TILE_GRASS;
                entities.add(new MapEntity(x, y, type, name, desc));
                return true;
            }
        }
        // Fallback: place on any walkable tile
        for (int y = 2; y < HEIGHT - 2; y++) {
            for (int x = 2; x < WIDTH - 2; x++) {
                if (isWalkable(x, y) && getEntityAt(x, y) == null) {
                    tiles[y][x] = TILE_GRASS;
                    entities.add(new MapEntity(x, y, type, name, desc));
                    return true;
                }
            }
        }
        return false;
    }
}
