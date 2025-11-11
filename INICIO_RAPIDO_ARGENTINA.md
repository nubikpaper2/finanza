# 🚀 Inicio Rápido - Soporte Argentina

## ⚡ Configuración en 5 Minutos

### 1. Aplicar Migración de Base de Datos

```powershell
# Conectar a PostgreSQL y ejecutar migración
psql -U postgres -d finanza -f backend\migration-argentina-support.sql
```

### 2. Compilar Backend

```powershell
cd backend
mvn clean package -DskipTests
```

### 3. Iniciar Sistema

```powershell
# Opción A: Con Docker
docker-compose up -d

# Opción B: Sin Docker
.\start-without-docker.ps1
```

### 4. Acceder a la Aplicación

Abrir navegador en: `http://localhost:5173`

---

## 📝 Primer Uso

### Paso 1: Configurar Tipos de Cambio

1. Login con tu usuario
2. Ir a **TC** (Tipos de Cambio)
3. Agregar cotizaciones del día:

```
Dólar Oficial: 
- Compra: 1000
- Venta: 1010

Dólar Blue:
- Compra: 1150
- Venta: 1170

Dólar MEP:
- Compra: 1100
- Venta: 1110

Dólar Tarjeta:
- Compra: 1750
- Venta: 1760
```

### Paso 2: Crear una Tarjeta

1. Ir a **Tarjetas**
2. Click **+ Nueva Tarjeta**
3. Completar:
   ```
   Nombre: Visa Platinum
   Banco: Banco Galicia
   Últimos 4 dígitos: 1234
   Día de cierre: 10
   Día de vencimiento: 25
   Límite: 500000
   Moneda: ARS
   ```

### Paso 3: Registrar una Compra en Cuotas

1. Ir a **Transacciones**
2. Click **+ Nueva Transacción**
3. Llenar:
   ```
   Tipo: Gasto
   Monto: 60000
   Moneda: ARS
   Fecha: Hoy
   Descripción: Notebook
   Cuenta: Tu cuenta
   Categoría: Tecnología
   Tarjeta: Visa Platinum
   Cuotas: 6
   ```
4. Guardar

### Paso 4: Ver las Cuotas Generadas

1. Ir a **Cuotas**
2. Ver las 6 cuotas de $10,000 cada una
3. Marcar la primera como pagada

---

## 🎯 Ejemplos Prácticos

### Compra Internacional con Impuestos

**Escenario**: Compra en Amazon de USD 100

1. **Transacción**:
   - Monto: 100 USD
   - Fecha: Hoy
   - Descripción: "Libro en Amazon"
   - Tarjeta: Visa Platinum
   - Cuotas: 3

2. **Impuestos** (agregar en la misma transacción):
   - Impuesto PAIS: 30% = USD 30
   - Percepción RG 5371: 45% = USD 45
   
3. **Total**: USD 175 (100 + 30 + 45)

4. **En ARS** (con TC Tarjeta $1750):
   - Total: $306,250
   - Por cuota: $102,083.33

### Gasto Recurrente (Netflix)

**Cada mes**:
1. Crear transacción de USD 15.99
2. Agregar impuestos:
   - PAIS: 30% = USD 4.80
   - Percepción: 45% = USD 7.20
3. Total: USD 27.99
4. En ARS (TC Tarjeta): ~$48,983

---

## 📊 Verificar que Funciona

### Test 1: Tipo de Cambio
```bash
curl http://localhost:8080/api/exchange-rates/latest/OFICIAL \
  -H "Authorization: Bearer TU_TOKEN"
```

Debería retornar el tipo de cambio oficial.

### Test 2: Tarjetas
```bash
curl http://localhost:8080/api/credit-cards \
  -H "Authorization: Bearer TU_TOKEN"
```

Debería listar tus tarjetas.

### Test 3: Cuotas Pendientes
```bash
curl http://localhost:8080/api/installments/unpaid \
  -H "Authorization: Bearer TU_TOKEN"
```

Debería mostrar cuotas sin pagar.

---

## 🐛 Solución de Problemas

### Error: "Tipo de cambio no encontrado"

**Solución**: Agregar tipos de cambio para la fecha de la transacción en USD.

### Error: "Tarjeta no encontrada"

**Solución**: Verificar que la tarjeta existe y está activa.

### Cuotas no se generan

**Verificar**:
1. Transacción tiene `creditCardId`
2. Campo `installments` es > 1
3. Revisar logs del backend

### Migración falla

```powershell
# Verificar que la base de datos existe
psql -U postgres -l | findstr finanza

# Si no existe, crearla
psql -U postgres -c "CREATE DATABASE finanza;"

# Ejecutar migración nuevamente
psql -U postgres -d finanza -f backend\migration-argentina-support.sql
```

---

## 📱 Navegación Rápida

- **Dashboard**: Vista general
- **Transacciones**: Registrar gastos/ingresos
- **Tarjetas**: Gestionar tarjetas de crédito
- **Cuotas**: Ver y marcar cuotas pagadas
- **TC**: Configurar tipos de cambio
- **Categorías**: Organizar gastos
- **Presupuestos**: Controlar gastos mensuales
- **Reportes**: Análisis de gastos

---

## 🎨 Tips de Uso

### Actualizar TC Diariamente
Crear un recordatorio para actualizar los tipos de cambio cada día hábil.

### Marcar Cuotas Pagadas
Al pagar el resumen de la tarjeta, marcar todas las cuotas correspondientes.

### Categorizar Compras
Asignar categorías a todas las transacciones para mejores reportes.

### Usar Tags
Etiquetar compras relacionadas (ej: "viaje-miami", "regalo-cumple").

### Revisar Cuotas Futuras
Mensualmente, revisar las cuotas próximas a vencer en los próximos 3 meses.

---

## 🔄 Flujo de Trabajo Recomendado

### Semanal
1. Registrar todas las transacciones de la semana
2. Categorizar correctamente
3. Verificar cuotas próximas

### Mensual
1. Actualizar tipos de cambio
2. Revisar resumen de tarjetas
3. Marcar cuotas pagadas
4. Generar reportes
5. Ajustar presupuestos si es necesario

### Trimestral
1. Analizar tendencias de gastos
2. Revisar impuestos pagados
3. Ajustar estrategia de pagos

---

## 📞 Ayuda Adicional

Para más información consultar:
- `SOPORTE_ARGENTINA.md` - Documentación completa
- `API_EXAMPLES.md` - Ejemplos de API
- `README.md` - Documentación general

---

¡Listo para empezar! 🎉
