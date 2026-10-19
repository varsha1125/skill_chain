@echo off
echo ===================================================================
echo  SKILL CHAIN: COMPILATION SCRIPT
echo ===================================================================

if not exist "target\classes" mkdir "target\classes"

set "JAVAC_CMD=javac"
where javac >nul 2>nul
if %errorlevel% neq 0 (
    if exist "C:\Program Files\Java\jdk-25.0.4.1\bin\javac.exe" (
        set "JAVAC_CMD=C:\Program Files\Java\jdk-25.0.4.1\bin\javac.exe"
    ) else (
        echo [ERROR] JDK javac compiler not found on PATH.
        pause
        exit /b 1
    )
)

echo Compiling Java sources to target\classes...
"%JAVAC_CMD%" -d target\classes -cp "lib\mysql-connector-j-8.4.0.jar" src\main\java\com\skillchain\*.java src\main\java\com\skillchain\model\*.java src\main\java\com\skillchain\dao\*.java src\main\java\com\skillchain\service\*.java src\main\java\com\skillchain\util\*.java src\main\java\com\skillchain\ui\*.java

if %errorlevel% equ 0 (
    echo [SUCCESS] Compilation finished with 0 errors!
) else (
    echo [ERROR] Compilation failed.
)
pause
