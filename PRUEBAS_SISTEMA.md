# 🧪 Pruebas Rápidas del Sistema de Movimientos

## Configuración Inicial

1. **Iniciar Backend y Frontend**:
```bash
# Terminal 1 - Backend
cd backend
mvn spring-boot:run

# Terminal 2 - Frontend
cd frontend
npm run dev
```

2. **Acceder a la aplicación**:
- URL: http://localhost:3000
- Usuario: admin@demo.com
- Password: admin123

## 📝 Casos de Prueba

### 1. Ver Categorías Predefinidas
1. Ir a "Categorías"
2. Verificar que hay categorías de ejemplo
3. Filtrar por tipo (Ingresos/Gastos)
4. Observar iconos y colores

**Resultado esperado**: 15+ categorías con iconos coloridos

### 2. Crear Nueva Categoría
1. Click en "+ Nueva Categoría"
2. Llenar formulario:
   - Nombre: "Freelance"
   - Tipo: Ingreso
   - Icono: 💻
   - Color: #3B82F6
   - Descripción: "Trabajos independientes"
3. Guardar

**Resultado esperado**: Categoría creada y visible en el listado

### 3. Crear Transacción de Ingreso
1. Ir a "Transacciones"
2. Click en "+ Nueva Transacción"
3. Llenar formulario:
   - Tipo: Ingreso
   - Monto: 1500.00
   - Fecha: Hoy
   - Categoría: Ventas
   - Descripción: "Venta de producto"
4. Guardar

**Resultado esperado**: 
- Transacción creada
- Aparece en la tabla con badge verde
- El saldo de la cuenta aumentó en $1500

### 4. Crear Transacción de Gasto
1. Click en "+ Nueva Transacción"
2. Llenar formulario:
   - Tipo: Gasto
   - Monto: 300.00
   - Fecha: Hoy
   - Categoría: Oficina
   - Descripción: "Compra de suministros"
   - Notas: "Papelería y tinta"
3. Guardar

**Resultado esperado**: 
- Transacción creada con badge rojo
- El saldo de la cuenta disminuyó en $300

### 5. Verificar Actualización de Saldos

**Comprobar en Backend**:
```bash
# Conectar a PostgreSQL
psql -U finanza_user -d finanza_db

# Ver saldos de cuentas
SELECT id, name, balance FROM accounts;

# Ver transacciones
SELECT id, type, amount, transaction_date, description 
FROM transactions 
ORDER BY transaction_date DESC 
LIMIT 10;
```

**Resultado esperado**: 
- Cuenta tiene balance actualizado
- Transacciones registradas correctamente

### 6. Filtrar Transacciones
1. En vista de Transacciones
2. Usar filtros:
   - Fecha Inicio: Primer día del mes
   - Fecha Fin: Hoy
   - Categoría: (seleccionar una)
   - Tipo: Gastos
3. Aplicar filtros

**Resultado esperado**: Solo transacciones que coincidan con filtros

### 7. Editar Transacción
1. Click en "Editar" en una transacción
2. Cambiar monto de 300 a 250
3. Guardar

**Resultado esperado**: 
- Monto actualizado en tabla
- Saldo de cuenta recalculado (ajustado por diferencia de $50)

### 8. Crear Transferencia Entre Cuentas

**API Request** (usar Postman/Insomnia):
```bash
POST http://localhost:8080/api/transactions/transfer
Authorization: Bearer {TOKEN}
Content-Type: application/json

{
  "fromAccountId": 1,
  "toAccountId": 2,
  "amount": 500.00,
  "transactionDate": "2024-11-10",
  "description": "Transferencia a ahorros"
}
```

**Resultado esperado**: 
- Cuenta origen: saldo - 500
- Cuenta destino: saldo + 500
- Transacción tipo TRANSFER creada

### 9. Eliminar Transacción
1. Click en "Eliminar" en una transacción
2. Confirmar eliminación

**Resultado esperado**: 
- Transacción eliminada
- Saldo de cuenta revertido

### 10. Probar Tags y Adjuntos

**API Request**:
```bash
POST http://localhost:8080/api/transactions
Authorization: Bearer {TOKEN}
Content-Type: application/json

{
  "type": "EXPENSE",
  "amount": 100.00,
  "transactionDate": "2024-11-10",
  "description": "Compra con tags",
  "accountId": 1,
  "categoryId": 5,
  "tags": ["urgente", "oficina", "Q4-2024"],
  "attachments": ["https://ejemplo.com/factura.pdf", "local://documentos/recibo.jpg"]
}
```

