@echo off
chcp 65001 >nul
cls

echo ╔═══════════════════════════════════════════════════════════════╗
echo ║        SISTEMA FINANZA - Inicio Automático                   ║
echo ╚═══════════════════════════════════════════════════════════════╝
echo.

REM Verificar si PostgreSQL está corriendo
echo [1/5] Verificando PostgreSQL...
sc query postgresql-x64-15 | find "RUNNING" >nul
if errorlevel 1 (
    echo   PostgreSQL no está corriendo. Intentando iniciar...
    net start postgresql-x64-15 >nul 2>&1
    if errorlevel 1 (
        echo   ERROR: No se pudo iniciar PostgreSQL
        echo   Por favor, inicia PostgreSQL manualmente
        pause
        exit /b 1
    )
    echo   PostgreSQL iniciado correctamente
) else (
    echo   PostgreSQL ya está corriendo
)
echo.

REM Ejecutar migración de base de datos
echo [2/5] Ejecutando migración de base de datos...
cd /d "%~dp0backend"
set PGPASSWORD=finanza2024
psql -h localhost -U finanza_user -d finanza_db -f migration-budgets-rules.sql >nul 2>&1
if errorlevel 1 (
    echo   Advertencia: La migración tuvo errores (las tablas pueden ya existir)
) else (
    echo   Migración completada exitosamente
)
cd /d "%~dp0"
echo.

REM Limpiar procesos anteriores
echo [3/5] Limpiando procesos anteriores...
taskkill /F /IM node.exe >nul 2>&1
taskkill /F /IM java.exe >nul 2>&1
timeout /t 2 /nobreak >nul
echo   Procesos anteriores limpiados
echo.

REM Iniciar Backend
echo [4/5] Iniciando Backend (Spring Boot)...
cd /d "%~dp0backend"
start "Finanza Backend" cmd /k "echo Compilando y ejecutando backend... && mvn clean spring-boot:run"
cd /d "%~dp0"
echo   Backend iniciado en nueva ventana
echo.

REM Esperar a que el backend esté listo
echo [5/5] Esperando a que el backend esté listo (30 segundos)...
timeout /t 30 /nobreak >nul
echo   Backend debería estar listo
echo.

REM Iniciar Frontend
echo [6/6] Iniciando Frontend (React + Vite)...
cd /d "%~dp0frontend"
start "Finanza Frontend" cmd /k "echo Iniciando frontend... && npm run dev"
cd /d "%~dp0"
echo   Frontend iniciado en nueva ventana
echo.

echo ╔═══════════════════════════════════════════════════════════════╗
echo ║        Sistema iniciado correctamente                        ║
echo ╚═══════════════════════════════════════════════════════════════╝
echo.
echo Accesos:
echo   Frontend: http://localhost:3000
echo   Backend:  http://localhost:8080
echo.
echo Nuevas funcionalidades:
echo   - Importar CSV/Excel:     http://localhost:3000/import
echo   - Presupuestos:           http://localhost:3000/budgets
echo   - Reportes:               http://localhost:3000/reports
echo   - Reglas de Categorias:   http://localhost:3000/category-rules
echo.
echo Presiona cualquier tecla para abrir el navegador...
pause >nul

REM Abrir el navegador
start http://localhost:3000

echo.
echo El sistema está corriendo. 
echo Cierra esta ventana cuando quieras detener los servicios.
echo.
pause
