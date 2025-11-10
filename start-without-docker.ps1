# Script para ejecutar sin Docker
# Usar PostgreSQL local o ajustar a tu instalación
# Ejecutar: .\start-without-docker.ps1

Write-Host "🚀 Iniciando Finanza SIN Docker" -ForegroundColor Cyan
Write-Host "====================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "⚠️  NOTA IMPORTANTE:" -ForegroundColor Yellow
Write-Host "Este script asume que tienes PostgreSQL instalado localmente" -ForegroundColor Yellow
Write-Host "Si no tienes PostgreSQL, debes:" -ForegroundColor Yellow
Write-Host "1. Instalar PostgreSQL desde https://www.postgresql.org/download/windows/" -ForegroundColor Yellow
Write-Host "2. Crear la base de datos 'finanza_db'" -ForegroundColor Yellow
Write-Host "3. Crear el usuario 'finanza_user' con password 'finanza_pass'" -ForegroundColor Yellow
Write-Host ""

$continue = Read-Host "¿Tienes PostgreSQL instalado y configurado? (S/N)"
if ($continue -ne "S" -and $continue -ne "s") {
    Write-Host ""
    Write-Host "❌ Necesitas PostgreSQL o Docker Desktop para continuar" -ForegroundColor Red
    Write-Host ""
    Write-Host "OPCIONES:" -ForegroundColor Cyan
    Write-Host "1. Instalar PostgreSQL: https://www.postgresql.org/download/windows/" -ForegroundColor White
    Write-Host "2. O mejor aún, iniciar Docker Desktop y usar: .\start-dev.ps1" -ForegroundColor White
    exit 1
}

Write-Host ""
Write-Host "✓ Verificando PostgreSQL..." -ForegroundColor Green

# Verificar Java
Write-Host "☕ Verificando Java..." -ForegroundColor Yellow
$javaVersion = java -version 2>&1 | Select-String "version" | ForEach-Object { $_ -replace '.*version "(.+?)".*', '$1' }
Write-Host "✓ Java version: $javaVersion" -ForegroundColor Green

# Verificar Maven
Write-Host "📦 Verificando Maven..." -ForegroundColor Yellow
$mavenVersion = mvn -version 2>&1 | Select-String "Apache Maven" | Select-Object -First 1
Write-Host "✓ $mavenVersion" -ForegroundColor Green

# Verificar Node
Write-Host "📦 Verificando Node.js..." -ForegroundColor Yellow
$nodeVersion = node -v
Write-Host "✓ Node.js version: $nodeVersion" -ForegroundColor Green

Write-Host ""
Write-Host "🔧 Configurando Backend..." -ForegroundColor Yellow
Set-Location backend

if (!(Test-Path "target")) {
    Write-Host "📥 Compilando backend por primera vez..." -ForegroundColor Yellow
    mvn clean install -DskipTests
}

Write-Host ""
Write-Host "▶️  Iniciando Backend..." -ForegroundColor Green
Write-Host "Backend estará en: http://localhost:8080/api" -ForegroundColor Cyan

# Iniciar backend en segundo plano
$backendJob = Start-Job -ScriptBlock {
    Set-Location $using:PWD
    mvn spring-boot:run
}

Set-Location ..

Write-Host ""
Write-Host "⏳ Esperando a que el backend inicie (15 segundos)..." -ForegroundColor Yellow
Start-Sleep -Seconds 15

Write-Host ""
Write-Host "🎨 Configurando Frontend..." -ForegroundColor Yellow
Set-Location frontend

if (!(Test-Path "node_modules")) {
    Write-Host "📥 Instalando dependencias del frontend..." -ForegroundColor Yellow
    npm install
}

Write-Host ""
Write-Host "✅ Sistema iniciado!" -ForegroundColor Green
Write-Host ""
Write-Host "📍 URLs:" -ForegroundColor Cyan
Write-Host "   Frontend: http://localhost:3000" -ForegroundColor White
Write-Host "   Backend:  http://localhost:8080/api" -ForegroundColor White
Write-Host "   Health:   http://localhost:8080/api/health" -ForegroundColor White
Write-Host ""
Write-Host "👤 Credenciales:" -ForegroundColor Cyan
Write-Host "   Admin: admin@demo.com / admin123" -ForegroundColor White
Write-Host "   User:  user@demo.com / user123" -ForegroundColor White
Write-Host ""
Write-Host "▶️  Iniciando Frontend..." -ForegroundColor Green

# Ejecutar frontend (bloqueante)
npm run dev

# Cleanup
Set-Location ..
Stop-Job $backendJob
Remove-Job $backendJob
