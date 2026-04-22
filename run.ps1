# Performance Pulse - PowerShell Run Script
if (!(Test-Path bin)) { 
    New-Item -ItemType Directory -Path bin | Out-Null
    Write-Host "Created bin directory..." -ForegroundColor Cyan
}

Write-Host "Compiling source files..." -ForegroundColor Yellow

$javaFiles = Get-ChildItem -Path src -Filter *.java -Recurse | Select-Object -ExpandProperty FullName
if ($javaFiles.Count -eq 0) {
    Write-Error "No .java source files found in src directory!"
    exit
}

javac -cp ".;lib/*" -d bin $javaFiles

if ($LASTEXITCODE -ne 0) {
    Write-Host "Compilation failed! Check for errors above." -ForegroundColor Red
    pause
    exit
}

Write-Host "Compilation successful!" -ForegroundColor Green
Write-Host "Launching Performance Pulse..." -ForegroundColor Yellow

java -cp "bin;lib/*" ui.PerformanceManagementUI
