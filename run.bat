@echo off
REM Script pour compiler et lancer l'application Green Ledger

echo [INFO] Compilation du projet...
javac -cp "src/main/java" -d target/classes -encoding UTF-8 ^
    src/main/java/org/GreenLedger/*.java ^
    src/main/java/Controllers/*.java ^
    src/main/java/Services/*.java ^
    src/main/java/dao/*.java ^
    src/main/java/Models/*.java ^
    src/main/java/DataBase/*.java ^
    src/main/java/Utils/*.java

if %errorlevel% neq 0 (
    echo [ERR] Erreur de compilation !
    pause
    exit /b 1
)

echo [OK] Compilation reussie !
echo [INFO] Demarrage de l'application...

REM Lancer l'application
java -cp "target/classes;src/main/java" org.GreenLedger.MainFX

pause

