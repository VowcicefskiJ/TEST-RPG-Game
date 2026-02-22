package com.rpg.gui;

import com.rpg.Inventory;
import com.rpg.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Visual drag-and-drop inventory grid.
 * Toggle with 'I' key. Items can be dragged between slots to rearrange,
 * and right-clicked to drop.
 */
public class InventoryPanel extends JPanel {

    // Grid layout
    private static final int COLS = 6;
    private static final int ROWS = 5;
    private static final int SLOT_SIZE = 52;
    private static final int SLOT_PAD = 4;
    private static final int GRID_PAD = 12;
    private static final int HEADER_H = 32;

    // Dimensions
    private static final int PANEL_W = GRID_PAD * 2 + COLS * (SLOT_SIZE + SLOT_PAD) - SLOT_PAD;
    private static final int PANEL_H = GRID_PAD + HEADER_H + ROWS * (SLOT_SIZE + SLOT_PAD) - SLOT_PAD + GRID_PAD;

    // Colors
    private static final Color BG = new Color(18, 18, 28, 240);
    private static final Color SLOT_BG = new Color(35, 35, 50);
    private static final Color SLOT_HOVER = new Color(50, 50, 70);
    private static final Color SLOT_DRAG_SRC = new Color(60, 40, 40);
    private static final Color SLOT_DRAG_DEST = new Color(40, 60, 40);
    private static final Color SLOT_BORDER = new Color(60, 60, 80);
    private static final Color SLOT_EMPTY = new Color(28, 28, 40);
    private static final Color TEXT_WHITE = new Color(220, 220, 220);
    private static final Color TEXT_GOLD = new Color(220, 200, 120);
    private static final Color TEXT_GRAY = new Color(140, 140, 150);
    private static final Color QTY_BG = new Color(30, 30, 45, 200);
    private static final Color TITLE_COLOR = new Color(180, 160, 100);
    private static final Color TOOLTIP_BG = new Color(15, 15, 25, 230);

    // Item type categories for icons
    private static final String[] FISH_KEYS = {"fish", "carp", "trout"};
    private static final String[] ORE_KEYS = {"ore", "shard", "iron", "copper", "nugget", "ingot", "scrap"};
    private static final String[] HERB_KEYS = {"resin", "thistle", "herb", "nightshade"};
    private static final String[] FOOD_KEYS = {"stew", "grilled", "root", "crop", "meal"};
    private static final String[] WEAPON_KEYS = {"blade", "sword", "gauntlet"};
    private static final String[] ARMOR_KEYS = {"chain", "mail", "armor", "shield"};
    private static final String[] POTION_KEYS = {"salve", "tonic", "potion", "flask"};
    private static final String[] TOOL_KEYS = {"pick", "hammer", "ladle", "hook", "trowel", "shears"};
    private static final String[] MAGIC_KEYS = {"staff", "orb", "crystal", "focus", "arcane", "rune", "scroll"};
    private static final String[] BOOK_KEYS = {"book", "recipe", "journal", "survey", "fossil"};

    // Slot model
    private final List<Slot> slots = new ArrayList<>();
    private Player player;

    // Drag state
    private int dragSourceIndex = -1;
    private int hoverIndex = -1;
    private int mouseX, mouseY;
    private boolean dragging = false;

    // Tooltip
    private int tooltipIndex = -1;

