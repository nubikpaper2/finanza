# 🎯 Sistema de Movimiento Básico de Dinero - Finanza

## ✅ Funcionalidades Implementadas

### Backend (Spring Boot)

#### 1. **CRUD Categorías** ✅
- **Modelo**: `Category.java`
  - Campos: id, name, description, type (INCOME/EXPENSE/TRANSFER), icon, color, active
  - Relaciones: Organization, User (createdBy)
  - Timestamps automáticos

- **Repository**: `CategoryRepository.java`
  - Búsqueda por organización
  - Filtrado por tipo
  - Búsqueda activas

- **Service**: `CategoryService.java`
  - CRUD completo con validaciones
  - Verificación de nombres duplicados
  - Soft delete (desactivación)

- **Controller**: `CategoryController.java`
  - `GET /api/categories` - Listar todas
  - `GET /api/categories/type/{type}` - Filtrar por tipo
  - `GET /api/categories/{id}` - Obtener por ID
  - `POST /api/categories` - Crear nueva
  - `PUT /api/categories/{id}` - Actualizar
  - `DELETE /api/categories/{id}` - Eliminar (soft delete)

#### 2. **CRUD Transacciones** ✅
- **Modelo**: `Transaction.java`
  - Campos: id, type, amount, transactionDate, description, notes
  - Relaciones: Account, Category, DestinationAccount (para transferencias), Organization, User
  - **Tags**: Set de etiquetas para organización
  - **Attachments**: Set de URLs de adjuntos (local o S3)
  - Timestamps automáticos

- **Repository**: `TransactionRepository.java`
  - Búsqueda por organización
  - Filtrado por fechas, categoría, tipo
  - Consultas personalizadas con @Query
  - Paginación incluida

- **Service**: `TransactionService.java`
  - CRUD completo
  - **Actualización automática de saldos**
  - Validaciones de cuentas activas
  - Manejo de tags y adjuntos

- **Controller**: `TransactionController.java`
  - `GET /api/transactions` - Listar con filtros y paginación
  - `GET /api/transactions/{id}` - Obtener por ID
  - `POST /api/transactions` - Crear nueva
  - `PUT /api/transactions/{id}` - Actualizar
  - `DELETE /api/transactions/{id}` - Eliminar

#### 3. **Transferencias Cuenta ↔ Cuenta** ✅
- **DTO**: `TransferRequest.java`
  - Campos: fromAccountId, toAccountId, amount, transactionDate, description, notes

- **Service**: Método `createTransfer()`
  - Validación de cuentas diferentes
  - Validación de saldo suficiente
  - Actualización atómica de saldos
  - Creación de transacción tipo TRANSFER

- **Controller**: Endpoint específico
  - `POST /api/transactions/transfer` - Crear transferencia

#### 4. **Validación y Actualización de Saldos** ✅
- Actualización automática en:
  - Crear transacción (INCOME suma, EXPENSE resta)
  - Actualizar transacción (revierte y aplica nuevos valores)
  - Eliminar transacción (revierte el efecto)
  - Transferencias (resta de origen, suma a destino)

- Validaciones:
  - Saldo suficiente para gastos y transferencias
  - Cuentas activas
  - Montos positivos
  - Datos requeridos

#### 5. **Tags y Adjuntos** ✅
- **Tags**: 
  - Set de strings para etiquetar transacciones
  - Tabla: `transaction_tags`
  - Útil para búsquedas y organización

- **Attachments**:
  - Set de URLs (máx 1000 caracteres cada una)
  - Tabla: `transaction_attachments`
  - Soporte para links locales o S3

#### 6. **Data Seeder Actualizado** ✅
- Categorías predefinidas:
  - **Ingresos**: Ventas, Servicios, Inversiones, Otros
  - **Gastos**: Sueldos, Oficina, Marketing, Servicios, Transporte, Alimentación, Tecnología, Capacitación, Mantenimiento, Impuestos, Otros
  - Cada categoría con emoji y color

### Frontend (React + Vite)

#### 1. **Servicios API** ✅
- `categoryService`:
  - getAll, getByType, getById, create, update, delete

- `transactionService`:
  - getAll (con parámetros de filtros), getById, create, update, delete, createTransfer

#### 2. **Vista de Transacciones** ✅
Archivo: `frontend/src/pages/Transactions.jsx`

**Características**:
- ✅ Listado paginado de transacciones
- ✅ Tabla responsive con información completa
- ✅ Modal para crear/editar transacciones
- ✅ Formulario completo con validaciones
- ✅ Filtros avanzados:
  - Rango de fechas (startDate, endDate)
  - Categoría
  - Tipo (INCOME/EXPENSE/TRANSFER)
  - Paginación
  - Ordenamiento
- ✅ Indicadores visuales de tipo (colores)
- ✅ Acciones: Editar, Eliminar
- ✅ Carga rápida con estados de loading
- ✅ Selector de categorías dinámico según tipo

#### 3. **Vista de Categorías** ✅
Archivo: `frontend/src/pages/Categories.jsx`

**Características**:
- ✅ Grid de tarjetas con categorías
- ✅ Vista colorida con iconos emoji
- ✅ Filtros por tipo (Todas/Ingresos/Gastos)
- ✅ Modal para crear/editar
- ✅ Selector de color visual
- ✅ Estado activo/inactivo
- ✅ Validaciones de formulario

