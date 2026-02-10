# TEST-RPG-Game

See the RPG prototype in `rpg/` for the Java skill-loop demo, entry point, and design notes.

## Quick start

```bash
javac -d out $(find rpg/src/main/java -name "*.java")
java -cp out com.rpg.Main
```

That will run the sample loop, spell catalog preview, animation preview, and starter area overview.

## Windows quick start (PowerShell)

1. **Install a JDK (if you see "javac is not recognized").**
   - Recommended: **JDK 21 (LTS)**. JDK 17 (LTS) also works.
   - Download and install a JDK (e.g., Temurin or Oracle JDK).
   - After install, open a new PowerShell and confirm:
     ```powershell
     java -version
     javac -version
     ```
2. **Clone and enter the repo.**
   ```powershell
   git clone https://github.com/VowcicefskiJ/TEST-RPG-Game.git
   cd TEST-RPG-Game
   ```
3. **Compile using PowerShell syntax.**
   ```powershell
   javac -d out (Get-ChildItem -Recurse rpg/src/main/java -Filter *.java).FullName
   ```
4. **Run the game.**
   ```powershell
   java -cp out com.rpg.Main
   ```

## Step-by-step run guide

1. **Open a terminal at the repo root.**
   ```bash
   git clone https://github.com/VowcicefskiJ/TEST-RPG-Game.git
   cd TEST-RPG-Game
   ```
2. **Compile the Java sources.**
   ```bash
   javac -d out $(find rpg/src/main/java -name "*.java")
   ```
3. **Run the game.**
   ```bash
   java -cp out com.rpg.Main
   ```
4. **Log in at the prompt.** Use the seeded admin account:
   - **Username:** `admin`
   - **Password:** `admin123`
5. **Follow the console output.** You will see the XP loop, combat preview, animations, and starter-area overview.

## Local test server (single-user)

This prototype is currently a **local console app**, not a networked server. To run your personal "test server," compile and launch the app locally and log in as the seeded admin user:

```bash
javac -d out $(find rpg/src/main/java -name "*.java")
java -cp out com.rpg.Main
```

Default admin credentials (from `rpg/data/users.csv`):
- **Username:** `admin`
- **Password:** `admin123`

If you want to add your own test user, append a new row to `rpg/data/users.csv` with a SHA-256 password hash. You can generate hashes using the `PasswordHasher` class or any SHA-256 tool and keep the role as `ADMIN` for now.

## Design notes

- **Skill loop:** Actions award XP and levels via `GameEngine` + `SkillAction`.
- **World data:** `GameWorld` aggregates areas, skills, spells, and animations.
- **Combat preview:** `CombatSystem` shows directional parry resolution.
- **AI agents:** `AiAgent` and `BasicPlannerAgent` pick actions to progress skills.

For a full map of the codebase, see `docs/PROJECT_MAP.md`.
