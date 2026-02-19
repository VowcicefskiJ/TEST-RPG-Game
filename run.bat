@echo off
title Ashen Gate RPG
echo Starting Ashen Gate...

:: Check if Java is installed
java -version >nul 2>&1
if errorlevel 1 (
    echo.
    echo ERROR: Java is not installed or not in your PATH.
    echo.
    echo Download Java from: https://adoptium.net/
    echo After installing, restart this script.
    echo.
    pause
    exit /b 1
)

:: Run the game
java -jar AshenGate.jar
if errorlevel 1 (
    echo.
    echo The game exited with an error. Check the output above.
    pause
)
