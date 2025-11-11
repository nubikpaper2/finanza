# 🇦🇷 Soporte Argentina - Guía Completa

## 📋 Tabla de Contenidos
1. [Características Implementadas](#características-implementadas)
2. [Modelos de Datos](#modelos-de-datos)
3. [API Endpoints](#api-endpoints)
4. [Guía de Uso](#guía-de-uso)
5. [Ejemplos](#ejemplos)

---

## ✅ Características Implementadas

### 💵 Múltiples Monedas
- Soporte para ARS (Peso Argentino) y USD (Dólar)
- Conversión automática usando tipos de cambio configurables
- Almacenamiento dual: monto original + monto en moneda local

### 📊 Tipos de Cambio
- **Dólar Oficial**: Tipo de cambio del Banco Central
- **Dólar Blue**: Mercado paralelo
- **Dólar MEP**: Mercado Electrónico de Pagos
- **Dólar Tarjeta**: Oficial + impuestos (PAIS + Percepciones)

Características:
- Registro histórico diario
- Cotización compra/venta
- Cálculo automático de promedio
- Obtención del tipo de cambio más reciente

### 💳 Tarjetas de Crédito
- Gestión completa de tarjetas
- Configuración de:
  - Día de cierre
  - Día de vencimiento
  - Límite de crédito
  - Banco emisor
  - Últimos 4 dígitos

- Cálculo automático:
  - Deuda actual
  - Crédito disponible
  
### 📅 Sistema de Cuotas
- Compras en cuotas automáticas
- Cálculo inteligente de vencimientos
- Estados: Pendiente, Pagada, Vencida
- Reportes de cuotas futuras
- Marcado de pago/impago

### 💰 Impuestos Argentinos
Soporte para:
- **Impuesto PAIS** (30%)
- **Percepción RG 5371** (45%)
- **Percepción RG 4815** (35%)
- **IVA** (21%)
- **Ingresos Brutos**
- **Otros impuestos personalizados**

---

## 🗄️ Modelos de Datos

### ExchangeRate (Tipo de Cambio)
```java
{
  "id": Long,
  "date": LocalDate,
  "rateType": "OFICIAL|BLUE|MEP|TARJETA",
  "buyRate": BigDecimal,
  "sellRate": BigDecimal,
  "averageRate": BigDecimal (calculado)
}
```

### CreditCard (Tarjeta de Crédito)
```java
{
  "id": Long,
  "name": String,
  "lastFourDigits": String,
  "closingDay": Integer (1-31),
  "dueDay": Integer (1-31),
  "creditLimit": BigDecimal,
  "currency": "ARS|USD",
  "bank": String,
  "active": Boolean,
  "currentDebt": BigDecimal (calculado),
  "availableCredit": BigDecimal (calculado)
}
```

### CreditCardInstallment (Cuota)
```java
{
  "id": Long,
  "transactionId": Long,
  "creditCardId": Long,
  "installmentNumber": Integer,
  "totalInstallments": Integer,
  "amount": BigDecimal,
  "dueDate": LocalDate,
  "paid": Boolean,
  "paidDate": LocalDate
}
```

### ArgentinaTax (Impuesto)
```java
{
  "id": Long,
  "transactionId": Long,
  "taxType": "PAIS|PERCEPCION_RG_5371|...",
  "percentage": BigDecimal,
  "amount": BigDecimal,
  "description": String
}
```

---

## 🔌 API Endpoints

### Tipos de Cambio

#### `POST /api/exchange-rates`
Crear o actualizar tipo de cambio
```json
{
  "date": "2025-11-11",
  "rateType": "OFICIAL",
  "buyRate": 1000.50,
  "sellRate": 1010.50
}
```

#### `GET /api/exchange-rates/date/{date}`
Obtener todos los tipos de cambio de una fecha

#### `GET /api/exchange-rates/latest/{rateType}`
Obtener el tipo de cambio más reciente

#### `GET /api/exchange-rates/range`
Obtener tipos de cambio en un rango
```
?startDate=2025-11-01&endDate=2025-11-11&rateType=OFICIAL
```

---

### Tarjetas de Crédito

#### `POST /api/credit-cards`
Crear tarjeta
```json
{
  "name": "Visa Platinum",
  "lastFourDigits": "1234",
  "closingDay": 10,
  "dueDay": 25,
  "creditLimit": 500000,
  "currency": "ARS",
  "bank": "Banco Galicia",
  "active": true
}
```

#### `GET /api/credit-cards`
Listar todas las tarjetas
- Query param: `?activeOnly=true` para solo activas

#### `GET /api/credit-cards/{id}`
Obtener detalles de una tarjeta

#### `PUT /api/credit-cards/{id}`
Actualizar tarjeta

#### `DELETE /api/credit-cards/{id}`
Eliminar tarjeta

---

### Cuotas

#### `GET /api/installments/unpaid`
Obtener todas las cuotas pendientes

#### `GET /api/installments/upcoming`
Obtener cuotas próximas
```
?startDate=2025-11-01&endDate=2026-02-01
```

#### `GET /api/installments/credit-card/{creditCardId}`
Obtener cuotas de una tarjeta específica

#### `PATCH /api/installments/{id}/pay`
Marcar cuota como pagada

#### `PATCH /api/installments/{id}/unpay`
Marcar cuota como impaga

---

### Transacciones (Actualizado)

#### `POST /api/transactions`
Crear transacción con soporte completo
```json
{
  "type": "EXPENSE",
  "amount": 15000,
  "currency": "ARS",
  "transactionDate": "2025-11-11",
  "description": "Compra Amazon",
  "accountId": 1,
  "categoryId": 5,
  "creditCardId": 2,
  "installments": 6,
  "taxes": [
    {
      "taxType": "PAIS",
      "percentage": 30,
      "amount": 4500,
      "description": "Impuesto PAIS 30%"
    },
    {
      "taxType": "PERCEPCION_RG_5371",
      "percentage": 45,
      "amount": 6750,
      "description": "Percepción Ganancias 45%"
    }
  ]
}
```

Para transacciones en USD:
```json
{
  "type": "EXPENSE",
  "amount": 100,
  "currency": "USD",
  "exchangeRate": 1050.50,
  "transactionDate": "2025-11-11",
  "description": "Compra internacional",
  "accountId": 1
}
```

---

## 📖 Guía de Uso

### 1. Configurar Tipos de Cambio

Antes de registrar transacciones en USD, configure los tipos de cambio:

1. Ir a **TC** en el menú
2. Hacer clic en **+ Nuevo Tipo de Cambio**
3. Completar:
   - Fecha
   - Tipo (Oficial, Blue, MEP, Tarjeta)
   - Compra y Venta
4. Guardar

💡 **Tip**: Puede actualizar tipos de cambio existentes enviando nuevamente con la misma fecha y tipo.

### 2. Crear Tarjetas de Crédito

1. Ir a **Tarjetas** en el menú
2. Hacer clic en **+ Nueva Tarjeta**
3. Completar:
   - Nombre
   - Banco
   - Últimos 4 dígitos
   - Día de cierre (ej: 10)
   - Día de vencimiento (ej: 25)
   - Límite de crédito
4. Guardar

### 3. Registrar Compra en Cuotas

1. Ir a **Transacciones**
2. Nueva transacción tipo **Gasto**
3. Seleccionar:
   - Tarjeta de crédito
   - Número de cuotas
4. El sistema automáticamente:
   - Divide el monto
   - Calcula fechas de vencimiento
   - Crea las cuotas

### 4. Gestionar Cuotas

1. Ir a **Cuotas** en el menú
2. Ver cuotas:
   - Pendientes
   - Próximas (3 meses)
   - Por tarjeta
3. Marcar como pagada/impaga según corresponda

### 5. Registrar Transacción con Impuestos

Ejemplo: Compra internacional de USD 100

1. Crear transacción:
   - Monto: 100 USD
   - El sistema aplica TC automáticamente
   
2. Agregar impuestos:
   - PAIS 30%: $31.50 (sobre USD 100 × TC)
   - RG 5371 45%: $47.25
   
3. Total en ARS = USD 100 × TC + Impuestos

---

## 💡 Ejemplos Prácticos

### Ejemplo 1: Compra en Amazon con Tarjeta

**Escenario**: Compra de USD 150 en Amazon con tarjeta Visa, 3 cuotas

```json
POST /api/transactions
{
  "type": "EXPENSE",
  "amount": 150,
  "currency": "USD",
  "transactionDate": "2025-11-11",
  "description": "Compra Amazon - Libro Kindle",
  "categoryId": 8,
  "creditCardId": 1,
  "installments": 3,
  "taxes": [
    {
      "taxType": "PAIS",
      "percentage": 30,
      "amount": 47.25,
      "description": "Impuesto PAIS"
    },
    {
      "taxType": "PERCEPCION_RG_5371",
      "percentage": 45,
      "amount": 70.875,
      "description": "Percepción Ganancias"
    }
  ]
}
```

**Resultado**:
- Se crea la transacción
- Se generan 3 cuotas de USD 50 c/u
- Cada cuota vence según el cierre de la tarjeta
- Se registran los impuestos

### Ejemplo 2: Actualizar Tipos de Cambio Diarios

```json
POST /api/exchange-rates
[
  {
    "date": "2025-11-11",
    "rateType": "OFICIAL",
    "buyRate": 1000.50,
    "sellRate": 1010.50
  },
  {
    "date": "2025-11-11",
    "rateType": "BLUE",
    "buyRate": 1150.00,
    "sellRate": 1170.00
  },
  {
    "date": "2025-11-11",
    "rateType": "TARJETA",
    "buyRate": 1750.00,
    "sellRate": 1760.00
  }
]
```

### Ejemplo 3: Consultar Cuotas Próximas a Vencer

```bash
GET /api/installments/upcoming?startDate=2025-11-01&endDate=2025-12-31
```

**Respuesta**:
```json
[
  {
    "id": 45,
    "creditCardName": "Visa Platinum",
    "transactionDescription": "Compra Amazon",
    "installmentNumber": 2,
    "totalInstallments": 3,
    "amount": 50000,
    "dueDate": "2025-11-25",
    "paid": false
  },
  {
    "id": 46,
    "creditCardName": "Mastercard Black",
    "transactionDescription": "Notebook Dell",
    "installmentNumber": 5,
    "totalInstallments": 12,
    "amount": 83333.33,
    "dueDate": "2025-12-10",
    "paid": false
  }
]
```

---

## 🎯 Casos de Uso

### Caso 1: Netflix en USD
- Cargo mensual de USD 15.99
- Tarjeta: Visa
- Impuestos: PAIS (30%) + Percepción (45%)
- Total ARS ≈ $27,983 (con TC tarjeta ~$1000)

### Caso 2: Compra en 12 Cuotas
- Notebook: $600,000
- 12 cuotas sin interés
- Cuota: $50,000
- Vencimiento: Día 25 de cada mes

### Caso 3: Viaje Internacional
- Hotel USD 500
- Alquiler auto USD 300
- TC MEP: $1100
- Total ARS: $880,000 + impuestos

---

## 🔧 Configuración Técnica

### Migración de Base de Datos

Ejecutar el script de migración:

```bash
psql -U usuario -d finanza -f backend/migration-argentina-support.sql
```

### Variables de Entorno

No requiere configuración adicional. El sistema está listo para usar.

---

## 📱 Interfaz de Usuario

### Pantallas Disponibles

1. **Tarjetas** (`/credit-cards`)
   - Vista tipo "tarjeta de crédito"
   - Muestra límite, uso y disponible
   - Estados: Activa/Inactiva

2. **Cuotas** (`/installments`)
   - Tabla con todas las cuotas
   - Filtros por estado y tarjeta
   - Estados visuales: Pendiente/Pagada/Vencida

3. **Tipos de Cambio** (`/exchange-rates`)
   - Agrupados por fecha
   - Vista de 4 tipos simultáneos
   - Compra, venta y promedio

---

## ⚡ Funcionalidades Automáticas

1. **Conversión de Moneda**
   - Al crear transacción USD, busca TC del día
   - Si no existe, usa el más reciente
   - Guarda monto original + convertido

2. **Generación de Cuotas**
   - Calcula vencimientos basado en cierre de tarjeta
   - Divide monto equitativamente
   - Ajusta al último día del mes si es necesario

3. **Cálculo de Deuda**
   - Suma cuotas impagas por tarjeta
   - Actualiza crédito disponible
   - Alerta de cuotas vencidas

---

## 🎨 Mejoras Futuras

- [ ] Importar tipos de cambio automáticamente desde API
- [ ] Alertas de vencimiento de cuotas
- [ ] Gráficos de evolución del tipo de cambio
- [ ] Reportes de impuestos pagados
- [ ] Integración con bancos para saldos reales
- [ ] Proyección de gastos en cuotas
- [ ] Notificaciones de cierre de tarjeta

---

## 📞 Soporte

Para consultas o problemas:
- Revisar logs del backend
- Verificar migración de base de datos
- Consultar documentación de API

---

**Versión**: 1.0.0  
**Fecha**: Noviembre 2025  
**Estado**: ✅ Producción
