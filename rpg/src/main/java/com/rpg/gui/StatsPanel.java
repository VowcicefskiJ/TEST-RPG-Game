package com.rpg.gui;

import com.rpg.Player;
import com.rpg.Skill;
import com.rpg.SkillType;

import javax.swing.*;
import java.awt.*;

public class StatsPanel extends JPanel {
    private Player player;
    private final JLabel nameLabel;
    private final JLabel healthLabel;
    private final JLabel shieldLabel;
    private final JLabel staminaLabel;
    private final JPanel skillsPanel;

    private static final Color BG = new Color(25, 25, 35);
    private static final Color BG_INNER = new Color(35, 35, 48);
    private static final Color TEXT = new Color(210, 210, 210);
    private static final Color ACCENT = new Color(80, 160, 220);
    private static final Color HEALTH_COLOR = new Color(180, 50, 50);
    private static final Color SHIELD_COLOR = new Color(60, 130, 180);
    private static final Color STAMINA_COLOR = new Color(200, 180, 60);
    private static final Color XP_BAR = new Color(80, 170, 80);
    private static final Color XP_BG = new Color(45, 45, 55);

    public StatsPanel() {
        setPreferredSize(new Dimension(220, 0));
        setBackground(BG);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("CHARACTER");
        title.setForeground(ACCENT);
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        title.setAlignmentX(LEFT_ALIGNMENT);
        add(title);
        add(Box.createVerticalStrut(8));

        nameLabel = createLabel("Adventurer");
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        add(nameLabel);
        add(Box.createVerticalStrut(4));

        healthLabel = createLabel("HP: 120/120");
        healthLabel.setForeground(HEALTH_COLOR);
        add(healthLabel);

        shieldLabel = createLabel("Shield: 80/80");
        shieldLabel.setForeground(SHIELD_COLOR);
        add(shieldLabel);

        staminaLabel = createLabel("Stamina: 100/100");
        staminaLabel.setForeground(STAMINA_COLOR);
        add(staminaLabel);

        add(Box.createVerticalStrut(12));

        JLabel skillsTitle = new JLabel("SKILLS");
        skillsTitle.setForeground(ACCENT);
        skillsTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        skillsTitle.setAlignmentX(LEFT_ALIGNMENT);
        add(skillsTitle);
        add(Box.createVerticalStrut(4));

        skillsPanel = new JPanel();
        skillsPanel.setLayout(new BoxLayout(skillsPanel, BoxLayout.Y_AXIS));
        skillsPanel.setBackground(BG);
        skillsPanel.setAlignmentX(LEFT_ALIGNMENT);

        JScrollPane scroll = new JScrollPane(skillsPanel);
        scroll.setAlignmentX(LEFT_ALIGNMENT);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(BG);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroll);
    }

    public void setPlayer(Player player) {
        this.player = player;
        refresh();
    }

    public void refresh() {
        if (player == null) return;

        nameLabel.setText(player.getName());
        healthLabel.setText("HP: " + player.getHealth() + "/" + player.getMaxHealth());
        shieldLabel.setText("Shield: " + player.getShieldDurability() + "/80");
        staminaLabel.setText("Stamina: " + player.getStamina() + "/" + player.getMaxStamina());

        skillsPanel.removeAll();
        for (SkillType type : SkillType.values()) {
            Skill skill = player.getSkill(type);
            skillsPanel.add(createSkillBar(type.name(), skill.getLevel(), skill.getExperience(),
                    skill.experienceForNextLevel()));
            skillsPanel.add(Box.createVerticalStrut(2));
        }
        skillsPanel.revalidate();
        skillsPanel.repaint();
    }

    private JPanel createSkillBar(String name, int level, int xp, int xpNeeded) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                int w = getWidth();
                int h = getHeight();

                // Background
                g2.setColor(BG_INNER);
                g2.fillRoundRect(0, 0, w, h, 4, 4);

                // XP bar
                double pct = xpNeeded > 0 ? (double) xp / xpNeeded : 0;
                g2.setColor(XP_BG);
                g2.fillRoundRect(2, h - 6, w - 4, 4, 2, 2);
                g2.setColor(XP_BAR);
                g2.fillRoundRect(2, h - 6, (int) ((w - 4) * pct), 4, 2, 2);

                // Text
                g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                g2.setColor(TEXT);
                String display = formatSkillName(name);
                g2.drawString(display, 4, 12);
                String lvl = "Lv " + level;
                int lw = g2.getFontMetrics().stringWidth(lvl);
                g2.setColor(ACCENT);
                g2.drawString(lvl, w - lw - 4, 12);
            }
        };
        panel.setPreferredSize(new Dimension(200, 22));
        panel.setMaximumSize(new Dimension(200, 22));
        panel.setAlignmentX(LEFT_ALIGNMENT);
        return panel;
    }

    private String formatSkillName(String raw) {
        String lower = raw.toLowerCase().replace('_', ' ');
        StringBuilder sb = new StringBuilder();
        boolean cap = true;
        for (char c : lower.toCharArray()) {
            sb.append(cap ? Character.toUpperCase(c) : c);
            cap = (c == ' ');
        }
        return sb.toString();
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        label.setAlignmentX(LEFT_ALIGNMENT);
        return label;
    }
}
