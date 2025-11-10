# API Examples - Finanza

Ejemplos de uso de la API REST de Finanza.

## Base URL
```
http://localhost:8080/api
```

## 1. Health Check

### Request
```http
GET /health
```

### Response
```json
{
  "status": "UP",
  "timestamp": "2024-11-09T10:30:00",
  "application": "Finanza Backend",
  "version": "1.0.0"
}
```

## 2. Registro de Usuario

### Request
```http
POST /auth/register
Content-Type: application/json

{
  "email": "nuevo@ejemplo.com",
  "password": "password123",
  "firstName": "Juan",
  "lastName": "Pérez",
  "phone": "+1234567890",
  "organizationName": "Mi Empresa S.A."
}
```

### Response Success (201 Created)
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 3,
  "email": "nuevo@ejemplo.com",
  "firstName": "Juan",
  "lastName": "Pérez",
  "roles": ["ROLE_USER"],
  "organizationId": 2,
  "organizationName": "Mi Empresa S.A."
}
```

### Response Error (400 Bad Request)
```json
{
  "message": "El email ya está registrado",
  "status": 400,
  "timestamp": 1699531800000
}
```

### Response Validation Error
```json
{
  "email": "Email inválido",
  "password": "La contraseña debe tener al menos 6 caracteres"
}
```

## 3. Login

### Request
```http
POST /auth/login
Content-Type: application/json

{
  "email": "admin@demo.com",
  "password": "admin123"
}
```

### Response Success (200 OK)
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "email": "admin@demo.com",
  "firstName": "Admin",
  "lastName": "Demo",
  "roles": ["ROLE_USER", "ROLE_ADMIN"],
  "organizationId": 1,
  "organizationName": "Demo Company"
}
```

### Response Error (401 Unauthorized)
```json
{
  "message": "Credenciales inválidas",
  "status": 401,
  "timestamp": 1699531800000
}
```

## 4. Endpoints Protegidos (Ejemplo)

Para acceder a endpoints protegidos, incluir el token en el header:

### Request
```http
GET /api/users/me
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## PowerShell Examples

### 1. Health Check
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/health" -Method Get
```

### 2. Registro
```powershell
$body = @{
    email = "test@example.com"
    password = "test123"
    firstName = "Test"
    lastName = "User"
} | ConvertTo-Json

$response = Invoke-RestMethod `
    -Uri "http://localhost:8080/api/auth/register" `
    -Method Post `
    -Body $body `
    -ContentType "application/json"

$response
```

### 3. Login
```powershell
$credentials = @{
    email = "admin@demo.com"
    password = "admin123"
} | ConvertTo-Json

$response = Invoke-RestMethod `
    -Uri "http://localhost:8080/api/auth/login" `
    -Method Post `
    -Body $credentials `
    -ContentType "application/json"

# Guardar token
$token = $response.token
Write-Host "Token: $token"
```

### 4. Usar Token en Request
```powershell
$headers = @{
    "Authorization" = "Bearer $token"
}

Invoke-RestMethod `
    -Uri "http://localhost:8080/api/users/me" `
    -Method Get `
    -Headers $headers
```

## JavaScript/Fetch Examples

### 1. Registro
```javascript
const register = async (userData) => {
  const response = await fetch('http://localhost:8080/api/auth/register', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(userData),
  });
  
  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message);
  }
  
  return await response.json();
};

// Uso
const newUser = {
  email: 'test@example.com',
  password: 'test123',
  firstName: 'Test',
  lastName: 'User',
};

register(newUser)
  .then(data => console.log('Usuario registrado:', data))
  .catch(error => console.error('Error:', error));
```

### 2. Login
```javascript
const login = async (email, password) => {
  const response = await fetch('http://localhost:8080/api/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ email, password }),
  });
  
  if (!response.ok) {
    throw new Error('Credenciales inválidas');
  }
  
  const data = await response.json();
  // Guardar token en localStorage
  localStorage.setItem('token', data.token);
  return data;
};

// Uso
login('admin@demo.com', 'admin123')
  .then(data => console.log('Login exitoso:', data))
  .catch(error => console.error('Error:', error));
```

### 3. Request Autenticado
```javascript
const authenticatedRequest = async (url) => {
  const token = localStorage.getItem('token');
  
  const response = await fetch(url, {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
  });
  
  if (!response.ok) {
    throw new Error('Error en la petición');
  }
  
  return await response.json();
};
```

## Axios Examples (React)

### 1. Configurar Axios
```javascript
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor para agregar token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

export default api;
```

### 2. Usar en Componentes
```javascript
import api from './services/api';

// Registro
const handleRegister = async (userData) => {
  try {
    const response = await api.post('/auth/register', userData);
    console.log('Usuario registrado:', response.data);
  } catch (error) {
    console.error('Error:', error.response?.data?.message);
  }
};

// Login
const handleLogin = async (email, password) => {
  try {
    const response = await api.post('/auth/login', { email, password });
    localStorage.setItem('token', response.data.token);
    return response.data;
  } catch (error) {
    throw error.response?.data?.message || 'Error al iniciar sesión';
  }
};
```

## cURL Examples

### 1. Health Check
```bash
curl http://localhost:8080/api/health
```

### 2. Registro
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "test123",
    "firstName": "Test",
    "lastName": "User"
  }'
```

### 3. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@demo.com",
    "password": "admin123"
  }'
```

### 4. Request con Token
```bash
TOKEN="tu-token-aqui"

curl http://localhost:8080/api/users/me \
  -H "Authorization: Bearer $TOKEN"
```

## Status Codes

- `200 OK` - Petición exitosa
- `201 Created` - Recurso creado exitosamente
- `400 Bad Request` - Error en los datos enviados
- `401 Unauthorized` - No autenticado o token inválido
- `403 Forbidden` - No tienes permisos
- `404 Not Found` - Recurso no encontrado
- `500 Internal Server Error` - Error del servidor

## Error Handling

Todos los errores siguen este formato:

```json
{
  "message": "Descripción del error",
  "status": 400,
  "timestamp": 1699531800000
}
```

Para errores de validación:
```json
{
  "campo1": "Mensaje de error del campo1",
  "campo2": "Mensaje de error del campo2"
}
```

## Testing con Postman

1. Importar colección (crear archivo `Finanza.postman_collection.json`)
2. Configurar variable `{{baseUrl}}` = `http://localhost:8080/api`
3. Configurar variable `{{token}}` para requests autenticados
4. Ejecutar tests

## Testing con Thunder Client (VS Code)

1. Instalar extensión Thunder Client
2. Crear nueva colección "Finanza"
3. Agregar requests según ejemplos
4. Usar variables de entorno para baseUrl y token