    public InventoryPanel() {
        setPreferredSize(new Dimension(PANEL_W, PANEL_H));
        setOpaque(false);

        // Init empty slots
        for (int i = 0; i < COLS * ROWS; i++) {
            slots.add(new Slot(null, 0));
        }

        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int idx = slotAt(e.getX(), e.getY());
                if (idx < 0 || idx >= slots.size()) return;

                if (SwingUtilities.isRightMouseButton(e)) {
                    // Right-click: drop 1 item
                    Slot s = slots.get(idx);
                    if (s.item != null && player != null) {
                        player.getInventory().removeItem(s.item, 1);
                        syncFromInventory();
                        repaint();
                    }
                    return;
                }

                if (SwingUtilities.isLeftMouseButton(e)) {
                    Slot s = slots.get(idx);
                    if (s.item != null) {
                        dragSourceIndex = idx;
                        dragging = true;
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!dragging) return;
                int destIdx = slotAt(e.getX(), e.getY());
                if (destIdx >= 0 && destIdx < slots.size() && destIdx != dragSourceIndex) {
                    // Swap slots
                    Slot src = slots.get(dragSourceIndex);
                    Slot dst = slots.get(destIdx);
                    slots.set(dragSourceIndex, dst);
                    slots.set(destIdx, src);
                }
                dragging = false;
                dragSourceIndex = -1;
                repaint();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
                int idx = slotAt(mouseX, mouseY);
                if (idx != hoverIndex) {
                    hoverIndex = idx;
                    tooltipIndex = idx;
                    repaint();
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
                hoverIndex = slotAt(mouseX, mouseY);
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hoverIndex = -1;
                tooltipIndex = -1;
                repaint();
            }
        };
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    public void setPlayer(Player player) {
        this.player = player;
        syncFromInventory();
    }

    /** Refresh slot list from the player's inventory. Preserves slot order for existing items. */
    public void syncFromInventory() {
        if (player == null) return;
        Inventory inv = player.getInventory();
        Map<String, Integer> items = inv.getAllItems();

        // Update quantities for existing slot items; remove gone items
        for (int i = 0; i < slots.size(); i++) {
            Slot s = slots.get(i);
            if (s.item != null) {
                int qty = items.getOrDefault(s.item, 0);
                if (qty <= 0) {
                    slots.set(i, new Slot(null, 0));
                } else {
                    s.quantity = qty;
                }
            }
        }

        // Add new items not yet in a slot
        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            boolean found = false;
            for (Slot s : slots) {
                if (entry.getKey().equals(s.item)) { found = true; break; }
            }
            if (!found) {
                for (int i = 0; i < slots.size(); i++) {
                    if (slots.get(i).item == null) {
                        slots.set(i, new Slot(entry.getKey(), entry.getValue()));
                        break;
                    }
                }
            }
        }

        repaint();
    }

    // ==================== SLOT POSITIONING ====================

    private int slotX(int index) {
        return GRID_PAD + (index % COLS) * (SLOT_SIZE + SLOT_PAD);
    }

    private int slotY(int index) {
        return GRID_PAD + HEADER_H + (index / COLS) * (SLOT_SIZE + SLOT_PAD);
    }

    private int slotAt(int mx, int my) {
        for (int i = 0; i < slots.size(); i++) {
            int sx = slotX(i), sy = slotY(i);
            if (mx >= sx && mx < sx + SLOT_SIZE && my >= sy && my < sy + SLOT_SIZE) {
                return i;
            }
        }
        return -1;
    }

    // ==================== RENDERING ====================

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Panel background
        g2.setColor(BG);
        g2.fillRoundRect(0, 0, PANEL_W, PANEL_H, 12, 12);
        g2.setColor(new Color(80, 70, 50));
        g2.drawRoundRect(0, 0, PANEL_W - 1, PANEL_H - 1, 12, 12);

        // Title
        g2.setFont(new Font("Serif", Font.BOLD, 16));
        g2.setColor(TITLE_COLOR);
        g2.drawString("INVENTORY", GRID_PAD, GRID_PAD + 18);

        // Slot count
        long filled = slots.stream().filter(s -> s.item != null).count();
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g2.setColor(TEXT_GRAY);
        String countStr = filled + "/" + (COLS * ROWS);
        int cw = g2.getFontMetrics().stringWidth(countStr);
        g2.drawString(countStr, PANEL_W - GRID_PAD - cw, GRID_PAD + 18);

        // Hint
        g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
        g2.setColor(TEXT_GRAY);
        g2.drawString("Drag to rearrange  |  Right-click to drop  |  I to close", GRID_PAD, PANEL_H - 4);

        // Draw slots
        for (int i = 0; i < slots.size(); i++) {
            drawSlot(g2, i);
        }

        // Draw dragged item following cursor
        if (dragging && dragSourceIndex >= 0) {
            Slot src = slots.get(dragSourceIndex);
            if (src.item != null) {
                int dw = SLOT_SIZE - 8;
                int dx = mouseX - dw / 2;
                int dy = mouseY - dw / 2;
                Composite old = g2.getComposite();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                drawItemIcon(g2, src.item, dx, dy, dw);
                g2.setComposite(old);
            }
        }

