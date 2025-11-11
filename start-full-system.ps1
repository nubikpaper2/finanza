# Script de Inicio - Sistema Finanza con Nuevas Funcionalidades
# Ejecuta migracion de base de datos y arranca backend + frontend

Write-Host "Iniciando Sistema Finanza con Nuevas Funcionalidades..." -ForegroundColor Cyan
Write-Host ""

# 1. Ejecutar migracion de base de datos
Write-Host "Ejecutando migracion de base de datos..." -ForegroundColor Yellow
$env:PGPASSWORD = "finanza2024"
$migrationResult = psql -h localhost -U finanza_user -d finanza_db -f backend\migration-budgets-rules.sql 2>&1

if ($LASTEXITCODE -eq 0) {
    Write-Host "Migracion completada exitosamente" -ForegroundColor Green
} else {
    Write-Host "Advertencia: La migracion pudo tener errores (las tablas pueden ya existir)" -ForegroundColor Yellow
    Write-Host $migrationResult
}

Write-Host ""

# 2. Limpiar procesos anteriores
Write-Host "Limpiando procesos anteriores..." -ForegroundColor Yellow
Get-Process node -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Get-Process java -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 2

Write-Host ""

# 3. Iniciar Backend
Write-Host "Iniciando Backend (Spring Boot)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$PSScriptRoot\backend'; Write-Host 'Compilando y ejecutando backend...' -ForegroundColor Cyan; mvn clean spring-boot:run"

Write-Host "Esperando a que el backend este listo (30 segundos)..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

Write-Host ""

# 4. Iniciar Frontend
Write-Host "Iniciando Frontend (React + Vite)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$PSScriptRoot\frontend'; Write-Host 'Iniciando frontend...' -ForegroundColor Cyan; npm run dev"

Write-Host ""
Write-Host "Sistema iniciado correctamente!" -ForegroundColor Green
Write-Host ""
Write-Host "Accesos:" -ForegroundColor Cyan
Write-Host "   Frontend: http://localhost:3000" -ForegroundColor White
Write-Host "   Backend:  http://localhost:8080" -ForegroundColor White
Write-Host ""
Write-Host "Nuevas funcionalidades disponibles:" -ForegroundColor Cyan
Write-Host "   - Importar CSV/Excel:     http://localhost:3000/import" -ForegroundColor White
Write-Host "   - Presupuestos:           http://localhost:3000/budgets" -ForegroundColor White
Write-Host "   - Reportes:               http://localhost:3000/reports" -ForegroundColor White
Write-Host "   - Reglas de Categorias:   http://localhost:3000/category-rules" -ForegroundColor White
Write-Host ""
Write-Host "Ver documentacion completa en: NUEVAS_FUNCIONALIDADES.md" -ForegroundColor Yellow
Write-Host ""
Write-Host "Presiona Ctrl+C en cada ventana para detener los servicios" -ForegroundColor Gray
