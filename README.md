# Ashen Gate RPG

A Java 2D tile-based RPG with directional combat, 16 skills, 4 world zones, magic schools, quests, crafting, and more.

---

## Download & Play

> **No Java installation required** — the native installers below bundle everything.

Go to the [**Actions** tab](../../actions), click the latest **Build & Package Game** run, scroll to **Artifacts**, and download for your platform:

| Platform | Artifact name | How to install |
|----------|--------------|----------------|
| **Windows** | `AshenGate-Windows-Setup` | Unzip → run `Ashen Gate-1.0.0.exe` → double-click desktop icon |
| **macOS** | `AshenGate-macOS` | Unzip → open `.dmg` → drag to Applications |
| **Linux** | `AshenGate-Linux-deb` | Unzip → `sudo dpkg -i ashengate_1.0.0_amd64.deb` |
| **Any OS** (needs Java) | `AshenGate-JAR-portable` | Unzip → run `run.bat` (Win) or `./run.sh` (Mac/Linux) |

> **macOS note:** If you see "unidentified developer", right-click the app → Open → Open.

**Login:** Username `admin` / Password `admin123`

---

## Build From Source

### Prerequisites
- Java JDK 21+ ([adoptium.net](https://adoptium.net/))

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

### Manual compile
```bash
cd TEST-RPG-Game
mkdir -p rpg/build
javac -d rpg/build rpg/src/main/java/com/rpg/*.java rpg/src/main/java/com/rpg/gui/*.java
java -cp rpg/build com.rpg.Main
```

---

## Run Tests
```bash
javac -d rpg/build rpg/src/main/java/com/rpg/*.java rpg/src/main/java/com/rpg/gui/*.java
java -cp rpg/build com.rpg.GameSystemsTest
```
65 automated tests covering inventory, crafting, combat, quests, milestones, stamina, and XP systems.

---

## Game Features

- **4 World Zones** with portals: Gloamcrest Rise → Darkwood Forest, Hollow Caves, Ashen Ruins
- **16 Skills:** Cooking, Farming, Fishing, Fighting, Foraging, Mapping, Alchemy, Mining, Geology, Archaeology, Lorekeeping, Magic Schools, Blacksmithing, Armor Making, Weapon Making, Staff Making
- **Directional Combat:** 4-way attack/parry with combos, feints, bleed, stagger, and morale
- **Stamina System:** Actions cost stamina, recovers between turns
- **Crafting:** 8 recipes linking skills (mine ore → smelt → forge weapons)
- **Quest System:** 5 quests with progress tracking and XP/item rewards
- **Level Milestones:** Unlock reward items at skill levels 5 and 10
- **Procedural World:** Biome-aware terrain, rivers, towns, ruins, and animated tiles
- **WASD movement**, camera scrolling, minimap, animated entity sprites

---

## Default Credentials

- **Username:** `admin`
- **Password:** `admin123`

To add users, edit `rpg/data/users.csv` (password is SHA-256 hashed).

---

## Project Structure

```
rpg/
  src/main/java/com/rpg/       # Core game logic (Player, Combat, Skills, etc.)
  src/main/java/com/rpg/gui/   # Swing GUI (GamePanel, TileMap, GameController, etc.)
  data/users.csv               # User credentials
  MANIFEST.MF                  # JAR main-class manifest
build.bat / build.sh           # Build scripts (compile + package JAR)
run.bat / run.sh               # Launcher scripts
.github/workflows/build.yml    # CI: auto-builds native installers on every push
```
