# 🧪 Ejemplos de API - Soporte Argentina

Colección completa de ejemplos para testing con cURL y Postman.

---

## 🔐 Autenticación

Primero obtener token:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "user": {
    "id": 1,
    "firstName": "Juan",
    "lastName": "Pérez",
    "email": "user@example.com"
  }
}
```

**Guardar el token** para usarlo en las siguientes requests:
```bash
export TOKEN="eyJhbGciOiJIUzI1NiJ9..."
```

---

## 📊 Tipos de Cambio

### Crear/Actualizar Tipo de Cambio

```bash
curl -X POST http://localhost:8080/api/exchange-rates \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "date": "2025-11-11",
    "rateType": "OFICIAL",
    "buyRate": 1000.50,
    "sellRate": 1010.50
  }'
```

**Respuesta:**
```json
{
  "id": 1,
  "date": "2025-11-11",
  "rateType": "OFICIAL",
  "buyRate": 1000.50,
  "sellRate": 1010.50,
  "averageRate": 1005.50
}
```

### Crear Múltiples Tipos (Batch)

```bash
# Dólar Blue
curl -X POST http://localhost:8080/api/exchange-rates \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "date": "2025-11-11",
    "rateType": "BLUE",
    "buyRate": 1150.00,
    "sellRate": 1170.00
  }'

# Dólar MEP
curl -X POST http://localhost:8080/api/exchange-rates \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "date": "2025-11-11",
    "rateType": "MEP",
    "buyRate": 1100.00,
    "sellRate": 1110.00
  }'

# Dólar Tarjeta
curl -X POST http://localhost:8080/api/exchange-rates \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "date": "2025-11-11",
    "rateType": "TARJETA",
    "buyRate": 1750.00,
    "sellRate": 1760.00
  }'
```

### Obtener Tipos de Cambio del Día

```bash
curl -X GET "http://localhost:8080/api/exchange-rates/date/2025-11-11" \
  -H "Authorization: Bearer $TOKEN"
```

**Respuesta:**
```json
[
  {
    "id": 1,
    "date": "2025-11-11",
    "rateType": "OFICIAL",
    "buyRate": 1000.50,
    "sellRate": 1010.50,
    "averageRate": 1005.50
  },
  {
    "id": 2,
    "date": "2025-11-11",
    "rateType": "BLUE",
    "buyRate": 1150.00,
    "sellRate": 1170.00,
    "averageRate": 1160.00
  }
]
```

### Obtener Último Tipo de Cambio

```bash
curl -X GET "http://localhost:8080/api/exchange-rates/latest/OFICIAL" \
  -H "Authorization: Bearer $TOKEN"
```

### Obtener por Rango de Fechas

```bash
curl -X GET "http://localhost:8080/api/exchange-rates/range?startDate=2025-11-01&endDate=2025-11-11&rateType=OFICIAL" \
  -H "Authorization: Bearer $TOKEN"
```

### Listar Todos

```bash
curl -X GET "http://localhost:8080/api/exchange-rates" \
  -H "Authorization: Bearer $TOKEN"
```

### Eliminar Tipo de Cambio

```bash
curl -X DELETE "http://localhost:8080/api/exchange-rates/1" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 💳 Tarjetas de Crédito

### Crear Tarjeta

```bash
curl -X POST http://localhost:8080/api/credit-cards \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Visa Platinum",
    "lastFourDigits": "1234",
    "closingDay": 10,
    "dueDay": 25,
    "creditLimit": 500000,
    "currency": "ARS",
    "bank": "Banco Galicia",
    "active": true
  }'
```

**Respuesta:**
```json
{
  "id": 1,
  "name": "Visa Platinum",
  "lastFourDigits": "1234",
  "closingDay": 10,
  "dueDay": 25,
  "creditLimit": 500000,
  "currency": "ARS",
  "bank": "Banco Galicia",
  "active": true,
  "currentDebt": 0,
  "availableCredit": 500000
}
```

### Crear Otra Tarjeta

```bash
curl -X POST http://localhost:8080/api/credit-cards \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Mastercard Black",
    "lastFourDigits": "5678",
    "closingDay": 15,
    "dueDay": 5,
    "creditLimit": 1000000,
    "currency": "ARS",
    "bank": "Banco Santander",
    "active": true
  }'
```

### Listar Todas las Tarjetas

```bash
curl -X GET "http://localhost:8080/api/credit-cards" \
  -H "Authorization: Bearer $TOKEN"
```

### Listar Solo Activas

```bash
curl -X GET "http://localhost:8080/api/credit-cards?activeOnly=true" \
  -H "Authorization: Bearer $TOKEN"
```