**Resultado esperado**: 
- Transacción creada con tags
- Tags almacenados en tabla `transaction_tags`
- Attachments en tabla `transaction_attachments`

## 🔍 Verificaciones de Base de Datos

```sql
-- Ver todas las categorías
SELECT * FROM categories ORDER BY type, name;

-- Ver transacciones con categoría
SELECT 
  t.id,
  t.type,
  t.amount,
  t.transaction_date,
  t.description,
  c.name as category_name,
  a.name as account_name,
  a.balance as current_balance
FROM transactions t
LEFT JOIN categories c ON t.category_id = c.id
LEFT JOIN accounts a ON t.account_id = a.id
ORDER BY t.transaction_date DESC;

-- Ver tags de una transacción
SELECT * FROM transaction_tags WHERE transaction_id = 1;

-- Ver adjuntos de una transacción
SELECT * FROM transaction_attachments WHERE transaction_id = 1;

-- Verificar saldos (suma de transacciones debe coincidir)
SELECT 
  a.id,
  a.name,
  a.balance as current_balance,
  COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END), 0) as total_income,
  COALESCE(SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END), 0) as total_expense
FROM accounts a
LEFT JOIN transactions t ON t.account_id = a.id
GROUP BY a.id, a.name, a.balance;
```

## 📊 Datos de Prueba Sugeridos

### Categorías Adicionales
- "Comisiones" (Ingreso) 💳 #8B5CF6
- "Bonos" (Ingreso) 🎁 #F59E0B
- "Seguros" (Gasto) 🛡️ #EF4444
- "Internet" (Gasto) 🌐 #3B82F6

### Transacciones de Prueba
```json
[
  {
    "type": "INCOME",
    "amount": 5000.00,
    "transactionDate": "2024-11-01",
    "description": "Salario mensual",
    "accountId": 2,
    "categoryId": 1
  },
  {
    "type": "EXPENSE",
    "amount": 1200.00,
    "transactionDate": "2024-11-05",
    "description": "Alquiler",
    "accountId": 2,
    "categoryId": 6
  },
  {
    "type": "EXPENSE",
    "amount": 150.00,
    "transactionDate": "2024-11-07",
    "description": "Supermercado",
    "accountId": 1,
    "categoryId": 10,
    "tags": ["comida", "mensual"]
  },
  {
    "type": "INCOME",
    "amount": 800.00,
    "transactionDate": "2024-11-10",
    "description": "Proyecto freelance",
    "accountId": 2,
    "categoryId": 2
  }
]
```

## ✅ Checklist de Pruebas

- [ ] Backend inicia sin errores
- [ ] Frontend se carga correctamente
- [ ] Login funciona con credenciales demo
- [ ] Categorías se listan correctamente
- [ ] Crear categoría funciona
- [ ] Editar categoría funciona
- [ ] Eliminar categoría funciona (soft delete)
- [ ] Transacciones se listan con paginación
- [ ] Crear transacción de ingreso actualiza saldo (+)
- [ ] Crear transacción de gasto actualiza saldo (-)
- [ ] Editar transacción recalcula saldo
- [ ] Eliminar transacción revierte saldo
- [ ] Filtros de fecha funcionan
- [ ] Filtros de categoría funcionan
- [ ] Filtros de tipo funcionan
- [ ] Paginación funciona
- [ ] Transferencia entre cuentas funciona (API)
- [ ] Tags se guardan correctamente
- [ ] Adjuntos se guardan correctamente
- [ ] Validaciones funcionan (saldo insuficiente, campos requeridos)

## 🎯 Criterios de Éxito

1. ✅ Se pueden registrar ingresos, gastos y transferencias
2. ✅ Los saldos de las cuentas se actualizan automáticamente
3. ✅ Las categorías permiten organizar las transacciones
4. ✅ Los filtros permiten buscar transacciones específicas
5. ✅ La interfaz es intuitiva y responsive
6. ✅ No hay errores en consola
7. ✅ Los datos persisten en PostgreSQL
8. ✅ Las validaciones previenen datos incorrectos

## 🐛 Problemas Conocidos

Ninguno reportado hasta el momento. El sistema está completamente funcional.

## 📞 Soporte

Si encuentras algún problema:
1. Verifica que PostgreSQL esté corriendo
2. Verifica las credenciales de base de datos
3. Revisa los logs del backend
4. Revisa la consola del navegador
