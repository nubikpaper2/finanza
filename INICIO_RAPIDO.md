# 🚀 Inicio Rápido - Nuevas Funcionalidades

## ✨ Lo Nuevo

Se han implementado las siguientes funcionalidades:

### 📥 1. Importación de Datos
- Importar transacciones desde CSV o Excel
- Auto-categorización basada en reglas
- Plantilla descargable
- Reporte de errores detallado

### 🎯 2. Presupuestos Mensuales
- Crear presupuestos por categoría
- Alertas visuales (verde/amarillo/rojo)
- Seguimiento de gasto vs presupuesto
- Barra de progreso en tiempo real

### 🤖 3. Reglas de Auto-Categorización
- 6 tipos de reglas (contiene, empieza con, regex, etc.)
- Sistema de prioridades
- Activar/desactivar reglas
- Aplicación automática en importación

### 📊 4. Reportes y Dashboards
- Reporte mensual detallado
- Reporte anual comparativo
- Gráficos interactivos (barras, pastel, líneas)
- Top categorías de gastos
- Análisis de tendencias

## 🏃‍♂️ Inicio Rápido

### Opción 1: Script Automático (Recomendado)
```powershell
.\start-full-system.ps1
```

Este script:
1. Ejecuta la migración de base de datos
2. Inicia el backend en puerto 8080
3. Inicia el frontend en puerto 3000

### Opción 2: Manual

#### 1. Migración de Base de Datos
```powershell
cd backend
$env:PGPASSWORD = "finanza2024"
psql -h localhost -U finanza_user -d finanza_db -f migration-budgets-rules.sql
```

#### 2. Backend
```powershell
cd backend
mvn clean spring-boot:run
```

#### 3. Frontend
```powershell
cd frontend
npm install
npm run dev
```

## 📝 Flujo de Uso

### 1. Configurar Reglas de Categorización
1. Ir a `/category-rules`
2. Crear reglas como:
   - "supermercado" → Categoría "Alimentación"
   - "salario" → Categoría "Salario"
   - "gasolina" → Categoría "Transporte"

### 2. Importar Transacciones
1. Ir a `/import`
2. Descargar plantilla CSV
3. Llenar con tus datos
4. Subir archivo
5. Las transacciones se categorizan automáticamente

### 3. Configurar Presupuestos
1. Ir a `/budgets`
2. Crear presupuesto para cada categoría
3. Ver alertas cuando te acerques al límite

### 4. Ver Reportes
1. Ir a `/reports`
2. Analizar gastos por categoría
3. Ver gráficos y tendencias

## 📂 Archivos de Ejemplo

- `ejemplo-importacion.csv` - Archivo de ejemplo para importar
- Ver formato completo en `/import` (botón "Descargar Plantilla")

## 🔗 URLs Importantes

- Frontend: http://localhost:3000
- Backend API: http://localhost:8080
- Importar: http://localhost:3000/import
- Presupuestos: http://localhost:3000/budgets
- Reportes: http://localhost:3000/reports
- Reglas: http://localhost:3000/category-rules

## 📚 Documentación Completa

Ver `NUEVAS_FUNCIONALIDADES.md` para:
- Documentación completa de APIs
- Estructura de base de datos
- Ejemplos de uso
- Mejores prácticas

## 🆘 Solución de Problemas

### Backend no inicia
- Verificar que PostgreSQL esté corriendo
- Verificar credenciales en `application.properties`
- Ejecutar migración: `psql -h localhost -U finanza_user -d finanza_db -f backend/migration-budgets-rules.sql`

### Frontend no inicia
- Ejecutar `npm install` en carpeta frontend
- Verificar que puerto 3000 esté libre

### Importación falla
- Verificar formato de CSV (ver plantilla)
- Verificar que las fechas estén en formato correcto
- Verificar que el campo "tipo" sea INCOME o EXPENSE

## ✅ Checklist Post-Instalación

- [ ] Base de datos migrada (tablas budgets y category_rules creadas)
- [ ] Backend corriendo en puerto 8080
- [ ] Frontend corriendo en puerto 3000
- [ ] Categorías creadas en `/categories`
- [ ] Reglas de categorización configuradas en `/category-rules`
- [ ] Primera importación exitosa en `/import`
- [ ] Presupuestos configurados en `/budgets`
- [ ] Reportes visibles en `/reports`

## 🎉 ¡Listo para Usar!

El sistema está completamente funcional con todas las nuevas características integradas.
