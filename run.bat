@echo off
echo ===================================================================
echo  LAUNCHING SKILL CHAIN DESKTOP APPLICATION
echo ===================================================================

set "JAVA_CMD=java"
where java >nul 2>nul
if %errorlevel% neq 0 (
    if exist "C:\Program Files\Java\jdk-25.0.4.1\bin\java.exe" (
        set "JAVA_CMD=C:\Program Files\Java\jdk-25.0.4.1\bin\java.exe"
    ) else (
        echo [ERROR] Java Runtime not found on PATH.
        pause
        exit /b 1
    )
)

"%JAVA_CMD%" -cp "target\classes;lib\mysql-connector-j-8.4.0.jar" com.skillchain.Main
