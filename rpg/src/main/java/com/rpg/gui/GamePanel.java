package com.rpg.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class GamePanel extends JPanel {
    public static final int TILE_SIZE = 32;

    private TileMap tileMap;
    private int playerX;
    private int playerY;
    private GameController controller;

    // Colors for tiles
    private static final Color COLOR_GRASS = new Color(58, 107, 53);
    private static final Color COLOR_GRASS_ALT = new Color(52, 97, 47);
    private static final Color COLOR_STONE = new Color(128, 128, 130);
    private static final Color COLOR_WATER = new Color(40, 80, 140);
    private static final Color COLOR_WATER_LIGHT = new Color(55, 100, 160);
    private static final Color COLOR_TREE_TRUNK = new Color(92, 64, 38);
    private static final Color COLOR_TREE_LEAVES = new Color(30, 85, 30);
    private static final Color COLOR_PATH = new Color(160, 140, 100);
    private static final Color COLOR_WALL = new Color(70, 65, 65);
    private static final Color COLOR_DOOR = new Color(140, 100, 50);
    private static final Color COLOR_PLAYER = new Color(50, 130, 220);
    private static final Color COLOR_PLAYER_OUTLINE = new Color(30, 80, 160);
    private static final Color COLOR_RESOURCE = new Color(220, 180, 40);
    private static final Color COLOR_MONSTER = new Color(190, 40, 40);
    private static final Color COLOR_NPC = new Color(100, 200, 100);
    private static final Color COLOR_HIGHLIGHT = new Color(255, 255, 255, 80);

    private int highlightX = -1;
    private int highlightY = -1;

    public GamePanel() {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(TileMap.WIDTH * TILE_SIZE, TileMap.HEIGHT * TILE_SIZE));
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (controller == null || tileMap == null) return;
                int dx = 0, dy = 0;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W: case KeyEvent.VK_UP:    dy = -1; break;
                    case KeyEvent.VK_S: case KeyEvent.VK_DOWN:  dy = 1;  break;
                    case KeyEvent.VK_A: case KeyEvent.VK_LEFT:  dx = -1; break;
                    case KeyEvent.VK_D: case KeyEvent.VK_RIGHT: dx = 1;  break;
                    case KeyEvent.VK_E: case KeyEvent.VK_SPACE:
                        controller.interact(playerX, playerY);
                        return;
                }
                if (dx != 0 || dy != 0) {
                    movePlayer(dx, dy);
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                highlightX = e.getX() / TILE_SIZE;
                highlightY = e.getY() / TILE_SIZE;
                repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (controller == null || tileMap == null) return;
                int tx = e.getX() / TILE_SIZE;
                int ty = e.getY() / TILE_SIZE;
                // Click to move toward target
                int dx = Integer.compare(tx, playerX);
                int dy = Integer.compare(ty, playerY);
                if (dx != 0 || dy != 0) {
                    movePlayer(dx, dy);
                }
            }
        });
    }

    public void init(TileMap map, int startX, int startY, GameController ctrl) {
        this.tileMap = map;
        this.playerX = startX;
        this.playerY = startY;
        this.controller = ctrl;
        repaint();
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
            controller.onPlayerMoved(playerX, playerY);
            repaint();
        } else {
            MapEntity entity = tileMap.getEntityAt(nx, ny);
            if (entity != null) {
                controller.onEntityContact(entity);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (tileMap == null) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw tiles
        for (int y = 0; y < TileMap.HEIGHT; y++) {
            for (int x = 0; x < TileMap.WIDTH; x++) {
                int px = x * TILE_SIZE;
                int py = y * TILE_SIZE;
                drawTile(g2, tileMap.getTile(x, y), px, py, x, y);
            }
        }

        // Draw entities
        for (MapEntity entity : tileMap.getEntities()) {
            int px = entity.getX() * TILE_SIZE;
            int py = entity.getY() * TILE_SIZE;
            drawEntity(g2, entity, px, py);
        }

        // Draw player
        drawPlayer(g2, playerX * TILE_SIZE, playerY * TILE_SIZE);

        // Highlight tile under cursor
        if (highlightX >= 0 && highlightX < TileMap.WIDTH && highlightY >= 0 && highlightY < TileMap.HEIGHT) {
            g2.setColor(COLOR_HIGHLIGHT);
            g2.fillRect(highlightX * TILE_SIZE, highlightY * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            g2.setColor(new Color(255, 255, 255, 120));
            g2.drawRect(highlightX * TILE_SIZE, highlightY * TILE_SIZE, TILE_SIZE - 1, TILE_SIZE - 1);

            // Tooltip for entities
            MapEntity hover = tileMap.getEntityAt(highlightX, highlightY);
            if (hover != null) {
                drawTooltip(g2, hover.getName(), highlightX * TILE_SIZE, highlightY * TILE_SIZE - 10);
            }
        }

        // Mini legend
        g2.setFont(new Font("Monospaced", Font.PLAIN, 10));
        g2.setColor(new Color(255, 255, 255, 180));
        g2.drawString("WASD/Arrows=Move  E/Space=Interact", 8, TileMap.HEIGHT * TILE_SIZE - 6);
    }

    private void drawTile(Graphics2D g, int tile, int px, int py, int tx, int ty) {
        switch (tile) {
            case TileMap.TILE_GRASS:
                g.setColor((tx + ty) % 2 == 0 ? COLOR_GRASS : COLOR_GRASS_ALT);
                g.fillRect(px, py, TILE_SIZE, TILE_SIZE);
                break;
            case TileMap.TILE_STONE:
                g.setColor(COLOR_STONE);
                g.fillRect(px, py, TILE_SIZE, TILE_SIZE);
                g.setColor(COLOR_STONE.darker());
                g.fillRect(px + 4, py + 4, 10, 8);
                g.fillRect(px + 18, py + 14, 12, 10);
                break;
            case TileMap.TILE_WATER:
                g.setColor((tx + ty) % 2 == 0 ? COLOR_WATER : COLOR_WATER_LIGHT);
                g.fillRect(px, py, TILE_SIZE, TILE_SIZE);
                g.setColor(new Color(100, 170, 220, 60));
                g.fillRect(px + 4, py + 10, 20, 3);
                break;
            case TileMap.TILE_TREE:
                g.setColor(COLOR_GRASS);
                g.fillRect(px, py, TILE_SIZE, TILE_SIZE);
                g.setColor(COLOR_TREE_TRUNK);
                g.fillRect(px + 13, py + 18, 6, 14);
                g.setColor(COLOR_TREE_LEAVES);
                g.fillOval(px + 4, py + 2, 24, 20);
                g.setColor(COLOR_TREE_LEAVES.brighter());
                g.fillOval(px + 8, py + 4, 16, 12);
                break;
            case TileMap.TILE_PATH:
                g.setColor(COLOR_PATH);
                g.fillRect(px, py, TILE_SIZE, TILE_SIZE);
                g.setColor(COLOR_PATH.darker());
                g.fillOval(px + 6, py + 12, 4, 4);
                g.fillOval(px + 20, py + 6, 5, 5);
                break;
            case TileMap.TILE_WALL:
                g.setColor(COLOR_WALL);
                g.fillRect(px, py, TILE_SIZE, TILE_SIZE);
                g.setColor(COLOR_WALL.brighter());
                g.drawRect(px + 2, py + 2, TILE_SIZE - 5, TILE_SIZE - 5);
                g.drawLine(px, py + TILE_SIZE / 2, px + TILE_SIZE, py + TILE_SIZE / 2);
                break;
            case TileMap.TILE_DOOR:
                g.setColor(COLOR_DOOR);
                g.fillRect(px, py, TILE_SIZE, TILE_SIZE);
                g.setColor(COLOR_DOOR.darker());
                g.fillRect(px + 4, py + 2, TILE_SIZE - 8, TILE_SIZE - 4);
                g.setColor(new Color(200, 180, 80));
                g.fillOval(px + TILE_SIZE / 2 + 4, py + TILE_SIZE / 2 - 2, 5, 5);
                break;
        }
    }

    private void drawEntity(Graphics2D g, MapEntity entity, int px, int py) {
        // Draw the grass underneath first
        g.setColor(COLOR_GRASS);
        g.fillRect(px, py, TILE_SIZE, TILE_SIZE);

        switch (entity.getType()) {
            case MapEntity.TYPE_RESOURCE:
                // Glowing ore/plant node
                g.setColor(COLOR_RESOURCE);
                g.fillOval(px + 6, py + 6, 20, 20);
                g.setColor(COLOR_RESOURCE.brighter());
                g.fillOval(px + 10, py + 10, 12, 12);
                g.setColor(new Color(255, 255, 200, 60));
                g.fillOval(px + 2, py + 2, 28, 28);
                break;
            case MapEntity.TYPE_MONSTER:
                // Red enemy with horns
                g.setColor(COLOR_MONSTER);
                g.fillOval(px + 6, py + 8, 20, 20);
                g.setColor(COLOR_MONSTER.darker());
                g.fillOval(px + 10, py + 12, 12, 12);
                // Eyes
                g.setColor(Color.YELLOW);
                g.fillOval(px + 11, py + 14, 4, 4);
                g.fillOval(px + 19, py + 14, 4, 4);
                // Horns
                g.setColor(COLOR_MONSTER.brighter());
                g.fillRect(px + 8, py + 4, 3, 8);
                g.fillRect(px + 21, py + 4, 3, 8);
                break;
            case MapEntity.TYPE_NPC:
                // Green friendly NPC
                g.setColor(COLOR_NPC);
                g.fillOval(px + 8, py + 4, 16, 16);
                g.setColor(COLOR_NPC.darker());
                g.fillRect(px + 10, py + 18, 12, 12);
                // Face
                g.setColor(Color.WHITE);
                g.fillOval(px + 12, py + 9, 3, 3);
                g.fillOval(px + 19, py + 9, 3, 3);
                break;
        }
    }

    private void drawPlayer(Graphics2D g, int px, int py) {
        // Shadow
        g.setColor(new Color(0, 0, 0, 50));
        g.fillOval(px + 4, py + 22, 24, 10);

        // Body
        g.setColor(COLOR_PLAYER);
        g.fillOval(px + 6, py + 2, 20, 18);

        // Armor body
        g.setColor(COLOR_PLAYER.darker());
        g.fillRect(px + 8, py + 16, 16, 14);

        // Shield on left
        g.setColor(new Color(160, 140, 100));
        g.fillOval(px + 2, py + 14, 10, 14);

        // Sword on right
        g.setColor(new Color(200, 200, 210));
        g.fillRect(px + 24, py + 10, 3, 18);
        g.setColor(new Color(140, 100, 50));
        g.fillRect(px + 22, py + 16, 7, 3);

        // Helmet visor
        g.setColor(COLOR_PLAYER_OUTLINE);
        g.drawOval(px + 6, py + 2, 20, 18);
        g.setColor(new Color(200, 220, 255));
        g.fillRect(px + 11, py + 8, 10, 4);

        // Outline
        g.setColor(COLOR_PLAYER_OUTLINE);
        g.drawOval(px + 6, py + 2, 20, 18);
    }

    private void drawTooltip(Graphics2D g, String text, int x, int y) {
        FontMetrics fm = g.getFontMetrics();
        int tw = fm.stringWidth(text) + 10;
        int th = fm.getHeight() + 6;
        int tx = Math.min(x, getWidth() - tw - 4);
        int ty = Math.max(y - th, 4);

        g.setColor(new Color(20, 20, 30, 220));
        g.fillRoundRect(tx, ty, tw, th, 6, 6);
        g.setColor(new Color(200, 200, 200));
        g.drawRoundRect(tx, ty, tw, th, 6, 6);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.drawString(text, tx + 5, ty + th - 6);
    }
}