        // Tooltip
        if (tooltipIndex >= 0 && tooltipIndex < slots.size() && !dragging) {
            Slot s = slots.get(tooltipIndex);
            if (s.item != null) {
                drawTooltip(g2, s.item, s.quantity, mouseX, mouseY);
            }
        }
    }

    private void drawSlot(Graphics2D g, int idx) {
        int sx = slotX(idx), sy = slotY(idx);
        Slot s = slots.get(idx);

        // Slot background
        Color bg;
        if (dragging && idx == dragSourceIndex) {
            bg = SLOT_DRAG_SRC;
        } else if (dragging && idx == hoverIndex) {
            bg = SLOT_DRAG_DEST;
        } else if (idx == hoverIndex && s.item != null) {
            bg = SLOT_HOVER;
        } else if (s.item != null) {
            bg = SLOT_BG;
        } else {
            bg = SLOT_EMPTY;
        }

        g.setColor(bg);
        g.fillRoundRect(sx, sy, SLOT_SIZE, SLOT_SIZE, 6, 6);
        g.setColor(SLOT_BORDER);
        g.drawRoundRect(sx, sy, SLOT_SIZE, SLOT_SIZE, 6, 6);

        if (s.item == null) return;

        // Item icon
        int iconPad = 6;
        int iconSize = SLOT_SIZE - iconPad * 2;
        drawItemIcon(g, s.item, sx + iconPad, sy + iconPad, iconSize);

        // Quantity badge
        if (s.quantity > 1) {
            String qStr = String.valueOf(s.quantity);
            g.setFont(new Font("SansSerif", Font.BOLD, 10));
            FontMetrics fm = g.getFontMetrics();
            int tw = fm.stringWidth(qStr) + 6;
            int th = fm.getHeight();
            int bx = sx + SLOT_SIZE - tw - 2;
            int by = sy + SLOT_SIZE - th - 1;
            g.setColor(QTY_BG);
            g.fillRoundRect(bx, by, tw, th, 4, 4);
            g.setColor(TEXT_GOLD);
            g.drawString(qStr, bx + 3, by + th - 3);
        }
    }

    // ==================== ITEM ICONS (drawn from name) ====================

    private void drawItemIcon(Graphics2D g, String item, int x, int y, int size) {
        String lower = item.toLowerCase();

        if (matchesAny(lower, FISH_KEYS)) {
            drawFishIcon(g, x, y, size);
        } else if (matchesAny(lower, ORE_KEYS)) {
            drawOreIcon(g, x, y, size);
        } else if (matchesAny(lower, HERB_KEYS)) {
            drawHerbIcon(g, x, y, size);
        } else if (matchesAny(lower, FOOD_KEYS)) {
            drawFoodIcon(g, x, y, size);
        } else if (matchesAny(lower, WEAPON_KEYS)) {
            drawWeaponIcon(g, x, y, size);
        } else if (matchesAny(lower, ARMOR_KEYS)) {
            drawArmorIcon(g, x, y, size);
        } else if (matchesAny(lower, POTION_KEYS)) {
            drawPotionIcon(g, x, y, size);
        } else if (matchesAny(lower, TOOL_KEYS)) {
            drawToolIcon(g, x, y, size);
        } else if (matchesAny(lower, MAGIC_KEYS)) {
            drawMagicIcon(g, x, y, size);
        } else if (matchesAny(lower, BOOK_KEYS)) {
            drawBookIcon(g, x, y, size);
        } else {
            drawGenericIcon(g, x, y, size, item);
        }
    }

    private void drawFishIcon(Graphics2D g, int x, int y, int s) {
        g.setColor(new Color(140, 170, 200));
        g.fillOval(x + s / 6, y + s / 4, s * 2 / 3, s / 2);
        // Tail
        int[] tx = {x + s / 6, x + 2, x + s / 6};
        int[] ty = {y + s / 3, y + s / 2, y + s * 2 / 3};
        g.fillPolygon(tx, ty, 3);
        // Eye
        g.setColor(new Color(30, 30, 40));
        g.fillOval(x + s * 5 / 8, y + s * 3 / 8, s / 8, s / 8);
    }

    private void drawOreIcon(Graphics2D g, int x, int y, int s) {
        g.setColor(new Color(130, 120, 110));
        g.fillRoundRect(x + s / 8, y + s / 4, s * 3 / 4, s / 2, 4, 4);
        g.setColor(new Color(100, 95, 85));
        g.fillRoundRect(x + s / 4, y + s / 6, s / 2, s / 3, 3, 3);
        // Glint
        g.setColor(new Color(200, 170, 60));
        g.fillOval(x + s / 3, y + s / 3, s / 6, s / 6);
        g.fillOval(x + s / 2, y + s / 4, s / 8, s / 8);
    }

    private void drawHerbIcon(Graphics2D g, int x, int y, int s) {
        g.setColor(new Color(50, 100, 40));
        g.fillRect(x + s * 2 / 5, y + s / 2, s / 5, s / 2);
        g.setColor(new Color(60, 140, 50));
        g.fillOval(x + s / 6, y + s / 8, s / 3, s / 3);
        g.fillOval(x + s / 2, y + s / 6, s / 3, s / 3);
        g.setColor(new Color(80, 170, 65));
        g.fillOval(x + s / 3, y, s / 3, s / 3);
        // Berry
        g.setColor(new Color(180, 50, 60));
        g.fillOval(x + s * 3 / 8, y + s / 8, s / 6, s / 6);
    }

    private void drawFoodIcon(Graphics2D g, int x, int y, int s) {
        // Bowl
        g.setColor(new Color(160, 130, 90));
        g.fillArc(x + s / 8, y + s / 3, s * 3 / 4, s / 2, 0, -180);
        g.setColor(new Color(190, 150, 100));
        g.fillRect(x + s / 8, y + s / 3, s * 3 / 4, s / 8);
        // Steam
        g.setColor(new Color(200, 200, 200, 120));
        g.drawLine(x + s / 3, y + s / 4, x + s / 4, y + s / 10);
        g.drawLine(x + s / 2, y + s / 4, x + s / 2, y + s / 10);
        g.drawLine(x + s * 2 / 3, y + s / 4, x + s * 3 / 4, y + s / 10);
    }

    private void drawWeaponIcon(Graphics2D g, int x, int y, int s) {
        // Blade
        g.setColor(new Color(190, 195, 210));
        g.fillRect(x + s * 2 / 5, y + s / 10, s / 5, s * 3 / 5);
        g.setColor(new Color(210, 215, 225));
        g.drawLine(x + s / 2, y + s / 10, x + s / 2, y + s * 7 / 10);
        // Guard
        g.setColor(new Color(160, 120, 50));
        g.fillRect(x + s / 5, y + s * 6 / 10, s * 3 / 5, s / 8);
        // Grip
        g.setColor(new Color(100, 70, 40));
        g.fillRect(x + s * 2 / 5, y + s * 7 / 10, s / 5, s / 4);
    }

    private void drawArmorIcon(Graphics2D g, int x, int y, int s) {
        g.setColor(new Color(140, 140, 155));
        g.fillRoundRect(x + s / 5, y + s / 5, s * 3 / 5, s * 3 / 5, 6, 6);
        g.setColor(new Color(120, 120, 135));
        g.fillRoundRect(x + s / 4, y + s / 4, s / 2, s / 2, 4, 4);
        // Rivets
        g.setColor(new Color(180, 180, 190));
        g.fillOval(x + s / 3, y + s / 3, s / 8, s / 8);
        g.fillOval(x + s / 2, y + s / 3, s / 8, s / 8);
        g.fillOval(x + s / 3, y + s / 2, s / 8, s / 8);
        g.fillOval(x + s / 2, y + s / 2, s / 8, s / 8);
    }

    private void drawPotionIcon(Graphics2D g, int x, int y, int s) {
        // Bottle
        g.setColor(new Color(100, 180, 100));
        g.fillRoundRect(x + s / 4, y + s / 3, s / 2, s / 2, 6, 6);
        g.setColor(new Color(80, 160, 80));
        g.fillRoundRect(x + s / 3, y + s / 4, s / 3, s / 6, 3, 3);
        // Stopper
        g.setColor(new Color(140, 100, 50));
        g.fillRect(x + s * 3 / 8, y + s / 6, s / 4, s / 8);
        // Liquid shimmer
        g.setColor(new Color(150, 220, 150, 120));
        g.fillOval(x + s / 3, y + s * 2 / 5, s / 5, s / 5);
    }

    private void drawToolIcon(Graphics2D g, int x, int y, int s) {
        // Handle
        g.setColor(new Color(120, 85, 45));
        g.fillRect(x + s * 2 / 5, y + s / 3, s / 5, s * 2 / 3);
        // Head
        g.setColor(new Color(160, 160, 170));
        g.fillRoundRect(x + s / 6, y + s / 8, s * 2 / 3, s / 3, 4, 4);
    }

    private void drawMagicIcon(Graphics2D g, int x, int y, int s) {
        // Staff/orb glow
        g.setColor(new Color(120, 60, 200, 80));
        g.fillOval(x + s / 6, y + s / 6, s * 2 / 3, s * 2 / 3);
        g.setColor(new Color(160, 100, 240));
        g.fillOval(x + s / 4, y + s / 4, s / 2, s / 2);
        g.setColor(new Color(200, 160, 255));
        g.fillOval(x + s / 3, y + s / 3, s / 3, s / 3);
        // Sparkle
        g.setColor(new Color(255, 255, 255, 180));
        g.fillOval(x + s * 2 / 5, y + s / 3, s / 8, s / 8);
    }

    private void drawBookIcon(Graphics2D g, int x, int y, int s) {
        g.setColor(new Color(140, 100, 60));
        g.fillRoundRect(x + s / 6, y + s / 6, s * 2 / 3, s * 2 / 3, 3, 3);
        g.setColor(new Color(200, 185, 150));
        g.fillRect(x + s / 4, y + s / 4, s / 2, s / 2);
        // Page lines
        g.setColor(new Color(120, 110, 90));
        for (int i = 0; i < 4; i++) {
            int ly = y + s / 3 + i * s / 10;
            g.drawLine(x + s / 3, ly, x + s * 2 / 3, ly);
        }
    }

    private void drawGenericIcon(Graphics2D g, int x, int y, int s, String item) {
        g.setColor(new Color(120, 110, 100));
        g.fillRoundRect(x + s / 5, y + s / 5, s * 3 / 5, s * 3 / 5, 6, 6);
        g.setColor(new Color(180, 170, 155));
        g.setFont(new Font("SansSerif", Font.BOLD, s / 3));
        FontMetrics fm = g.getFontMetrics();
        String ch = item.substring(0, 1).toUpperCase();
        int tw = fm.stringWidth(ch);
        g.drawString(ch, x + (s - tw) / 2, y + s / 2 + fm.getAscent() / 3);
    }

    // ==================== TOOLTIP ====================

    private void drawTooltip(Graphics2D g, String item, int qty, int mx, int my) {
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        FontMetrics fmBold = g.getFontMetrics();
        int nameW = fmBold.stringWidth(item);

        g.setFont(new Font("SansSerif", Font.PLAIN, 10));
        FontMetrics fmSmall = g.getFontMetrics();
        String qtyLine = "Quantity: " + qty;
        String catLine = "Category: " + getCategory(item);
        int qtyW = fmSmall.stringWidth(qtyLine);
        int catW = fmSmall.stringWidth(catLine);
        String hintLine = "Right-click to drop";
        int hintW = fmSmall.stringWidth(hintLine);

        int tw = Math.max(Math.max(nameW, Math.max(qtyW, catW)), hintW) + 16;
        int th = fmBold.getHeight() + fmSmall.getHeight() * 3 + 12;

        // Position tooltip above cursor, clamp to panel
        int tx = mx + 12;
        int ty = my - th - 4;
        if (tx + tw > PANEL_W) tx = mx - tw - 4;
        if (ty < 0) ty = my + 16;

        g.setColor(TOOLTIP_BG);
        g.fillRoundRect(tx, ty, tw, th, 6, 6);
        g.setColor(new Color(100, 90, 60));
        g.drawRoundRect(tx, ty, tw, th, 6, 6);

        int textX = tx + 8;
        int textY = ty + fmBold.getAscent() + 4;

        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.setColor(TEXT_GOLD);
        g.drawString(item, textX, textY);

        g.setFont(new Font("SansSerif", Font.PLAIN, 10));
        textY += fmSmall.getHeight() + 2;
        g.setColor(TEXT_WHITE);
        g.drawString(qtyLine, textX, textY);

        textY += fmSmall.getHeight();
        g.setColor(TEXT_GRAY);
        g.drawString(catLine, textX, textY);

        textY += fmSmall.getHeight();
        g.setColor(new Color(100, 100, 120));
        g.drawString(hintLine, textX, textY);
    }

    private String getCategory(String item) {
        String lower = item.toLowerCase();
        if (matchesAny(lower, FISH_KEYS)) return "Fish";
        if (matchesAny(lower, ORE_KEYS)) return "Ore / Metal";
        if (matchesAny(lower, HERB_KEYS)) return "Herb";
        if (matchesAny(lower, FOOD_KEYS)) return "Food";
        if (matchesAny(lower, WEAPON_KEYS)) return "Weapon";
        if (matchesAny(lower, ARMOR_KEYS)) return "Armor";
        if (matchesAny(lower, POTION_KEYS)) return "Potion";
        if (matchesAny(lower, TOOL_KEYS)) return "Tool";
        if (matchesAny(lower, MAGIC_KEYS)) return "Magic";
        if (matchesAny(lower, BOOK_KEYS)) return "Knowledge";
        return "Misc";
    }

    private boolean matchesAny(String lower, String[] keys) {
        for (String k : keys) {
            if (lower.contains(k)) return true;
        }
        return false;
    }

    // ==================== SLOT MODEL ====================

    private static class Slot {
        String item;
        int quantity;

        Slot(String item, int quantity) {
            this.item = item;
            this.quantity = quantity;
        }
    }
}
