# Ashen Gate RPG

A Java 2D tile-based RPG with directional combat, 16 skills, magic schools, quests, crafting, and more.

## Download & Play (Easiest)

1. Go to the [**Actions** tab](../../actions) on GitHub
2. Click the latest **Build & Package Game** run
3. Scroll to **Artifacts** and download **AshenGate-RPG**
4. Unzip the download
5. **Windows:** Double-click `run.bat` (or double-click `AshenGate.jar`)
6. **Mac/Linux:** Run `./run.sh` in terminal

**Requires:** Java 8 or newer. Download free from [adoptium.net](https://adoptium.net/) if needed.

**Login:** Username `admin` / Password `admin123`

## Build From Source

### Prerequisites
- Java JDK 8+ ([adoptium.net](https://adoptium.net/))

### Windows
```bat
git clone https://github.com/VowcicefskiJ/TEST-RPG-Game.git
cd TEST-RPG-Game
build.bat
cd dist
run.bat
```

### Mac / Linux
```bash
git clone https://github.com/VowcicefskiJ/TEST-RPG-Game.git
cd TEST-RPG-Game
chmod +x build.sh run.sh
./build.sh
cd dist
./run.sh
```

### Manual compile (no build script)
```bash
cd TEST-RPG-Game/rpg
mkdir -p build
javac -d build src/main/java/com/rpg/*.java src/main/java/com/rpg/gui/*.java
java -cp build com.rpg.Main
```

## Run Tests
```bash
javac -d rpg/build rpg/src/main/java/com/rpg/*.java rpg/src/main/java/com/rpg/gui/*.java
java -cp rpg/build com.rpg.GameSystemsTest
```
65 automated tests cover inventory, crafting, combat, quests, milestones, stamina, and XP systems.

## Game Features

- **16 Skills:** Cooking, Farming, Fishing, Fighting, Foraging, Mapping, Alchemy, Mining, Geology, Archaeology, Lorekeeping, Magic Schools, Blacksmithing, Armor Making, Weapon Making, Staff Making
- **Directional Combat:** 4-way attack/parry system with combos, feints, bleed, stagger, and morale
- **Stamina System:** Actions cost stamina, recovers between turns
- **Crafting:** 8 recipes linking skills together (mine ore -> smelt -> forge weapons)
- **Quest System:** 5 quests with progress tracking and XP/item rewards
- **Level Milestones:** Unlock rewards at skill levels 5 and 10
- **Exponential XP Curve:** Early levels are quick, high levels feel earned
- **Tile-based GUI:** WASD movement, mouse interaction, context-aware action buttons

## Default Credentials

- **Username:** `admin`
- **Password:** `admin123`

To add users, edit `rpg/data/users.csv` with a SHA-256 hashed password.

## Project Structure

```
rpg/
  src/main/java/com/rpg/       # Core game logic
  src/main/java/com/rpg/gui/   # Swing GUI (GameWindow, GamePanel, etc.)
  data/users.csv                # User database
  MANIFEST.MF                   # JAR manifest
build.bat / build.sh            # Build scripts
run.bat / run.sh                # Launcher scripts
.github/workflows/build.yml     # CI: auto-builds JAR on every push
docs/PROJECT_MAP.md             # Detailed codebase map
```

For a full map of the codebase, see `docs/PROJECT_MAP.md`.
