@echo off
if not exist bin mkdir bin
echo Compiling...

REM Build a list of all .java files to compile
dir /s /B src\*.java > sources.txt

javac -cp ".;lib/*" @sources.txt -d bin
if %errorlevel% neq 0 (
    echo Compilation failed!
    del sources.txt
    pause
    exit /b %errorlevel%
)
del sources.txt

echo Running Performance Pulse...
java -cp "bin;lib/*" ui.PerformanceManagementUI
pause
