# 🚀 Deployment - Soporte Argentina

## ⚡ Guía de Deployment Paso a Paso

### Pre-requisitos
- ✅ PostgreSQL corriendo
- ✅ Java 17+
- ✅ Maven 3.8+
- ✅ Node.js 18+
- ✅ Backup de la base de datos actual

---

## 📋 PASO 1: Backup de Base de Datos

```powershell
# Crear backup antes de migrar
pg_dump -U postgres -d finanza -F c -b -v -f "finanza_backup_$(Get-Date -Format 'yyyyMMdd_HHmmss').backup"
```

---

## 📋 PASO 2: Aplicar Migración

```powershell
# Ir al directorio del proyecto
cd e:\Proyectos\finanza

# Ejecutar migración
psql -U postgres -d finanza -f backend\migration-argentina-support.sql

# Verificar que las tablas se crearon
psql -U postgres -d finanza -c "\dt"
```

### Verificar Tablas Creadas
Deberías ver:
- `exchange_rates`
- `credit_cards`
- `credit_card_installments`
- `argentina_taxes`

Y la tabla `transactions` con los nuevos campos.

---

## 📋 PASO 3: Compilar Backend

```powershell
cd backend

# Limpiar y compilar
mvn clean package -DskipTests

# Verificar que el JAR se creó
ls target\*.jar
```

Deberías ver: `finanza-backend-1.0.0.jar`

---

## 📋 PASO 4: Compilar Frontend

```powershell
cd ..\frontend

# Instalar dependencias (solo si es necesario)
npm install

# Compilar para producción
npm run build

# Verificar que se creó el build
ls dist
```

Deberías ver el directorio `dist` con los archivos compilados.

---

## 📋 PASO 5: Detener Servicios Actuales

### Si usas Docker:
```powershell
docker-compose down
```

### Si NO usas Docker:
```powershell
# Detener manualmente los procesos de:
# - Backend (Spring Boot)
# - Frontend (Vite/npm)
# - PostgreSQL (si está como servicio local)
```

---

## 📋 PASO 6: Iniciar Servicios

### Opción A: Con Docker
```powershell
# Reconstruir imágenes con los cambios
docker-compose build

# Iniciar servicios
docker-compose up -d

# Ver logs
docker-compose logs -f backend
```

### Opción B: Sin Docker

#### Terminal 1 - PostgreSQL
```powershell
# Si PostgreSQL no está corriendo como servicio:
# Iniciarlo manualmente o verificar que el servicio esté activo
```

#### Terminal 2 - Backend
```powershell
cd backend
mvn spring-boot:run

# O usando el JAR compilado:
java -jar target\finanza-backend-1.0.0.jar
```

#### Terminal 3 - Frontend (Dev)
```powershell
cd frontend
npm run dev
```

#### Terminal 3 - Frontend (Producción)
```powershell
cd frontend
npm run preview
# O servir con nginx/Apache
```

---

## 📋 PASO 7: Verificar Deployment

### 7.1 Verificar Backend
```powershell
# Health check
curl http://localhost:8080/api/health

# Deberías recibir: {"status": "UP"}
```

### 7.2 Verificar Base de Datos
```powershell
# Conectar a PostgreSQL
psql -U postgres -d finanza

# Verificar tablas
\dt

# Ver tipos de cambio de ejemplo (si se crearon)
SELECT * FROM exchange_rates;

# Salir
\q
```

### 7.3 Verificar Frontend
```
Abrir navegador: http://localhost:5173
```

Deberías poder:
- Login con tu usuario
- Ver el menú con las nuevas opciones: Tarjetas, Cuotas, TC

---

## 📋 PASO 8: Configuración Inicial

### 8.1 Crear Tipos de Cambio

1. Login en la aplicación
2. Ir a **TC** (Tipos de Cambio)
3. Agregar cotizaciones del día:

```json
{
  "date": "2025-11-11",
  "rateType": "OFICIAL",
  "buyRate": 1000.50,
  "sellRate": 1010.50
}
```

Repetir para: BLUE, MEP, TARJETA

### 8.2 Crear Primera Tarjeta

1. Ir a **Tarjetas**
2. Click **+ Nueva Tarjeta**
3. Completar formulario
4. Guardar

### 8.3 Probar Funcionalidad

1. Ir a **Transacciones**
2. Crear nueva transacción con:
   - Tarjeta seleccionada
   - 3 cuotas
3. Ir a **Cuotas**
4. Verificar que se crearon las 3 cuotas

---

## 📋 PASO 9: Testing

### 9.1 Test API con curl

#### Tipos de Cambio
```powershell
# Obtener token (reemplazar con tus credenciales)
$token = "TU_JWT_TOKEN"

# Listar tipos de cambio
curl -H "Authorization: Bearer $token" http://localhost:8080/api/exchange-rates
```

#### Tarjetas
```powershell
# Listar tarjetas
curl -H "Authorization: Bearer $token" http://localhost:8080/api/credit-cards
```

#### Cuotas
```powershell
# Listar cuotas pendientes
curl -H "Authorization: Bearer $token" http://localhost:8080/api/installments/unpaid
```

### 9.2 Test Frontend

- [ ] Login funciona
- [ ] Dashboard carga
- [ ] Menú muestra nuevas opciones
- [ ] Página Tarjetas carga
- [ ] Página Cuotas carga
- [ ] Página TC carga
- [ ] Crear tarjeta funciona
- [ ] Crear tipo de cambio funciona
- [ ] Crear transacción con cuotas funciona
- [ ] Ver cuotas generadas funciona
- [ ] Marcar cuota como pagada funciona

---

## 📋 PASO 10: Monitoreo