#### 4. **Dashboard Actualizado** ✅
Archivo: `frontend/src/pages/Dashboard.jsx`

**Características**:
- ✅ Navegación integrada (Dashboard, Transacciones, Categorías)
- ✅ Cards de estadísticas (placeholder para futuras métricas)
- ✅ Botones de acciones rápidas
- ✅ Diseño moderno con Tailwind CSS
- ✅ Información del usuario
- ✅ Lista de funcionalidades completadas

#### 5. **Rutas Configuradas** ✅
Archivo: `frontend/src/App.jsx`

- `/dashboard` - Dashboard principal
- `/transactions` - Gestión de transacciones
- `/categories` - Gestión de categorías
- `/login` - Inicio de sesión
- `/register` - Registro

### Base de Datos

#### Tablas Nuevas:
1. **categories**
   - id, name, description, type, icon, color, active
   - organization_id, created_by
   - created_at, updated_at

2. **transactions**
   - id, type, amount, transaction_date, description, notes
   - account_id, category_id, destination_account_id, organization_id, created_by
   - created_at, updated_at

3. **transaction_tags**
   - transaction_id, tag

4. **transaction_attachments**
   - transaction_id, attachment_url

#### Relaciones:
- Account ← Transaction (1:N)
- Account ← Transaction.destinationAccount (1:N) para transferencias
- Category ← Transaction (1:N)
- Organization ← Category (1:N)
- Organization ← Transaction (1:N)
- User ← Category.createdBy (1:N)
- User ← Transaction.createdBy (1:N)

## 🚀 Cómo Usar

### Backend
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm run dev
```

### Acceso
- Frontend: http://localhost:5173
- Backend: http://localhost:8080/api

### Credenciales Demo
- **Admin**: admin@demo.com / admin123
- **User**: user@demo.com / user123

## 📋 Endpoints API

### Categorías
```
GET    /api/categories                  - Listar todas
GET    /api/categories/type/{type}      - Filtrar por tipo
GET    /api/categories/{id}             - Obtener por ID
POST   /api/categories                  - Crear
PUT    /api/categories/{id}             - Actualizar
DELETE /api/categories/{id}             - Eliminar
```

### Transacciones
```
GET    /api/transactions                - Listar con filtros
       ?startDate=2024-01-01
       &endDate=2024-12-31
       &categoryId=1
       &type=EXPENSE
       &page=0
       &size=20
       &sortBy=transactionDate
       &sortDirection=DESC

GET    /api/transactions/{id}           - Obtener por ID
POST   /api/transactions                - Crear
PUT    /api/transactions/{id}           - Actualizar
DELETE /api/transactions/{id}           - Eliminar
POST   /api/transactions/transfer       - Crear transferencia
```

## 🎨 Ejemplos de Uso

### Crear Transacción de Ingreso
```json
POST /api/transactions
{
  "type": "INCOME",
  "amount": 1500.00,
  "transactionDate": "2024-11-10",
  "description": "Pago de cliente",
  "notes": "Factura #123",
  "accountId": 1,
  "categoryId": 1,
  "tags": ["cliente-abc", "proyecto-x"],
  "attachments": ["https://s3.../factura.pdf"]
}
```

### Crear Transferencia
```json
POST /api/transactions/transfer
{
  "fromAccountId": 1,
  "toAccountId": 2,
  "amount": 500.00,
  "transactionDate": "2024-11-10",
  "description": "Transferencia a ahorros",
  "notes": "Ahorro mensual"
}
```

### Crear Categoría
```json
POST /api/categories
{
  "name": "Ventas",
  "description": "Ingresos por ventas",
  "type": "INCOME",
  "icon": "💰",
  "color": "#10B981",
  "active": true
}
```

## ✅ Checklist Completo

### Backend
- [x] CRUD Categorías
- [x] CRUD Transacciones
- [x] Transferencias cuenta ↔ cuenta
- [x] Validación: actualiza saldo
- [x] Tags para transacciones
- [x] Adjuntos (link local o S3)

### Frontend
- [x] Vista transacciones
- [x] Crear/editar transacción
- [x] Filtros (fecha, categoría)
- [x] Carga rápida
- [x] Vista categorías
- [x] Crear/editar categorías

### DB
- [x] Definir relaciones account ↔ tx
- [x] Tablas de categorías
- [x] Tablas de transacciones
- [x] Tablas de tags y adjuntos

## 🎯 Entregable
✅ **Se registran movimientos + se refleja en saldos**

El sistema ahora permite:
1. Crear transacciones de ingresos/gastos
2. Los saldos de las cuentas se actualizan automáticamente
3. Se pueden hacer transferencias entre cuentas
4. Todo está validado y es transaccional
5. Las categorías permiten organizar los movimientos
6. Los tags y adjuntos añaden contexto adicional
7. La interfaz permite gestión completa de forma intuitiva

## 📊 Próximos Pasos Sugeridos
- Agregar estadísticas reales al dashboard
- Implementar gráficas de ingresos/gastos
- Exportación de reportes (PDF/Excel)
- Presupuestos por categoría
- Recordatorios de pagos recurrentes
- Dashboard de métricas avanzadas
