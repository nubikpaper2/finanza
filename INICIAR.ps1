# Script para iniciar Finanza fácilmente
Write-Host "🚀 Iniciando Finanza..." -ForegroundColor Green

# Matar procesos anteriores
Write-Host "🧹 Limpiando procesos anteriores..." -ForegroundColor Yellow
Get-Process node -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 1

# Iniciar Backend (Spring Boot)
Write-Host "🔧 Iniciando Backend en puerto 8080..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$PSScriptRoot\backend'; mvn spring-boot:run"

# Esperar un poco para que el backend inicie
Start-Sleep -Seconds 3

# Iniciar Frontend (Vite en puerto 3000)
Write-Host "🎨 Iniciando Frontend en puerto 3000..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$PSScriptRoot\frontend'; npx vite --port 3000"

# Esperar y abrir navegador
Start-Sleep -Seconds 5
Write-Host "🌐 Abriendo navegador..." -ForegroundColor Green
Start-Process "http://localhost:3000"

Write-Host ""
Write-Host "✅ ¡Finanza iniciado correctamente!" -ForegroundColor Green
Write-Host "📍 Frontend: http://localhost:3000" -ForegroundColor White
Write-Host "📍 Backend:  http://localhost:8080/api" -ForegroundColor White
Write-Host ""
Write-Host "Credenciales de prueba:" -ForegroundColor Yellow
Write-Host "  Email:    admin@demo.com" -ForegroundColor White
Write-Host "  Password: admin123" -ForegroundColor White
Write-Host ""
Write-Host "Presiona cualquier tecla para cerrar..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
