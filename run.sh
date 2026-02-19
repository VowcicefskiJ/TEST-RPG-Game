#!/usr/bin/env bash

echo "Starting Ashen Gate..."

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo
    echo "ERROR: Java is not installed or not in your PATH."
    echo
    echo "Install Java:"
    echo "  Ubuntu/Debian: sudo apt install default-jre"
    echo "  macOS:         brew install openjdk"
    echo "  Or download:   https://adoptium.net/"
    echo
    exit 1
fi

# Run the game
java -jar AshenGate.jar