### Obtener Tarjeta por ID

```bash
curl -X GET "http://localhost:8080/api/credit-cards/1" \
  -H "Authorization: Bearer $TOKEN"
```

### Actualizar Tarjeta

```bash
curl -X PUT "http://localhost:8080/api/credit-cards/1" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Visa Platinum Actualizada",
    "lastFourDigits": "1234",
    "closingDay": 12,
    "dueDay": 27,
    "creditLimit": 600000,
    "currency": "ARS",
    "bank": "Banco Galicia",
    "active": true
  }'
```

### Desactivar Tarjeta

```bash
curl -X PUT "http://localhost:8080/api/credit-cards/1" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Visa Platinum",
    "lastFourDigits": "1234",
    "closingDay": 10,
    "dueDay": 25,
    "creditLimit": 500000,
    "currency": "ARS",
    "bank": "Banco Galicia",
    "active": false
  }'
```

### Eliminar Tarjeta

```bash
curl -X DELETE "http://localhost:8080/api/credit-cards/1" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 💰 Transacciones con Tarjetas y Cuotas

### Compra en ARS con Cuotas

```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "type": "EXPENSE",
    "amount": 60000,
    "currency": "ARS",
    "transactionDate": "2025-11-11",
    "description": "Notebook Dell",
    "accountId": 1,
    "categoryId": 5,
    "creditCardId": 1,
    "installments": 6
  }'
```

**Resultado:**
- Se crea la transacción
- Se generan 6 cuotas de $10,000 cada una
- Vencimientos calculados según cierre de tarjeta

### Compra en USD con Impuestos y Cuotas

```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "type": "EXPENSE",
    "amount": 100,
    "currency": "USD",
    "transactionDate": "2025-11-11",
    "description": "Compra Amazon - Kindle",
    "accountId": 1,
    "categoryId": 8,
    "creditCardId": 1,
    "installments": 3,
    "taxes": [
      {
        "taxType": "PAIS",
        "percentage": 30,
        "amount": 30,
        "description": "Impuesto PAIS 30%"
      },
      {
        "taxType": "PERCEPCION_RG_5371",
        "percentage": 45,
        "amount": 45,
        "description": "Percepción Ganancias RG 5371"
      }
    ]
  }'
```

**Resultado:**
- Convierte USD a ARS usando TC del día
- Total: USD 175 (100 + 30 + 45)
- Genera 3 cuotas
- Registra impuestos por separado

### Compra Única sin Cuotas

```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "type": "EXPENSE",
    "amount": 15000,
    "currency": "ARS",
    "transactionDate": "2025-11-11",
    "description": "Supermercado",
    "accountId": 1,
    "categoryId": 3,
    "creditCardId": 1,
    "installments": 1
  }'
```

### Suscripción Mensual (ej: Netflix)

```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "type": "EXPENSE",
    "amount": 15.99,
    "currency": "USD",
    "transactionDate": "2025-11-11",
    "description": "Netflix Premium",
    "accountId": 1,
    "categoryId": 10,
    "creditCardId": 1,
    "taxes": [
      {
        "taxType": "PAIS",
        "percentage": 30,
        "amount": 4.80,
        "description": "Impuesto PAIS"
      },
      {
        "taxType": "PERCEPCION_RG_5371",
        "percentage": 45,
        "amount": 7.20,
        "description": "Percepción Ganancias"
      }
    ]
  }'
```

---

## 📅 Cuotas

### Listar Cuotas Pendientes

```bash
curl -X GET "http://localhost:8080/api/installments/unpaid" \
  -H "Authorization: Bearer $TOKEN"
```

**Respuesta:**
```json
[
  {
    "id": 1,
    "transactionId": 123,
    "transactionDescription": "Notebook Dell",
    "creditCardId": 1,
    "creditCardName": "Visa Platinum",
    "installmentNumber": 1,
    "totalInstallments": 6,
    "amount": 10000,
    "dueDate": "2025-11-25",
    "paid": false,
    "paidDate": null
  },
  {
    "id": 2,
    "transactionId": 123,
    "transactionDescription": "Notebook Dell",
    "creditCardId": 1,
    "creditCardName": "Visa Platinum",
    "installmentNumber": 2,
    "totalInstallments": 6,
    "amount": 10000,
    "dueDate": "2025-12-25",
    "paid": false,
    "paidDate": null
  }
]
```

### Listar Cuotas Próximas (3 meses)

```bash
curl -X GET "http://localhost:8080/api/installments/upcoming?startDate=2025-11-01&endDate=2026-02-01" \
  -H "Authorization: Bearer $TOKEN"
