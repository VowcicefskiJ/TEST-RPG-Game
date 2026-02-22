#!/usr/bin/env bash
set -e

echo "=== Building Ashen Gate RPG ==="
echo

# Clean
rm -rf dist rpg/build
mkdir -p rpg/build dist/data

# Compile
echo "Compiling Java sources..."
javac --release 17 -d rpg/build rpg/src/main/java/com/rpg/*.java rpg/src/main/java/com/rpg/gui/*.java

# Package JAR
echo "Packaging JAR..."
jar cfm dist/AshenGate.jar rpg/MANIFEST.MF -C rpg/build .

# Copy data files
echo "Copying game data..."
cp rpg/data/users.csv dist/data/users.csv

# Copy launcher
cp run.sh dist/run.sh
chmod +x dist/run.sh

echo
echo "=== Build Complete ==="
echo
echo "Output: dist/AshenGate.jar"
echo
echo "To play: cd dist && ./run.sh"
echo "Or: java -jar dist/AshenGate.jar"
echo
