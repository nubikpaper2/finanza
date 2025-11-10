# Script simple para iniciar solo con Docker
# Ejecutar: .\start-docker.ps1

Write-Host "🐳 Iniciando Finanza con Docker Compose..." -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# Verificar Docker
if (!(Get-Command docker -ErrorAction SilentlyContinue)) {
    Write-Host "❌ Docker no está instalado." -ForegroundColor Red
    exit 1
}

Write-Host "📦 Construyendo y levantando servicios..." -ForegroundColor Yellow
docker-compose up --build -d

Write-Host ""
Write-Host "⏳ Esperando a que los servicios estén listos..." -ForegroundColor Yellow
Start-Sleep -Seconds 20

Write-Host ""
Write-Host "✅ Sistema iniciado!" -ForegroundColor Green
Write-Host ""
Write-Host "📍 URLs:" -ForegroundColor Cyan
Write-Host "   Frontend: http://localhost:3000" -ForegroundColor White
Write-Host "   Backend:  http://localhost:8080/api" -ForegroundColor White
Write-Host ""
Write-Host "👤 Credenciales:" -ForegroundColor Cyan
Write-Host "   admin@demo.com / admin123" -ForegroundColor White
Write-Host "   user@demo.com / user123" -ForegroundColor White
Write-Host ""
Write-Host "📊 Ver logs:" -ForegroundColor Yellow
Write-Host "   docker-compose logs -f" -ForegroundColor White
Write-Host ""
Write-Host "⏹️  Detener:" -ForegroundColor Yellow
Write-Host "   docker-compose down" -ForegroundColor White
Write-Host ""

# Mostrar logs
docker-compose logs -f
