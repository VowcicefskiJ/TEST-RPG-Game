package com.rpg.gui;

import com.rpg.*;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {
    private final GamePanel gamePanel;
    private final StatsPanel statsPanel;
    private final ActionPanel actionPanel;

    public GameWindow(GameWorld world, Player player) {
        super("Ashen Gate - RPG Prototype");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(new Color(20, 20, 28));

        gamePanel = new GamePanel();
        statsPanel = new StatsPanel();
        actionPanel = new ActionPanel();

        // Layout: stats on the left, game in center, log on bottom
        setLayout(new BorderLayout(2, 2));
        add(statsPanel, BorderLayout.WEST);
        add(gamePanel, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);

        // Title bar at top
        JPanel titleBar = new JPanel();
        titleBar.setBackground(new Color(20, 20, 28));
        titleBar.setPreferredSize(new Dimension(0, 32));
        titleBar.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("GLOAMCREST RISE");
        titleLabel.setForeground(new Color(180, 160, 120));
        titleLabel.setFont(new Font("Serif", Font.BOLD, 16));
        titleBar.add(titleLabel);

        JLabel coordLabel = new JLabel();
        coordLabel.setForeground(new Color(120, 120, 140));
        coordLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
        titleBar.add(Box.createHorizontalStrut(30));
        titleBar.add(coordLabel);
        add(titleBar, BorderLayout.NORTH);

        pack();
        setLocationRelativeTo(null);

        // Initialize game
        GameController controller = new GameController(world, player, gamePanel,
                statsPanel, actionPanel);
        actionPanel.setController(controller);

        Area starterArea = world.getAreas().get(0);
        TileMap tileMap = new TileMap(starterArea, controller.getStarterAreaLinks());

        // Start player at gate entrance
        int startX = TileMap.WIDTH / 2;
        int startY = TileMap.HEIGHT - 3;
        gamePanel.init(tileMap, startX, startY, controller);
        controller.start();

        // Focus the game panel for keyboard input
        SwingUtilities.invokeLater(gamePanel::requestFocusInWindow);
    }
}