```

### Listar Cuotas de una Tarjeta

```bash
curl -X GET "http://localhost:8080/api/installments/credit-card/1" \
  -H "Authorization: Bearer $TOKEN"
```

### Marcar Cuota como Pagada

```bash
curl -X PATCH "http://localhost:8080/api/installments/1/pay" \
  -H "Authorization: Bearer $TOKEN"
```

**Respuesta:**
```json
{
  "id": 1,
  "transactionId": 123,
  "transactionDescription": "Notebook Dell",
  "creditCardId": 1,
  "creditCardName": "Visa Platinum",
  "installmentNumber": 1,
  "totalInstallments": 6,
  "amount": 10000,
  "dueDate": "2025-11-25",
  "paid": true,
  "paidDate": "2025-11-11"
}
```

### Marcar Cuota como Impaga

```bash
curl -X PATCH "http://localhost:8080/api/installments/1/unpay" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 🧪 Escenarios Completos de Prueba

### Escenario 1: Setup Inicial

```bash
# 1. Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "user@example.com", "password": "password123"}'

# 2. Crear tipos de cambio
curl -X POST http://localhost:8080/api/exchange-rates \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"date": "2025-11-11", "rateType": "OFICIAL", "buyRate": 1000, "sellRate": 1010}'

# 3. Crear tarjeta
curl -X POST http://localhost:8080/api/credit-cards \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"name": "Visa", "closingDay": 10, "dueDay": 25, "creditLimit": 500000, "currency": "ARS", "active": true}'
```

### Escenario 2: Compra Internacional

```bash
# 1. Compra en Amazon USD 150
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "type": "EXPENSE",
    "amount": 150,
    "currency": "USD",
    "transactionDate": "2025-11-11",
    "description": "Amazon - Auriculares",
    "accountId": 1,
    "categoryId": 5,
    "creditCardId": 1,
    "installments": 3,
    "taxes": [
      {"taxType": "PAIS", "percentage": 30, "amount": 45},
      {"taxType": "PERCEPCION_RG_5371", "percentage": 45, "amount": 67.5}
    ]
  }'

# 2. Ver cuotas generadas
curl -X GET "http://localhost:8080/api/installments/credit-card/1" \
  -H "Authorization: Bearer $TOKEN"

# 3. Marcar primera cuota como pagada
curl -X PATCH "http://localhost:8080/api/installments/1/pay" \
  -H "Authorization: Bearer $TOKEN"
```

### Escenario 3: Múltiples Tarjetas

```bash
# 1. Crear segunda tarjeta
curl -X POST http://localhost:8080/api/credit-cards \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"name": "Mastercard", "closingDay": 15, "dueDay": 5, "creditLimit": 800000, "currency": "ARS", "active": true}'

# 2. Compra con primera tarjeta
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"type": "EXPENSE", "amount": 30000, "currency": "ARS", "transactionDate": "2025-11-11", "description": "Compra 1", "accountId": 1, "creditCardId": 1, "installments": 6}'

# 3. Compra con segunda tarjeta
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"type": "EXPENSE", "amount": 50000, "currency": "ARS", "transactionDate": "2025-11-11", "description": "Compra 2", "accountId": 1, "creditCardId": 2, "installments": 12}'

# 4. Ver todas las cuotas pendientes
curl -X GET "http://localhost:8080/api/installments/unpaid" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 📊 Postman Collection

Importar este JSON en Postman para tener todos los endpoints:

```json
{
  "info": {
    "name": "Finanza - Argentina Support",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Exchange Rates",
      "item": [
        {
          "name": "Create Rate",
          "request": {
            "method": "POST",
            "header": [],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"date\": \"2025-11-11\",\n  \"rateType\": \"OFICIAL\",\n  \"buyRate\": 1000.50,\n  \"sellRate\": 1010.50\n}",
              "options": {
                "raw": {
                  "language": "json"
                }
              }
            },
            "url": "{{base_url}}/api/exchange-rates"
          }
        }
      ]
    }
  ]
}
```

---

## 🎯 Testing Checklist

- [ ] Autenticación funciona
- [ ] Crear tipo de cambio funciona
- [ ] Listar tipos de cambio funciona
- [ ] Crear tarjeta funciona
- [ ] Listar tarjetas funciona
- [ ] Crear transacción simple funciona
- [ ] Crear transacción con cuotas funciona
- [ ] Crear transacción USD funciona
- [ ] Cuotas se generan correctamente
- [ ] Marcar cuota pagada funciona
- [ ] Listar cuotas pendientes funciona
- [ ] Impuestos se registran correctamente

---

**¡Colección completa de ejemplos para testing! 🧪**
