package com.rpg;

import com.rpg.gui.GameWindow;
import com.rpg.gui.LoginDialog;

import javax.swing.*;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        // Use system look and feel for better appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            UserDatabase userDatabase = new FileUserDatabase(Path.of("rpg", "data", "users.csv"));

            // Show login dialog
            LoginDialog loginDialog = new LoginDialog(null, userDatabase);
            loginDialog.setVisible(true);

            if (!loginDialog.isAuthenticated()) {
                System.out.println("The Ashen Gate remains sealed. Exiting.");
                System.exit(0);
                return;
            }

            // Launch the game
            GameWorld world = new GameWorld();
            Player player = new Player("Adventurer");

            GameWindow window = new GameWindow(world, player);
            window.setVisible(true);
        });
    }
}
