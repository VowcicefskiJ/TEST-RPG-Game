package com.rpg;

import java.util.Scanner;

public class LoginScreen {
    private static final int MAX_ATTEMPTS = 3;

    public boolean authenticateAdmin(Scanner scanner, UserDatabase userDatabase) {
        System.out.println("=== Ashen Gate: Warden Access ===");
        System.out.println("A cold ember glow clings to the stone. The gate listens.");
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            System.out.print("Warden name: ");
            String username = scanner.nextLine().trim();
            System.out.print("Oath phrase: ");
            String password = scanner.nextLine().trim();
            if (isValidAdmin(username, password, userDatabase)) {
                System.out.println("The gate recognizes your oath. Proceed.\n");
                return true;
            }
            System.out.println("The ember dims. Attempts left: " + (MAX_ATTEMPTS - attempt));
        }
        return false;
    }

    private boolean isValidAdmin(String username, String password, UserDatabase userDatabase) {
        return userDatabase.verifyCredentials(username, password)
                && userDatabase.findUser(username)
                .map(record -> "ADMIN".equalsIgnoreCase(record.getRole()))
                .orElse(false);
    }
}
