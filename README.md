# 💰 Finanza - Sistema de Gestión Financiera

Sistema completo de gestión financiera con autenticación JWT, desarrollado con Spring Boot y React.

## 🎯 Características

✅ **Autenticación JWT completa**
- Registro de usuarios
- Login con credenciales seguras
- Protección de rutas
- Roles de usuario (USER/ADMIN)

✅ **Backend Spring Boot**
- API REST
- Spring Security
- MySQL Database (local o servidor)
- Hibernate/JPA
- Validación de datos
- Manejo de excepciones

✅ **Frontend React**
- Interfaz moderna y responsive
- Context API para state management
- React Router para navegación
- Axios para peticiones HTTP

✅ **Entidades del Sistema**
- Users (Usuarios)
- Organizations (Organizaciones)
- Accounts (Cuentas financieras)

✅ **Infraestructura**
- Docker Compose
- PostgreSQL containerizado
- Configuración de deployment

## 🚀 Inicio Rápido

### Prerrequisitos

-- Java 17+
-- Maven 3.9+
-- Node.js 18+
-- Docker & Docker Compose (opcional)
-- MySQL (local) o Docker

### Opción 1: Usando Docker Compose (Recomendado)

```powershell
# Clonar y entrar al proyecto
cd e:\Proyectos\finanza

# Levantar todos los servicios
docker-compose up -d

# Ver logs
docker-compose logs -f
```

La aplicación estará disponible en:
- Frontend: http://localhost:3000
- Backend: http://localhost:8080/api
- MySQL: localhost:3306

### Opción 2: Desarrollo Local

#### Backend

```powershell
# Navegar al directorio backend
cd backend

# Instalar dependencias y compilar
mvn clean install

# Ejecutar la aplicación
mvn spring-boot:run
```

El backend estará en: http://localhost:8080/api

#### Frontend

```powershell
# Navegar al directorio frontend
cd frontend

# Instalar dependencias
npm install

# Ejecutar en modo desarrollo
npm run dev
```

El frontend estará en: http://localhost:3000

## 🗄️ Base de Datos

### Configuración MySQL

```sql
CREATE DATABASE IF NOT EXISTS finanza_db;
CREATE USER IF NOT EXISTS 'finanza_user'@'localhost' IDENTIFIED BY 'finanza_pass';
GRANT ALL PRIVILEGES ON finanza_db.* TO 'finanza_user'@'localhost';
FLUSH PRIVILEGES;
```

### Configuración en application.properties

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/finanza_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=finanza_user
spring.datasource.password=finanza_pass
```

## 👤 Credenciales Demo

El sistema incluye usuarios de prueba:

**Administrador:**
- Email: `admin@demo.com`
- Password: `admin123`

**Usuario Regular:**
- Email: `user@demo.com`
- Password: `user123`

## 📡 API Endpoints

### Autenticación

#### POST /api/auth/register
Registrar nuevo usuario

```json
{
  "email": "usuario@example.com",
  "password": "password123",
  "firstName": "Juan",
  "lastName": "Pérez",
  "phone": "+1234567890",
  "organizationName": "Mi Empresa"
}
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "email": "usuario@example.com",
  "firstName": "Juan",
  "lastName": "Pérez",
  "roles": ["ROLE_USER"],
  "organizationId": 1,
  "organizationName": "Mi Empresa"
}
```

#### POST /api/auth/login
Iniciar sesión

```json
{
  "email": "admin@demo.com",
  "password": "admin123"
}
```

**Respuesta:** Igual al registro

### Endpoints Protegidos

Incluir en headers:
```
Authorization: Bearer {token}
```

## 🏗️ Estructura del Proyecto

```
finanza/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/finanza/
│   │   │   │   ├── config/          # Configuraciones
│   │   │   │   ├── controller/      # REST Controllers
│   │   │   │   ├── dto/             # Data Transfer Objects
│   │   │   │   ├── exception/       # Exception Handlers
│   │   │   │   ├── model/           # Entidades JPA
│   │   │   │   ├── repository/      # Repositorios
│   │   │   │   ├── security/        # JWT & Security
│   │   │   │   └── service/         # Servicios
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   ├── Dockerfile
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── context/        # Context API
│   │   ├── pages/          # Componentes de páginas
│   │   ├── services/       # API services
│   │   ├── App.jsx
│   │   ├── main.jsx
│   │   └── index.css
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── package.json
│   └── vite.config.js
└── docker-compose.yml
```

## 🔐 Seguridad

- Contraseñas hasheadas con BCrypt
- JWT con expiración de 24 horas
- CORS configurado
- Validación de datos en backend
- Protección CSRF deshabilitada (API stateless)
- Headers de seguridad

## 📊 Modelo de Datos

### User
- id, email, password, firstName, lastName
- phone, roles, active
- organization (ManyToOne)
- createdAt, updatedAt

### Organization
- id, name, description, taxId, address
- active
- users (OneToMany)
- accounts (OneToMany)
- createdAt, updatedAt

### Account
- id, name, type, balance, currency
- description, active
- organization (ManyToOne)
- createdBy (ManyToOne User)
- createdAt, updatedAt

**Tipos de Cuenta:** CASH, BANK, CREDIT_CARD, INVESTMENT, LOAN, SAVINGS, OTHER

## 🚢 Deployment

### Railway

```powershell
# Instalar Railway CLI
npm i -g @railway/cli

