package com.rpg;

import com.rpg.gui.GameWindow;
import com.rpg.gui.LoginDialog;

import javax.swing.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        // Use system look and feel for better appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            Path usersPath = resolveDataPath();
            UserDatabase userDatabase = new FileUserDatabase(usersPath);

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

    /**
     * Resolves the path to users.csv by checking multiple candidate locations
     * so the game works in dev, JAR distribution, and jpackage installs.
     */
    private static Path resolveDataPath() {
        // Candidate 1: next to the running JAR (native installer / dist layout)
        try {
            URI src = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            Path jarDir = Path.of(src).getParent();
            Path candidate = jarDir.resolve("data").resolve("users.csv");
            if (Files.exists(candidate)) return candidate;
        } catch (Exception ignored) {
        }

        // Candidate 2: current working directory (simple: java -jar AshenGate.jar)
        Path candidate2 = Path.of("data", "users.csv");
        if (Files.exists(candidate2)) return candidate2;

        // Candidate 3: repo dev layout (run from project root)
        Path candidate3 = Path.of("rpg", "data", "users.csv");
        if (Files.exists(candidate3)) return candidate3;

        // Fallback: return candidate2 so FileUserDatabase reports a clear error
        return candidate2;
    }
}
