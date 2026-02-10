package com.rpg;

import java.util.Optional;

public interface UserDatabase {
    Optional<UserRecord> findUser(String username);

    default boolean verifyCredentials(String username, String password) {
        return findUser(username)
                .map(record -> new PasswordHasher().hash(password).equals(record.getPasswordHash()))
                .orElse(false);
    }
}
