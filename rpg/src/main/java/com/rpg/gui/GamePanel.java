package com.rpg.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * Enhanced game renderer with biome-aware coloring, elevation shading,
 * detailed tile art, animated water/torches, and improved entity sprites.
 */
public class GamePanel extends JPanel {
    public static final int TILE_SIZE = 32;

    private TileMap tileMap;
    private int playerX;
    private int playerY;
    private int playerFacing = 0; // 0=down, 1=left, 2=right, 3=up
    private GameController controller;
    private long animTick = 0;

    // Camera offset for scrolling
    private int camX = 0;
    private int camY = 0;
    private int viewW, viewH; // tiles visible

    // Biome palettes — [biome][variant]
    private static final Color[][] GRASS_COLORS = {
        { new Color(72, 128, 60), new Color(65, 118, 54) },       // Vale
        { new Color(40, 90, 38), new Color(35, 82, 33) },         // Forest (darker)
        { new Color(62, 95, 50), new Color(55, 88, 45) },         // Swamp (muted)
        { new Color(78, 105, 62), new Color(70, 98, 55) },        // Ruins (desaturated)
        { new Color(80, 135, 65), new Color(72, 125, 58) },       // Town (bright)
    };

    // Tile colors
    private static final Color COLOR_WATER_DEEP = new Color(25, 55, 110);
    private static final Color COLOR_WATER = new Color(40, 80, 140);
    private static final Color COLOR_WATER_LIGHT = new Color(60, 110, 170);
    private static final Color COLOR_SAND = new Color(194, 178, 128);
    private static final Color COLOR_SAND_DARK = new Color(178, 162, 112);
    private static final Color COLOR_DIRT = new Color(120, 90, 60);
    private static final Color COLOR_DIRT_DARK = new Color(100, 72, 45);
    private static final Color COLOR_PATH = new Color(170, 150, 110);
    private static final Color COLOR_PATH_DARK = new Color(150, 130, 90);
    private static final Color COLOR_COBBLE = new Color(140, 138, 135);
    private static final Color COLOR_COBBLE_DARK = new Color(115, 113, 110);
    private static final Color COLOR_STONE = new Color(135, 135, 140);
    private static final Color COLOR_DARK_STONE = new Color(65, 60, 70);
    private static final Color COLOR_WALL = new Color(75, 70, 68);
    private static final Color COLOR_WALL_LIGHT = new Color(95, 90, 88);
    private static final Color COLOR_DOOR = new Color(145, 105, 55);
    private static final Color COLOR_ROOF = new Color(140, 55, 35);
    private static final Color COLOR_ROOF_DARK = new Color(110, 40, 25);
    private static final Color COLOR_FENCE = new Color(130, 100, 55);
    private static final Color COLOR_BRIDGE = new Color(120, 90, 50);

    // Entity colors
    private static final Color COLOR_PLAYER = new Color(50, 130, 220);
    private static final Color COLOR_PLAYER_DARK = new Color(30, 85, 165);
    private static final Color COLOR_RESOURCE = new Color(230, 190, 40);
    private static final Color COLOR_MONSTER = new Color(190, 40, 40);
    private static final Color COLOR_NPC = new Color(90, 190, 90);

    private int highlightX = -1, highlightY = -1;

    // Animation timer
    private final Timer animTimer;

