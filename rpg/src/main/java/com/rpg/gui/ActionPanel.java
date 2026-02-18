package com.rpg.gui;

import com.rpg.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ActionPanel extends JPanel {
    private final JTextArea logArea;
    private final JPanel buttonPanel;
    private GameController controller;

    private static final Color BG = new Color(25, 25, 35);
    private static final Color LOG_BG = new Color(15, 15, 22);
    private static final Color TEXT = new Color(190, 190, 190);
    private static final Color ACCENT = new Color(80, 160, 220);

    public ActionPanel() {
        setPreferredSize(new Dimension(0, 160));
        setBackground(BG);
        setLayout(new BorderLayout(6, 6));
        setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        // Log area
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setBackground(LOG_BG);
        logArea.setForeground(TEXT);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));

        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 60)));
        add(logScroll, BorderLayout.CENTER);

        // Button panel on the right
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(BG);
        buttonPanel.setPreferredSize(new Dimension(180, 0));

        JScrollPane btnScroll = new JScrollPane(buttonPanel);
        btnScroll.setBorder(BorderFactory.createEmptyBorder());
        btnScroll.getViewport().setBackground(BG);
        btnScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(btnScroll, BorderLayout.EAST);
    }

    public void setController(GameController controller) {
        this.controller = controller;
    }

    public void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    public void showActions(List<SkillAction> actions) {
        buttonPanel.removeAll();

        JLabel title = new JLabel("ACTIONS");
        title.setForeground(ACCENT);
        title.setFont(new Font("SansSerif", Font.BOLD, 11));
        title.setAlignmentX(LEFT_ALIGNMENT);
        buttonPanel.add(title);
        buttonPanel.add(Box.createVerticalStrut(4));

        for (SkillAction action : actions) {
            JButton btn = createButton(action.getName());
            btn.setToolTipText(action.getSkillType() + " +" + action.getExperienceReward() + " XP");
            btn.addActionListener(e -> {
                if (controller != null) {
                    controller.performAction(action);
                }
            });
            buttonPanel.add(btn);
            buttonPanel.add(Box.createVerticalStrut(2));
        }

        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    public void showCombatActions(MapEntity monster) {
        buttonPanel.removeAll();

        JLabel title = new JLabel("COMBAT: " + monster.getName());
        title.setForeground(new Color(220, 60, 60));
        title.setFont(new Font("SansSerif", Font.BOLD, 11));
        title.setAlignmentX(LEFT_ALIGNMENT);
        buttonPanel.add(title);
        buttonPanel.add(Box.createVerticalStrut(4));

        for (CombatDirection dir : CombatDirection.values()) {
            JButton atkBtn = createButton("Strike " + dir);
            atkBtn.addActionListener(e -> {
                if (controller != null) controller.performMeleeAttack(dir, monster);
            });
            buttonPanel.add(atkBtn);
            buttonPanel.add(Box.createVerticalStrut(1));
        }

        buttonPanel.add(Box.createVerticalStrut(6));
        for (CombatDirection dir : CombatDirection.values()) {
            JButton magBtn = createButton("Bolt " + dir);
            magBtn.setForeground(new Color(140, 120, 220));
            magBtn.addActionListener(e -> {
                if (controller != null) controller.performMagicAttack(dir, monster);
            });
            buttonPanel.add(magBtn);
            buttonPanel.add(Box.createVerticalStrut(1));
        }

        buttonPanel.add(Box.createVerticalStrut(6));
        JLabel feintTitle = new JLabel("FEINT");
        feintTitle.setForeground(new Color(200, 160, 60));
        feintTitle.setFont(new Font("SansSerif", Font.BOLD, 10));
        feintTitle.setAlignmentX(LEFT_ALIGNMENT);
        buttonPanel.add(feintTitle);
        buttonPanel.add(Box.createVerticalStrut(2));
        for (CombatDirection fakeDir : CombatDirection.values()) {
            for (CombatDirection realDir : CombatDirection.values()) {
                if (fakeDir == realDir) continue;
                JButton feintBtn = createButton("Feint " + shortDir(fakeDir) + "\u2192" + shortDir(realDir));
                feintBtn.setForeground(new Color(200, 160, 60));
                feintBtn.setFont(new Font("SansSerif", Font.PLAIN, 10));
                feintBtn.addActionListener(e -> {
                    if (controller != null) controller.performFeint(fakeDir, realDir, monster);
                });
                buttonPanel.add(feintBtn);
                buttonPanel.add(Box.createVerticalStrut(1));
            }
        }

        buttonPanel.add(Box.createVerticalStrut(6));
        JButton flee = createButton("Retreat");
        flee.setForeground(new Color(200, 180, 80));
        flee.addActionListener(e -> {
            if (controller != null) controller.endCombat();
        });
        buttonPanel.add(flee);

        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    private String shortDir(CombatDirection dir) {
        switch (dir) {
            case NORTH: return "N";
            case SOUTH: return "S";
            case EAST: return "E";
            case WEST: return "W";
            default: return dir.name();
        }
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(170, 26));
        btn.setPreferredSize(new Dimension(170, 26));
        btn.setBackground(new Color(50, 50, 65));
        btn.setForeground(TEXT);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
