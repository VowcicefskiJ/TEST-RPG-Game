package com.rpg;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FileUserDatabase implements UserDatabase {
    private final Map<String, UserRecord> users = new HashMap<>();

    public FileUserDatabase(Path filePath) {
        loadUsers(filePath);
    }

    @Override
    public Optional<UserRecord> findUser(String username) {
        return Optional.ofNullable(users.get(username.toLowerCase()));
    }

    private void loadUsers(Path filePath) {
        try {
            List<String> lines = Files.readAllLines(filePath);
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#") || trimmed.startsWith("username,")) {
                    continue;
                }
                String[] parts = trimmed.split(",", 3);
                if (parts.length < 3) {
                    continue;
                }
                String username = parts[0].trim();
                String hash = parts[1].trim();
                String role = parts[2].trim();
                users.put(username.toLowerCase(), new UserRecord(username, hash, role));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load users database at " + filePath, e);
        }
    }
}
