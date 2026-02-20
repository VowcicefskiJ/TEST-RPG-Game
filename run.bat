@echo off
title Ashen Gate RPG
echo Starting Ashen Gate RPG...
echo.

:: Check if Java is installed
java -version >nul 2>&1
if errorlevel 1 (
    echo  *** Java is not installed ***
    echo.
    echo  This portable JAR version requires Java 21 or newer.
    echo  For a version that needs NO software install, use the
    echo  native installer instead:
    echo.
    echo    GitHub Actions tab -^> Latest build -^> AshenGate-Windows-Setup
    echo.
    echo  Or install Java from: https://adoptium.net/
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