# Login
railway login

# Inicializar proyecto
railway init

# Deploy
railway up
```

### Render

1. Conectar repositorio GitHub
2. Crear Web Service para backend
3. Crear Static Site para frontend
4. Crear PostgreSQL database
5. Configurar variables de entorno

### Variables de Entorno Necesarias

Backend:
```
SPRING_DATASOURCE_URL=jdbc:postgresql://host:port/database
SPRING_DATASOURCE_USERNAME=user
SPRING_DATASOURCE_PASSWORD=password
JWT_SECRET=tu-secret-key-seguro
```

Frontend:
```
VITE_API_URL=https://tu-backend.com/api
```

## 🛠️ Comandos Útiles

### Maven
```powershell
mvn clean                 # Limpiar build
mvn compile              # Compilar
mvn test                 # Ejecutar tests
mvn package              # Crear JAR
mvn spring-boot:run      # Ejecutar aplicación
```

### Docker
```powershell
docker-compose up -d              # Levantar servicios
docker-compose down               # Detener servicios
docker-compose logs -f backend    # Ver logs backend
docker-compose restart backend    # Reiniciar backend
docker-compose ps                 # Ver estado servicios
```

### NPM
```powershell
npm install              # Instalar dependencias
npm run dev             # Modo desarrollo
npm run build           # Build producción
npm run preview         # Preview build
```

## 🧪 Testing

```powershell
# Backend tests
cd backend
mvn test

# Frontend tests (cuando se implementen)
cd frontend
npm test
```

## 📝 Datos Iniciales

El sistema carga automáticamente:
- 1 Organización demo
- 2 Usuarios (admin y user)
- 5 Cuentas de ejemplo:
  - Efectivo: $5,000
  - Banco Principal: $25,000
  - Ahorros: $10,000
  - Tarjeta de Crédito: -$3,500
  - Inversiones: $50,000

## 🔄 Git

```powershell
# Inicializar repositorio
git init

# Agregar archivos
git add .

# Commit inicial
git commit -m "Initial commit: Sistema Finanza con autenticación JWT"

# Agregar remote y push
git remote add origin https://github.com/tu-usuario/finanza.git
git branch -M main
git push -u origin main
```

## 📚 Tecnologías

**Backend:**
- Spring Boot 3.2.0
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT (jjwt 0.12.3)
- Lombok
- Maven

**Frontend:**
- React 18
- React Router DOM
- Axios
- Vite
- Context API

**DevOps:**
- Docker
- Docker Compose
- Nginx

## 🎯 Próximos Pasos

- [ ] Implementar transacciones
- [ ] Dashboard con gráficos
- [ ] Reportes financieros
- [ ] Categorías de gastos/ingresos
- [ ] Presupuestos
- [ ] Exportación de datos
- [ ] Notificaciones
- [ ] Multi-currency support
- [ ] Testing unitario completo
- [ ] API documentation (Swagger)

## 🤝 Contribuir

1. Fork el proyecto
2. Crea una rama (`git checkout -b feature/nueva-funcionalidad`)
3. Commit cambios (`git commit -m 'Agregar nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Abrir Pull Request

## 📄 Licencia

Este proyecto es de código abierto y está disponible bajo la licencia MIT.

## 👨‍💻 Autor

Desarrollado para el sistema de gestión financiera Finanza.

## 📞 Soporte

Para preguntas o problemas, crear un issue en el repositorio.

---

**¡Gracias por usar Finanza! 💰**
