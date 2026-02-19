@echo off
setlocal

echo === Building Ashen Gate RPG ===
echo.

:: Clean
if exist dist rmdir /s /q dist
if exist rpg\build rmdir /s /q rpg\build
mkdir rpg\build
mkdir dist
mkdir dist\data

:: Compile
echo Compiling Java sources...
javac -d rpg\build rpg\src\main\java\com\rpg\*.java rpg\src\main\java\com\rpg\gui\*.java
if errorlevel 1 (
    echo.
    echo BUILD FAILED: Compilation errors.
    exit /b 1
)

:: Package JAR
echo Packaging JAR...
jar cfm dist\AshenGate.jar rpg\MANIFEST.MF -C rpg\build .
if errorlevel 1 (
    echo.
    echo BUILD FAILED: Could not create JAR.
    exit /b 1
)

:: Copy data files
echo Copying game data...
copy rpg\data\users.csv dist\data\users.csv >nul

:: Copy launcher
copy run.bat dist\run.bat >nul

echo.
echo === Build Complete ===
echo.
echo Output: dist\AshenGate.jar
echo.
echo To play: cd dist ^& run.bat
echo Or just double-click AshenGate.jar in the dist folder!
echo.

endlocal
