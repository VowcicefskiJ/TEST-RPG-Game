# RPG Prototype

This folder contains a lightweight Java RPG prototype inspired by classic RuneScape-style skill loops. The focus is clarity and quick extensibility so other agents can add content easily.

## Quick start

```bash
javac -d out $(find rpg/src/main/java -name "*.java")
java -cp out com.rpg.Main
```

That will run the sample loop, spell catalog preview, animation preview, and starter area overview.

## Step-by-step run guide

1. **Open a terminal at the repo root.**
   ```bash
   cd /workspace/TEST-RPG-Game
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

This prototype is currently a **local console app**, not a networked server. To run your personal “test server,” compile and launch the app locally and log in as the seeded admin user:

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
