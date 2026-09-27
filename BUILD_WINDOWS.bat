@echo off
chcp 65001 >nul
echo ============================================
echo   Aurora Client - one-click build
echo ============================================
echo.
echo Checking Java...
where java >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java not found! Install JDK 21 from https://adoptium.net/
    pause
    exit /b 1
)
java -version
echo.
echo Starting build (first run will download Minecraft/Fabric, ~1-2 min)...
echo.
call gradlew.bat remapJar build -x test
if errorlevel 1 (
    echo.
    echo BUILD FAILED! See errors above.
    pause
    exit /b 1
)
echo.
echo ============================================
echo   BUILD SUCCESS!
echo ============================================
echo.
echo JAR file is at: build\libs\aurora-client-1.0.0.jar
echo Copy it into your Minecraft mods folder.
echo.
explorer build\libs
pause
