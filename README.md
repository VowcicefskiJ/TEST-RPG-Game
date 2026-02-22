# Ashen Gate RPG

A Java 2D tile-based RPG with directional combat, 16 skills, 4 world zones, magic schools, quests, crafting, and more.

---

## Download & Play

**No Java install required** — pick the right file for your system:

| File | Platform | Steps |
|------|----------|-------|
| `Ashen Gate-1.0.0.exe` | **Windows** | 1. Download → 2. Run installer → 3. Launch from Start Menu or Desktop |
| `AshenGate-1.0.0.dmg` | **macOS** | 1. Download → 2. Open DMG → 3. Drag to Applications → 4. Open |
| `ashengate_1.0.0_amd64.deb` | **Linux** | 1. Download → 2. `sudo dpkg -i ashengate*.deb` → 3. Launch from apps |
| `AshenGate-portable.zip` | **Any OS** *(needs Java 21)* | 1. Download → 2. Unzip → 3. Run `run.bat` (Win) or `./run.sh` (Mac/Linux) |

**[⬇ Go to Downloads (Releases page)](../../releases/latest)**

**Login:** Username `admin` / Password `admin123`

> **Windows SmartScreen:** If Windows shows "Unknown publisher", click **More info → Run anyway** — the installer is safe but unsigned.
>
> **macOS Gatekeeper:** If blocked, right-click the `.app` → **Open** → **Open**.

---

## Build From Source

### Prerequisites
- Java JDK 21+ ([adoptium.net](https://adoptium.net/))

### Windows
```bat
git clone https://github.com/VowcicefskiJ/TEST-RPG-Game.git
cd TEST-RPG-Game
build.bat
cd dist && run.bat
```

### Mac / Linux
```bash
git clone https://github.com/VowcicefskiJ/TEST-RPG-Game.git
cd TEST-RPG-Game
chmod +x build.sh run.sh
./build.sh && cd dist && ./run.sh
```

### Manual compile
```bash
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
- **Procedural World:** Biome-aware terrain, rivers, towns, ruins, animated tiles, minimap
- **WASD movement**, camera scrolling, name-aware entity sprites with idle animations

---

## Default Credentials

- **Username:** `admin`
- **Password:** `admin123`

To add users, edit `rpg/data/users.csv` (password column is SHA-256 hashed).

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
.github/workflows/build.yml    # CI: auto-builds native installers + GitHub Release on every push
```
