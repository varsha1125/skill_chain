@echo off
setlocal
echo ===================================================================
echo  SKILL CHAIN: BUILD AND OOP SMOKE TEST
echo ===================================================================

if not exist "target\classes" mkdir "target\classes"
if not exist "target\test-classes" mkdir "target\test-classes"

where javac >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERROR] JDK javac compiler not found on PATH.
    exit /b 1
)

echo Compiling production sources...
javac -d target\classes -cp "lib\mysql-connector-j-8.4.0.jar" src\main\java\com\skillchain\*.java src\main\java\com\skillchain\model\*.java src\main\java\com\skillchain\dao\*.java src\main\java\com\skillchain\service\*.java src\main\java\com\skillchain\util\*.java src\main\java\com\skillchain\ui\*.java
if %errorlevel% neq 0 exit /b 1

echo Compiling verification test...
javac -d target\test-classes -cp "target\classes;lib\mysql-connector-j-8.4.0.jar" src\test\java\com\skillchain\verification\CoreOopVerification.java
if %errorlevel% neq 0 exit /b 1

echo Running verification test...
java -ea -cp "target\test-classes;target\classes;lib\mysql-connector-j-8.4.0.jar" com.skillchain.verification.CoreOopVerification
if %errorlevel% neq 0 exit /b 1

echo [SUCCESS] Build and core OOP verification passed.
endlocal