### Logs Backend
```powershell
# Con Docker
docker-compose logs -f backend

# Sin Docker (archivo de logs si está configurado)
tail -f backend/logs/application.log
```

### Logs Base de Datos
```powershell
# Ver actividad de PostgreSQL
psql -U postgres -d finanza -c "SELECT * FROM pg_stat_activity;"
```

### Verificar Uso de Memoria
```powershell
# Con Docker
docker stats

# Sin Docker
# Usar Task Manager o herramienta de monitoreo
```

---

## 🔧 Troubleshooting

### Error: "relation does not exist"

**Causa**: Migración no se aplicó correctamente

**Solución**:
```powershell
# Re-ejecutar migración
psql -U postgres -d finanza -f backend\migration-argentina-support.sql
```

### Error: "Cannot connect to database"

**Causa**: PostgreSQL no está corriendo

**Solución**:
```powershell
# Iniciar servicio de PostgreSQL
net start postgresql-x64-14  # Ajustar versión según instalación
```

### Error: Backend no compila

**Causa**: Dependencias faltantes

**Solución**:
```powershell
cd backend
mvn clean install -U
```

### Error: Frontend no carga

**Causa**: Build incompleto

**Solución**:
```powershell
cd frontend
rm -rf node_modules
npm install
npm run build
```

### Error: "Tipo de cambio no encontrado"

**Causa**: No hay tipos de cambio configurados

**Solución**:
1. Ir a **TC** en la UI
2. Agregar tipos de cambio para el día actual

### Error: Cuotas no se generan

**Verificar**:
1. Transacción tiene `creditCardId`
2. Campo `installments` > 1
3. Revisar logs del backend

---

## 📊 Verificación Post-Deployment

### Checklist Completo

#### Base de Datos
- [ ] 4 tablas nuevas creadas
- [ ] Tabla `transactions` tiene 5 campos nuevos
- [ ] Índices creados correctamente
- [ ] Constraints funcionando
- [ ] Datos de ejemplo insertados (opcional)

#### Backend
- [ ] Compila sin errores
- [ ] Inicia correctamente
- [ ] 16 endpoints nuevos responden
- [ ] Logs no muestran errores críticos
- [ ] Health check pasa

#### Frontend
- [ ] 3 páginas nuevas cargan
- [ ] Navbar muestra 3 enlaces nuevos
- [ ] Rutas funcionan correctamente
- [ ] No hay errores en consola del navegador
- [ ] Diseño responsive funciona

#### Funcionalidades
- [ ] Crear tipo de cambio funciona
- [ ] Crear tarjeta funciona
- [ ] Crear transacción con cuotas funciona
- [ ] Ver cuotas funciona
- [ ] Marcar cuota pagada funciona
- [ ] Conversión USD→ARS funciona
- [ ] Cálculo de deuda funciona

---

## 🎯 Performance

### Optimizaciones Aplicadas

1. **Índices de Base de Datos**
   - `idx_exchange_rates_org_date`
   - `idx_credit_cards_org`
   - `idx_installments_due_date`
   - Y más...

2. **Lazy Loading**
   - Relaciones JPA configuradas como LAZY
   - Evita N+1 queries

3. **Paginación**
   - Endpoints con soporte de paginación
   - Reduce carga en listas grandes

---

## 🔐 Seguridad

### Verificaciones

- [ ] Todos los endpoints requieren autenticación
- [ ] Validación de organización en queries
- [ ] Constraints de BD protegen integridad
- [ ] Validación de input en DTOs
- [ ] CORS configurado correctamente

---

## 📈 Métricas de Éxito

### Después del deployment, verifica:

1. **Tiempo de respuesta API** < 200ms
2. **Tiempo de carga páginas** < 2s
3. **Tasa de error** < 1%
4. **Uso de CPU** < 50%
5. **Uso de RAM** < 2GB

---

## 🔄 Rollback Plan

Si algo sale mal:

### 1. Detener Servicios
```powershell
docker-compose down
# O detener manualmente
```

### 2. Restaurar Base de Datos
```powershell
# Restaurar desde backup
pg_restore -U postgres -d finanza -c finanza_backup_FECHA.backup
```

### 3. Revertir Código
```powershell
# Si usas Git
git checkout HEAD~1  # O commit anterior

# Recompilar
cd backend
mvn clean package
cd ../frontend
npm run build
```

### 4. Re-iniciar Servicios
```powershell
docker-compose up -d
# O iniciar manualmente
```

---

## 📞 Post-Deployment

### Comunicación a Usuarios

**Asunto**: Nueva funcionalidad disponible - Soporte Argentina

**Mensaje**:
```
Hola,

Nos complace informar que el sistema Finanza ahora incluye soporte
completo para Argentina:

✨ Nuevas funcionalidades:
- Gestión de tarjetas de crédito
- Seguimiento de cuotas
- Tipos de cambio (Oficial, Blue, MEP, Tarjeta)
- Registro de impuestos argentinos (PAIS, Percepciones)
- Transacciones en USD con conversión automática

📱 Accede a través del menú:
- Tarjetas
- Cuotas
- TC (Tipos de Cambio)

📚 Documentación disponible en:
- SOPORTE_ARGENTINA.md
- INICIO_RAPIDO_ARGENTINA.md

¡Comienza a usar las nuevas funcionalidades hoy!

Equipo Finanza
```

---

## ✅ Deployment Completado

**Fecha**: ___________  
**Hora**: ___________  
**Responsable**: ___________  
**Versión deployada**: 1.0.0  
**Estado**: ___________

### Notas:
_____________________________________
_____________________________________
_____________________________________

---

**¡Deployment exitoso! 🎉**

El sistema está ahora en producción con todas las funcionalidades de soporte para Argentina.