    public GamePanel() {
        setBackground(Color.BLACK);
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (controller == null || tileMap == null) return;
                int dx = 0, dy = 0;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W: case KeyEvent.VK_UP:    dy = -1; playerFacing = 3; break;
                    case KeyEvent.VK_S: case KeyEvent.VK_DOWN:  dy = 1;  playerFacing = 0; break;
                    case KeyEvent.VK_A: case KeyEvent.VK_LEFT:  dx = -1; playerFacing = 1; break;
                    case KeyEvent.VK_D: case KeyEvent.VK_RIGHT: dx = 1;  playerFacing = 2; break;
                    case KeyEvent.VK_E: case KeyEvent.VK_SPACE:
                        controller.interact(playerX, playerY);
                        return;
                }
                if (dx != 0 || dy != 0) movePlayer(dx, dy);
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                highlightX = e.getX() / TILE_SIZE + camX;
                highlightY = e.getY() / TILE_SIZE + camY;
                repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (controller == null || tileMap == null) return;
                int tx = e.getX() / TILE_SIZE + camX;
                int ty = e.getY() / TILE_SIZE + camY;
                int dx = Integer.compare(tx, playerX);
                int dy = Integer.compare(ty, playerY);
                if (dx != 0 || dy != 0) {
                    if (dx != 0) playerFacing = dx < 0 ? 1 : 2;
                    else playerFacing = dy < 0 ? 3 : 0;
                    movePlayer(dx, dy);
                }
            }
        });

        // 200ms animation tick for water shimmer, torch flicker, etc.
        animTimer = new Timer(200, e -> { animTick++; repaint(); });
        animTimer.start();
    }

    public void init(TileMap map, int startX, int startY, GameController ctrl) {
        this.tileMap = map;
        this.playerX = startX;
        this.playerY = startY;
        this.controller = ctrl;
        updateCamera();
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(TileMap.WIDTH * TILE_SIZE, TileMap.HEIGHT * TILE_SIZE);
    }

    public int getPlayerX() { return playerX; }
    public int getPlayerY() { return playerY; }
    public TileMap getTileMap() { return tileMap; }

    private void movePlayer(int dx, int dy) {
        int nx = playerX + dx;
        int ny = playerY + dy;
        if (tileMap.isWalkable(nx, ny) && tileMap.getEntityAt(nx, ny) == null) {
            playerX = nx;
            playerY = ny;
            updateCamera();
            controller.onPlayerMoved(playerX, playerY);
            repaint();
        } else {
            MapEntity entity = tileMap.getEntityAt(nx, ny);
            if (entity != null) controller.onEntityContact(entity);
        }
    }

    private void updateCamera() {
        viewW = getWidth() / TILE_SIZE;
        viewH = getHeight() / TILE_SIZE;
        if (viewW <= 0) viewW = TileMap.WIDTH;
        if (viewH <= 0) viewH = TileMap.HEIGHT;

        camX = playerX - viewW / 2;
        camY = playerY - viewH / 2;
        camX = Math.max(0, Math.min(TileMap.WIDTH - viewW, camX));
        camY = Math.max(0, Math.min(TileMap.HEIGHT - viewH, camY));
    }

    // ==================== RENDERING ====================

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (tileMap == null) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        updateCamera();
        int endX = Math.min(camX + viewW + 1, TileMap.WIDTH);
        int endY = Math.min(camY + viewH + 1, TileMap.HEIGHT);

        // Draw tiles
        for (int y = camY; y < endY; y++) {
            for (int x = camX; x < endX; x++) {
                int px = (x - camX) * TILE_SIZE;
                int py = (y - camY) * TILE_SIZE;
                drawTile(g2, x, y, px, py);
            }
        }

        // Draw entities
        for (MapEntity entity : tileMap.getEntities()) {
            int ex = entity.getX(), ey = entity.getY();
            if (ex >= camX && ex < endX && ey >= camY && ey < endY) {
                int px = (ex - camX) * TILE_SIZE;
                int py = (ey - camY) * TILE_SIZE;
                drawEntity(g2, entity, px, py);
            }
        }

        // Draw player
        int ppx = (playerX - camX) * TILE_SIZE;
        int ppy = (playerY - camY) * TILE_SIZE;
        drawPlayer(g2, ppx, ppy);

        // Highlight + tooltip
        if (highlightX >= camX && highlightX < endX && highlightY >= camY && highlightY < endY) {
            int hpx = (highlightX - camX) * TILE_SIZE;
            int hpy = (highlightY - camY) * TILE_SIZE;
            g2.setColor(new Color(255, 255, 255, 50));
            g2.fillRect(hpx, hpy, TILE_SIZE, TILE_SIZE);
            g2.setColor(new Color(255, 255, 255, 100));
            g2.drawRect(hpx, hpy, TILE_SIZE - 1, TILE_SIZE - 1);

            MapEntity hover = tileMap.getEntityAt(highlightX, highlightY);
            if (hover != null) drawTooltip(g2, hover.getName(), hpx, hpy - 10);
        }

        // Minimap
        drawMinimap(g2);

        // HUD
        g2.setFont(new Font("Monospaced", Font.PLAIN, 10));
        g2.setColor(new Color(255, 255, 255, 160));
        g2.drawString("WASD=Move  E=Interact  [" + playerX + "," + playerY + "]", 8, getHeight() - 6);
    }

    // ==================== TILE DRAWING ====================

    private void drawTile(Graphics2D g, int tx, int ty, int px, int py) {
        int tile = tileMap.getTile(tx, ty);
        int b = tileMap.getBiome(tx, ty);
        float elev = tileMap.getElevation(tx, ty);
        int s = TILE_SIZE;

        // Elevation shading factor
        float shade = 0.85f + elev * 0.3f;

        switch (tile) {
            case TileMap.TILE_GRASS:
                Color gc = GRASS_COLORS[b][(tx + ty) % 2];
                g.setColor(shadeColor(gc, shade));
                g.fillRect(px, py, s, s);
                // Subtle grass blades
                if ((tx * 7 + ty * 13) % 5 == 0) {
                    g.setColor(shadeColor(gc.brighter(), shade * 0.9f));
                    g.drawLine(px + 8, py + s - 2, px + 10, py + s - 8);
                    g.drawLine(px + 20, py + s - 3, px + 22, py + s - 9);
                }
                break;

            case TileMap.TILE_TALL_GRASS:
                gc = GRASS_COLORS[b][0];
                g.setColor(shadeColor(gc, shade));
                g.fillRect(px, py, s, s);
                g.setColor(shadeColor(gc.brighter(), shade));
                for (int i = 0; i < 6; i++) {
                    int bx = px + 3 + ((tx * 3 + i * 7) % 24);
                    int by = py + s;
                    g.drawLine(bx, by, bx + (i % 2 == 0 ? 1 : -1), by - 8 - (i % 3) * 3);
                }
                break;

            case TileMap.TILE_FLOWERS:
                gc = GRASS_COLORS[b][0];
                g.setColor(shadeColor(gc, shade));
                g.fillRect(px, py, s, s);
                // Colorful flower dots
                Color[] petals = { new Color(220, 60, 80), new Color(200, 180, 50),
                                   new Color(150, 60, 200), new Color(255, 140, 60) };
                for (int i = 0; i < 4; i++) {
                    int fx = px + 4 + ((tx * 5 + i * 11) % 22);
                    int fy = py + 4 + ((ty * 7 + i * 13) % 22);
                    g.setColor(petals[(tx + ty + i) % petals.length]);
                    g.fillOval(fx, fy, 4, 4);
                    g.setColor(new Color(255, 255, 100));
                    g.fillOval(fx + 1, fy + 1, 2, 2);
                }
                break;

            case TileMap.TILE_WATER:
                int waveOff = (int) ((animTick + tx) % 3);
                g.setColor(waveOff == 0 ? COLOR_WATER : COLOR_WATER_LIGHT);
                g.fillRect(px, py, s, s);
                g.setColor(new Color(100, 180, 230, 50 + waveOff * 20));
                g.fillRect(px + 3 + waveOff * 2, py + 10 + waveOff, 18, 3);
                g.fillRect(px + 8 - waveOff, py + 22 - waveOff, 14, 2);
                break;

            case TileMap.TILE_DEEP_WATER:
                g.setColor(COLOR_WATER_DEEP);
                g.fillRect(px, py, s, s);
                break;

            case TileMap.TILE_SAND:
                g.setColor((tx + ty) % 2 == 0 ? COLOR_SAND : COLOR_SAND_DARK);
                g.fillRect(px, py, s, s);
                g.setColor(new Color(210, 195, 150, 60));
                g.fillOval(px + 6, py + 14, 5, 3);
                break;

            case TileMap.TILE_DIRT:
                g.setColor((tx + ty) % 2 == 0 ? COLOR_DIRT : COLOR_DIRT_DARK);
                g.fillRect(px, py, s, s);
                g.setColor(COLOR_DIRT_DARK.darker());
                g.fillOval(px + 10, py + 8, 4, 3);
                g.fillOval(px + 22, py + 20, 3, 2);
                break;

            case TileMap.TILE_STONE:
                g.setColor(shadeColor(COLOR_STONE, shade));
                g.fillRect(px, py, s, s);
                g.setColor(COLOR_STONE.darker());
                g.fillRoundRect(px + 2, py + 3, 12, 10, 3, 3);
                g.fillRoundRect(px + 16, py + 15, 14, 12, 3, 3);
                g.setColor(COLOR_STONE.brighter());
                g.drawLine(px + 4, py + 5, px + 11, py + 5);
                break;

            case TileMap.TILE_DARK_STONE:
                g.setColor(shadeColor(COLOR_DARK_STONE, shade));
                g.fillRect(px, py, s, s);
                g.setColor(COLOR_DARK_STONE.brighter());
                g.fillRoundRect(px + 3, py + 4, 10, 8, 2, 2);
                g.fillRoundRect(px + 18, py + 16, 11, 10, 2, 2);
                break;

            case TileMap.TILE_TREE:
                drawGrassBase(g, tx, ty, px, py, b, shade);
                // Trunk
                g.setColor(new Color(85, 60, 35));
                g.fillRect(px + 13, py + 18, 6, 14);
                // Canopy — layered circles
                g.setColor(new Color(25, 80, 25));
                g.fillOval(px + 2, py + 1, 28, 22);
                g.setColor(new Color(35, 100, 35));
                g.fillOval(px + 6, py + 3, 20, 16);
                g.setColor(new Color(45, 115, 45));
                g.fillOval(px + 10, py + 5, 12, 10);
                break;

            case TileMap.TILE_PINE_TREE:
                drawGrassBase(g, tx, ty, px, py, b, shade);
                g.setColor(new Color(70, 50, 30));
                g.fillRect(px + 14, py + 22, 4, 10);
                // Triangular pine layers
                g.setColor(new Color(20, 70, 30));
                int[] xp1 = {px + 16, px + 4, px + 28};
                int[] yp1 = {py + 2, py + 18, py + 18};
                g.fillPolygon(xp1, yp1, 3);
                g.setColor(new Color(30, 85, 40));
                int[] xp2 = {px + 16, px + 7, px + 25};
                int[] yp2 = {py + 6, py + 16, py + 16};
                g.fillPolygon(xp2, yp2, 3);
                break;

            case TileMap.TILE_DEAD_TREE:
                drawGrassBase(g, tx, ty, px, py, b, shade);
                g.setColor(new Color(80, 65, 50));
                g.fillRect(px + 14, py + 12, 4, 20);
                // Bare branches
                g.setColor(new Color(70, 55, 40));
                g.drawLine(px + 16, py + 14, px + 8, py + 6);
                g.drawLine(px + 16, py + 14, px + 26, py + 4);
                g.drawLine(px + 16, py + 18, px + 6, py + 16);
                g.drawLine(px + 16, py + 18, px + 28, py + 14);
                break;

            case TileMap.TILE_BUSH:
                drawGrassBase(g, tx, ty, px, py, b, shade);
                g.setColor(new Color(30, 90, 30));
                g.fillOval(px + 4, py + 10, 24, 18);
                g.setColor(new Color(40, 110, 40));
                g.fillOval(px + 8, py + 12, 16, 12);
                break;

            case TileMap.TILE_MUSHROOM:
                drawGrassBase(g, tx, ty, px, py, b, shade);
                // Stem
                g.setColor(new Color(220, 210, 190));
                g.fillRect(px + 14, py + 20, 4, 8);
                // Cap
                g.setColor(new Color(180, 40, 30));
                g.fillOval(px + 8, py + 12, 16, 12);
                g.setColor(new Color(255, 255, 255, 150));
                g.fillOval(px + 12, py + 14, 3, 3);
                g.fillOval(px + 18, py + 16, 2, 2);
                break;

            case TileMap.TILE_PATH:
                g.setColor((tx + ty) % 2 == 0 ? COLOR_PATH : COLOR_PATH_DARK);
                g.fillRect(px, py, s, s);
                // Pebble details
                g.setColor(COLOR_PATH_DARK.darker());
                g.fillOval(px + 5 + (tx * 3 % 7), py + 8 + (ty * 5 % 7), 4, 3);
                g.fillOval(px + 18 + (ty * 3 % 5), py + 20 + (tx * 7 % 5), 3, 3);
                break;

            case TileMap.TILE_COBBLE:
                g.setColor((tx + ty) % 2 == 0 ? COLOR_COBBLE : COLOR_COBBLE_DARK);
                g.fillRect(px, py, s, s);
                // Cobblestone pattern
                g.setColor(COLOR_COBBLE_DARK);
                g.drawLine(px, py + s / 2, px + s, py + s / 2);
                int off = (ty % 2) * (s / 2);
                g.drawLine(px + off, py, px + off, py + s / 2);
                g.drawLine(px + (off + s / 2) % s, py + s / 2, px + (off + s / 2) % s, py + s);
                break;

            case TileMap.TILE_WALL:
                g.setColor(COLOR_WALL);
                g.fillRect(px, py, s, s);
                g.setColor(COLOR_WALL_LIGHT);
                g.drawRect(px + 1, py + 1, s - 3, s / 2 - 2);
                g.drawRect(px + s / 4, py + s / 2, s / 2, s / 2 - 2);
                // Mortar lines
                g.setColor(COLOR_WALL.darker());
                g.drawLine(px, py + s / 2, px + s, py + s / 2);
                break;

            case TileMap.TILE_DOOR:
                g.setColor(COLOR_DOOR);
                g.fillRect(px + 2, py, s - 4, s);
                g.setColor(COLOR_DOOR.darker());
                g.fillRect(px + 4, py + 2, s - 8, s - 4);
                // Arch
                g.setColor(COLOR_WALL);
                g.fillRect(px, py, s, 4);
                // Handle
                g.setColor(new Color(210, 190, 80));
                g.fillOval(px + s / 2 + 4, py + s / 2, 4, 4);
                break;

            case TileMap.TILE_ROOF:
                g.setColor(COLOR_ROOF);
                g.fillRect(px, py, s, s);
                // Shingle pattern
                for (int row = 0; row < 4; row++) {
                    int roofOff = (row % 2) * (s / 4);
                    g.setColor(row % 2 == 0 ? COLOR_ROOF_DARK : COLOR_ROOF);
                    for (int col = 0; col < 4; col++) {
                        g.drawArc(px + roofOff + col * (s / 4) - 2, py + row * (s / 4), s / 4, s / 4, 0, 180);
                    }
                }
                break;

            case TileMap.TILE_FENCE:
                drawGrassBase(g, tx, ty, px, py, b, shade);
                g.setColor(COLOR_FENCE);
                // Horizontal rails
                g.fillRect(px, py + 10, s, 3);
                g.fillRect(px, py + 22, s, 3);
                // Posts
                g.fillRect(px + 2, py + 6, 3, 22);
                g.fillRect(px + s - 5, py + 6, 3, 22);
                break;

            case TileMap.TILE_BRIDGE:
                g.setColor(COLOR_BRIDGE);
                g.fillRect(px, py, s, s);
                g.setColor(COLOR_BRIDGE.darker());
                // Plank lines
                for (int i = 0; i < 4; i++) {
                    g.drawLine(px, py + 2 + i * 8, px + s, py + 2 + i * 8);
                }
                // Railings
                g.setColor(COLOR_FENCE);
                g.fillRect(px, py, s, 2);
                g.fillRect(px, py + s - 2, s, 2);
                break;

            case TileMap.TILE_GRAVE:
                drawGrassBase(g, tx, ty, px, py, b, shade);
                // Headstone
                g.setColor(new Color(100, 95, 90));
                g.fillRoundRect(px + 10, py + 6, 12, 16, 4, 4);
                g.setColor(new Color(80, 75, 70));
                g.drawLine(px + 14, py + 10, px + 14, py + 18);
                g.drawLine(px + 12, py + 13, px + 20, py + 13);
                // Dirt mound
                g.setColor(COLOR_DIRT);
                g.fillOval(px + 6, py + 20, 20, 8);
                break;

            case TileMap.TILE_RUINS:
                drawGrassBase(g, tx, ty, px, py, b, shade);
                // Broken wall segments
                g.setColor(new Color(90, 85, 80));
                g.fillRect(px + 2, py + 12, 8, 20);
                g.fillRect(px + 20, py + 8, 10, 24);
                g.setColor(new Color(75, 70, 65));
                g.fillRect(px + 12, py + 18, 6, 14);
                break;

            case TileMap.TILE_CAMPFIRE:
                drawGrassBase(g, tx, ty, px, py, b, shade);
                // Stone ring
                g.setColor(new Color(100, 95, 90));
                g.drawOval(px + 6, py + 12, 20, 16);
                // Fire (animated)
                int flicker = (int) (animTick % 3);
                g.setColor(new Color(220, 120, 20));
                g.fillOval(px + 10 + flicker, py + 12, 12 - flicker, 14);
                g.setColor(new Color(255, 200, 40, 200));
                g.fillOval(px + 12, py + 14 + flicker, 8 - flicker, 8);
                g.setColor(new Color(255, 255, 180, 120));
                g.fillOval(px + 14, py + 16, 4, 4);
                // Light glow
                g.setColor(new Color(255, 180, 60, 30));
                g.fillOval(px - 8, py - 4, s + 16, s + 8);
                break;

            case TileMap.TILE_WELL:
                g.setColor(COLOR_COBBLE);
                g.fillRect(px, py, s, s);
                // Stone ring
                g.setColor(new Color(100, 95, 90));
                g.fillOval(px + 4, py + 6, 24, 20);
                // Dark water inside
                g.setColor(COLOR_WATER_DEEP);
                g.fillOval(px + 8, py + 10, 16, 12);
                // Posts and roof
                g.setColor(new Color(90, 65, 35));
                g.fillRect(px + 6, py, 3, 12);
                g.fillRect(px + 23, py, 3, 12);
                g.setColor(COLOR_ROOF);
                g.fillRect(px + 4, py - 2, 24, 4);
                break;

            case TileMap.TILE_TORCH:
                g.setColor(COLOR_COBBLE);
                g.fillRect(px, py, s, s);
                // Torch pole
                g.setColor(new Color(90, 65, 35));
                g.fillRect(px + 14, py + 10, 4, 20);
                // Flame (animated)
                int tf = (int) (animTick % 2);
                g.setColor(new Color(255, 160, 30));
                g.fillOval(px + 12 + tf, py + 2, 8 - tf, 12);
                g.setColor(new Color(255, 220, 80, 200));
                g.fillOval(px + 13, py + 4 + tf, 6, 6);
                // Glow
                g.setColor(new Color(255, 200, 80, 25));
                g.fillOval(px - 6, py - 6, s + 12, s + 12);
                break;
        }
    }

    private void drawGrassBase(Graphics2D g, int tx, int ty, int px, int py, int b, float shade) {
        Color gc = GRASS_COLORS[b][(tx + ty) % 2];
        g.setColor(shadeColor(gc, shade));
        g.fillRect(px, py, TILE_SIZE, TILE_SIZE);
    }

    // ==================== ENTITY DRAWING ====================

    private void drawEntity(Graphics2D g, MapEntity entity, int px, int py) {
        int s = TILE_SIZE;

        switch (entity.getType()) {
            case MapEntity.TYPE_RESOURCE:
                // Glowing resource node
                g.setColor(new Color(255, 220, 60, 35));
                g.fillOval(px - 2, py - 2, s + 4, s + 4);
                g.setColor(COLOR_RESOURCE);
                g.fillOval(px + 6, py + 6, 20, 20);
                g.setColor(COLOR_RESOURCE.brighter());
                g.fillOval(px + 10, py + 10, 12, 12);
                // Sparkle
                int sparkle = (int) (animTick % 4);
                g.setColor(new Color(255, 255, 200, 120 + sparkle * 30));
                g.fillOval(px + 8 + sparkle * 2, py + 4 + sparkle, 3, 3);
                // Type indicator
                g.setColor(new Color(255, 255, 255, 180));
                g.setFont(new Font("SansSerif", Font.BOLD, 8));
                String initial = entity.getName().substring(0, 1);
                g.drawString(initial, px + 14, py + 20);
                break;

            case MapEntity.TYPE_MONSTER:
                // Shadow
                g.setColor(new Color(0, 0, 0, 40));
                g.fillOval(px + 4, py + 24, 24, 8);
                // Body
                g.setColor(COLOR_MONSTER);
                g.fillOval(px + 6, py + 6, 20, 22);
                g.setColor(COLOR_MONSTER.darker());
                g.fillOval(px + 8, py + 10, 16, 16);
                // Eyes (glowing)
                g.setColor(new Color(255, 255, 60));
                g.fillOval(px + 10, py + 12, 5, 5);
                g.fillOval(px + 18, py + 12, 5, 5);
                g.setColor(new Color(200, 0, 0));
                g.fillOval(px + 11, py + 13, 3, 3);
                g.fillOval(px + 19, py + 13, 3, 3);
                // Horns
                g.setColor(new Color(160, 140, 100));
                int[] hx1 = {px + 9, px + 6, px + 12};
                int[] hy1 = {py + 8, py + 1, py + 6};
                g.fillPolygon(hx1, hy1, 3);
                int[] hx2 = {px + 23, px + 26, px + 20};
                int[] hy2 = {py + 8, py + 1, py + 6};
                g.fillPolygon(hx2, hy2, 3);
                break;

            case MapEntity.TYPE_NPC:
                // Shadow
                g.setColor(new Color(0, 0, 0, 40));
                g.fillOval(px + 6, py + 24, 20, 8);
                // Body/robe
                g.setColor(COLOR_NPC.darker());
                g.fillRoundRect(px + 9, py + 16, 14, 14, 4, 4);
                // Head
                g.setColor(new Color(220, 190, 160));
                g.fillOval(px + 10, py + 4, 12, 14);
                // Eyes
                g.setColor(new Color(40, 40, 40));
                g.fillOval(px + 13, py + 9, 3, 3);
                g.fillOval(px + 18, py + 9, 3, 3);
                // Hat/hood
                g.setColor(COLOR_NPC);
                g.fillArc(px + 8, py + 1, 16, 12, 0, 180);
                // Quest marker if NPC
                g.setColor(new Color(255, 220, 40));
                g.setFont(new Font("SansSerif", Font.BOLD, 12));
                g.drawString("!", px + 14, py);
                break;

            case MapEntity.TYPE_PORTAL:
                // Swirling portal effect
                int pulseMod = (int) (animTick % 4);
                // Outer glow
                g.setColor(new Color(100, 50, 200, 40 + pulseMod * 15));
                g.fillOval(px - 4, py - 4, s + 8, s + 8);
                // Portal ring
                g.setColor(new Color(140, 80, 220, 180));
                g.fillOval(px + 2, py + 2, s - 4, s - 4);
                // Inner vortex
                g.setColor(new Color(180, 120, 255, 200));
                g.fillOval(px + 6 + pulseMod, py + 6 + pulseMod, s - 12 - pulseMod, s - 12 - pulseMod);
                // Center bright spot
                g.setColor(new Color(220, 200, 255, 230));
                g.fillOval(px + 10, py + 10, s - 20, s - 20);
                // Sparkles
                g.setColor(new Color(255, 255, 255, 150));
                g.fillOval(px + 8 + (pulseMod * 3), py + 6, 3, 3);
                g.fillOval(px + 20 - (pulseMod * 2), py + 22 - pulseMod, 2, 2);
                // Label arrow
                g.setFont(new Font("SansSerif", Font.BOLD, 8));
                g.setColor(new Color(200, 180, 255));
                g.drawString("\u2192", px + 12, py + 18);
                break;
        }
    }

    // ==================== PLAYER DRAWING ====================

    private void drawPlayer(Graphics2D g, int px, int py) {
        int s = TILE_SIZE;

        // Shadow
        g.setColor(new Color(0, 0, 0, 45));
        g.fillOval(px + 5, py + 24, 22, 8);

        // Body/armor
        g.setColor(COLOR_PLAYER_DARK);
        g.fillRoundRect(px + 8, py + 15, 16, 15, 4, 4);

        // Arms based on facing
        g.setColor(COLOR_PLAYER);
        if (playerFacing == 1) { // left
            g.fillRect(px + 3, py + 16, 6, 10);  // shield arm forward
        } else if (playerFacing == 2) { // right
            g.fillRect(px + 23, py + 16, 6, 10); // sword arm forward
        } else {
            g.fillRect(px + 4, py + 17, 5, 8);
            g.fillRect(px + 23, py + 17, 5, 8);
        }

        // Head
        g.setColor(COLOR_PLAYER);
        g.fillOval(px + 8, py + 2, 16, 16);

        // Helmet details
        g.setColor(COLOR_PLAYER_DARK);
        g.drawOval(px + 8, py + 2, 16, 16);
        // Visor
        g.setColor(new Color(180, 210, 255));
        if (playerFacing == 3) { // facing up — show back of helmet
            g.setColor(COLOR_PLAYER_DARK);
            g.fillOval(px + 10, py + 4, 12, 10);
        } else {
            g.fillRect(px + 11, py + 8, 10, 4);
        }

        // Shield (left hand)
        g.setColor(new Color(160, 145, 105));
        if (playerFacing != 2) {
            g.fillOval(px + 2, py + 14, 9, 12);
            g.setColor(new Color(130, 115, 80));
            g.drawOval(px + 2, py + 14, 9, 12);
        }

        // Sword (right hand)
        g.setColor(new Color(200, 200, 215));
        if (playerFacing != 1) {
            g.fillRect(px + 25, py + 8, 3, 18);
            g.setColor(new Color(140, 100, 50));
            g.fillRect(px + 23, py + 16, 7, 3);
        }
    }

    // ==================== MINIMAP ====================

    private void drawMinimap(Graphics2D g) {
        int mmW = 96, mmH = 72;
        int mmX = getWidth() - mmW - 8;
        int mmY = 8;

        // Background
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(mmX - 2, mmY - 2, mmW + 4, mmH + 4, 4, 4);
        g.setColor(new Color(100, 100, 100, 100));
        g.drawRoundRect(mmX - 2, mmY - 2, mmW + 4, mmH + 4, 4, 4);

        float sx = (float) mmW / TileMap.WIDTH;
        float sy = (float) mmH / TileMap.HEIGHT;

        for (int y = 0; y < TileMap.HEIGHT; y++) {
            for (int x = 0; x < TileMap.WIDTH; x++) {
                int tile = tileMap.getTile(x, y);
                Color c;
                switch (tile) {
                    case TileMap.TILE_WATER: case TileMap.TILE_DEEP_WATER: c = COLOR_WATER; break;
                    case TileMap.TILE_TREE: case TileMap.TILE_PINE_TREE: c = new Color(30, 80, 30); break;
                    case TileMap.TILE_WALL: case TileMap.TILE_ROOF: c = COLOR_WALL; break;
                    case TileMap.TILE_PATH: c = COLOR_PATH; break;
                    case TileMap.TILE_COBBLE: c = COLOR_COBBLE; break;
                    case TileMap.TILE_SAND: c = COLOR_SAND; break;
                    case TileMap.TILE_STONE: case TileMap.TILE_DARK_STONE: c = COLOR_STONE; break;
                    case TileMap.TILE_BRIDGE: c = COLOR_BRIDGE; break;
                    default: c = GRASS_COLORS[tileMap.getBiome(x, y)][0]; break;
                }
                g.setColor(c);
                g.fillRect(mmX + (int)(x * sx), mmY + (int)(y * sy),
                           Math.max(1, (int)sx), Math.max(1, (int)sy));
            }
        }

        // Entity dots
        for (MapEntity e : tileMap.getEntities()) {
            switch (e.getType()) {
                case MapEntity.TYPE_RESOURCE: g.setColor(COLOR_RESOURCE); break;
                case MapEntity.TYPE_MONSTER: g.setColor(COLOR_MONSTER); break;
                case MapEntity.TYPE_NPC: g.setColor(Color.GREEN); break;
                case MapEntity.TYPE_PORTAL: g.setColor(new Color(180, 120, 255)); break;
            }
            g.fillRect(mmX + (int)(e.getX() * sx), mmY + (int)(e.getY() * sy), 2, 2);
        }

        // Player dot
        g.setColor(Color.WHITE);
        g.fillRect(mmX + (int)(playerX * sx) - 1, mmY + (int)(playerY * sy) - 1, 3, 3);
    }

    // ==================== UTILITIES ====================

    private void drawTooltip(Graphics2D g, String text, int x, int y) {
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        FontMetrics fm = g.getFontMetrics();
        int tw = fm.stringWidth(text) + 12;
        int th = fm.getHeight() + 8;
        int tx = Math.max(4, Math.min(x, getWidth() - tw - 4));
        int ty = Math.max(4, y - th);

        g.setColor(new Color(15, 15, 25, 230));
        g.fillRoundRect(tx, ty, tw, th, 8, 8);
        g.setColor(new Color(160, 160, 180));
        g.drawRoundRect(tx, ty, tw, th, 8, 8);
        g.setColor(Color.WHITE);
        g.drawString(text, tx + 6, ty + th - 7);
    }

    private Color shadeColor(Color c, float factor) {
        int r = Math.min(255, Math.max(0, (int)(c.getRed() * factor)));
        int gr = Math.min(255, Math.max(0, (int)(c.getGreen() * factor)));
        int b = Math.min(255, Math.max(0, (int)(c.getBlue() * factor)));
        return new Color(r, gr, b);
    }
}
