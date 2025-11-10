# HELP.md - Guía de Ayuda Rápida

## 🚀 Inicio Rápido

### 1. Levantar la base de datos
Si usas MySQL local, asegúrate que el servicio esté corriendo. Si usas Docker, levanta MySQL o usa el paso de instalación en `INSTALACION.md`.

```powershell
# Iniciar MySQL service en Windows (si fue instalado vía Chocolatey)
net start mysql

# O con Docker (opcional)
docker-compose up -d
```

### 2. Ejecutar el backend
```powershell
cd backend
mvn spring-boot:run
```

### 3. Ejecutar el frontend
```powershell
cd frontend
npm install
npm run dev
```

## 🔑 Credenciales de Acceso

### Usuarios Demo
- **Admin**: admin@demo.com / admin123
- **User**: user@demo.com / user123

### Base de Datos
- **Database**: finanza_db
- **User**: finanza_user
- **Password**: finanza_pass
- **Port**: 3306

## 📡 URLs Importantes

- Frontend: http://localhost:3000
- Backend API: http://localhost:8080/api
- Health Check: http://localhost:8080/api/health

## 🛠️ Solución de Problemas

### Error de conexión a base de datos
1. Verificar que MySQL está corriendo: `net start mysql` o comprobar servicio en el administrador de servicios
2. Revisar logs de MySQL (Event Viewer o archivos de log de MySQL)
3. Reiniciar servicio: `net stop mysql` y `net start mysql` o usar el panel de servicios

### Error al compilar backend
1. Limpiar build: `mvn clean`
2. Actualizar dependencias: `mvn clean install`
3. Verificar Java 17+: `java -version`

### Error en frontend
1. Limpiar node_modules: `Remove-Item -Recurse -Force node_modules`
2. Reinstalar: `npm install`
3. Limpiar cache: `npm cache clean --force`

## 📝 Endpoints de Prueba

### Registro
```powershell
curl -X POST http://localhost:8080/api/auth/register `
  -H "Content-Type: application/json" `
  -d '{
    "email": "nuevo@test.com",
    "password": "test123",
    "firstName": "Nuevo",
    "lastName": "Usuario"
  }'
```

### Login
```powershell
curl -X POST http://localhost:8080/api/auth/login `
  -H "Content-Type: application/json" `
  -d '{
    "email": "admin@demo.com",
    "password": "admin123"
  }'
```

### Health Check
```powershell
curl http://localhost:8080/api/health
```

## 🐳 Docker Comandos Útiles

```powershell
# Ver servicios activos
docker-compose ps

# Ver logs
docker-compose logs -f

# Detener todo
docker-compose down

# Reconstruir imágenes
docker-compose build

# Limpiar todo
docker-compose down -v
```

## 💡 Tips de Desarrollo

1. **Hot Reload Backend**: Usa `spring-boot-devtools`
2. **Hot Reload Frontend**: Vite hace auto-reload
3. **Ver Base de Datos**: Usa DBeaver o pgAdmin
4. **Probar API**: Usa Postman o Thunder Client

## 📚 Recursos

- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [React Docs](https://react.dev)
- [MySQL Docs](https://dev.mysql.com/doc/)
- [JWT.io](https://jwt.io)
