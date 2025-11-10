# Script de inicio para desarrollo local
# Ejecutar: .\start-dev.ps1

Write-Host "🚀 Iniciando Finanza - Sistema de Gestión Financiera" -ForegroundColor Cyan
Write-Host "=================================================" -ForegroundColor Cyan
Write-Host ""

# Verificar Docker
Write-Host "📦 Verificando Docker..." -ForegroundColor Yellow
if (!(Get-Command docker -ErrorAction SilentlyContinue)) {
    Write-Host "❌ Docker no está instalado. Por favor instala Docker Desktop." -ForegroundColor Red
    exit 1
}

# Verificar Java
Write-Host "☕ Verificando Java..." -ForegroundColor Yellow
if (!(Get-Command java -ErrorAction SilentlyContinue)) {
    Write-Host "❌ Java no está instalado. Por favor instala Java 17+." -ForegroundColor Red
    exit 1
}

$javaVersion = java -version 2>&1 | Select-String "version" | ForEach-Object { $_ -replace '.*version "(.+?)".*', '$1' }
Write-Host "✓ Java version: $javaVersion" -ForegroundColor Green

# Verificar Node.js
Write-Host "📦 Verificando Node.js..." -ForegroundColor Yellow
if (!(Get-Command node -ErrorAction SilentlyContinue)) {
    Write-Host "❌ Node.js no está instalado. Por favor instala Node.js 18+." -ForegroundColor Red
    exit 1
}

$nodeVersion = node -v
Write-Host "✓ Node.js version: $nodeVersion" -ForegroundColor Green

Write-Host ""
Write-Host "🗄️  Iniciando PostgreSQL con Docker..." -ForegroundColor Yellow
docker-compose up -d postgres

Write-Host ""
Write-Host "⏳ Esperando a que PostgreSQL esté listo..." -ForegroundColor Yellow
Start-Sleep -Seconds 5

Write-Host ""
Write-Host "🔧 Configurando Backend..." -ForegroundColor Yellow
Set-Location backend

# Verificar si es primera vez
if (!(Test-Path "target")) {
    Write-Host "📥 Instalando dependencias del backend..." -ForegroundColor Yellow
    mvn clean install -DskipTests
}

Write-Host ""
Write-Host "▶️  Iniciando Backend en segundo plano..." -ForegroundColor Green
$backendJob = Start-Job -ScriptBlock {
    Set-Location $using:PWD/backend
    mvn spring-boot:run
}

Set-Location ..

Write-Host ""
Write-Host "🎨 Configurando Frontend..." -ForegroundColor Yellow
Set-Location frontend

# Verificar si node_modules existe
if (!(Test-Path "node_modules")) {
    Write-Host "📥 Instalando dependencias del frontend..." -ForegroundColor Yellow
    npm install
}

Write-Host ""
Write-Host "⏳ Esperando a que el backend esté listo..." -ForegroundColor Yellow
Start-Sleep -Seconds 15

Write-Host ""
Write-Host "✅ Sistema iniciado correctamente!" -ForegroundColor Green
Write-Host ""
Write-Host "📍 URLs Disponibles:" -ForegroundColor Cyan
Write-Host "   Frontend: http://localhost:3000" -ForegroundColor White
Write-Host "   Backend:  http://localhost:8080/api" -ForegroundColor White
Write-Host "   Health:   http://localhost:8080/api/health" -ForegroundColor White
Write-Host ""
Write-Host "👤 Credenciales Demo:" -ForegroundColor Cyan
Write-Host "   Admin: admin@demo.com / admin123" -ForegroundColor White
Write-Host "   User:  user@demo.com / user123" -ForegroundColor White
Write-Host ""
Write-Host "▶️  Iniciando servidor de desarrollo del frontend..." -ForegroundColor Green
Write-Host ""

# Ejecutar frontend
npm run dev

# Cleanup cuando se cierre
Set-Location ..
Stop-Job $backendJob
Remove-Job $backendJob
docker-compose down
