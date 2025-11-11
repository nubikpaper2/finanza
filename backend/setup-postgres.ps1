# Script para configurar PostgreSQL para Finanza
Write-Host "==================================================" -ForegroundColor Cyan
Write-Host "  Configuración de PostgreSQL para Finanza" -ForegroundColor Cyan
Write-Host "==================================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Por favor, ingresa la contraseña del usuario 'postgres' de PostgreSQL" -ForegroundColor Yellow
Write-Host "(Es la contraseña que configuraste cuando instalaste PostgreSQL)" -ForegroundColor Yellow
Write-Host ""

$env:PGPASSWORD = Read-Host "Contraseña de postgres" -AsString

Write-Host ""
Write-Host "Creando usuario y base de datos..." -ForegroundColor Green

# Crear el usuario y la base de datos
& "C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -c "CREATE USER finanza_user WITH PASSWORD 'finanza_pass';" 2>&1 | Out-Null
& "C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -c "CREATE DATABASE finanza_db;" 2>&1 | Out-Null
& "C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE finanza_db TO finanza_user;" 2>&1 | Out-Null
& "C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -d finanza_db -c "GRANT ALL ON SCHEMA public TO finanza_user;" 2>&1 | Out-Null
& "C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -d finanza_db -c "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO finanza_user;" 2>&1 | Out-Null
& "C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -d finanza_db -c "GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO finanza_user;" 2>&1 | Out-Null
& "C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -d finanza_db -c "ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO finanza_user;" 2>&1 | Out-Null
& "C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -d finanza_db -c "ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO finanza_user;" 2>&1 | Out-Null

Write-Host ""
Write-Host "✓ Usuario 'finanza_user' creado" -ForegroundColor Green
Write-Host "✓ Base de datos 'finanza_db' creada" -ForegroundColor Green
Write-Host "✓ Privilegios otorgados" -ForegroundColor Green
Write-Host ""
Write-Host "==================================================" -ForegroundColor Cyan
Write-Host "  Configuración completada!" -ForegroundColor Green
Write-Host "==================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Presiona Enter para continuar..."
Read-Host

# Limpiar la contraseña del entorno
Remove-Item Env:PGPASSWORD
