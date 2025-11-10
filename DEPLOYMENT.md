# Deployment Guide

## 🚀 Opciones de Deployment

### 1. Railway

#### Backend + Database
```bash
# Instalar CLI
npm i -g @railway/cli

# Login
railway login

# Crear proyecto
railway init

# Agregar PostgreSQL
railway add

# Deploy
railway up
```

#### Variables de Entorno Railway
```
SPRING_DATASOURCE_URL=${{Postgres.DATABASE_URL}}
SPRING_DATASOURCE_USERNAME=${{Postgres.PGUSER}}
SPRING_DATASOURCE_PASSWORD=${{Postgres.PGPASSWORD}}
JWT_SECRET=tu-secret-key-muy-seguro-aqui
```

### 2. Render

#### Backend (Web Service)
1. Conectar repo de GitHub
2. Seleccionar `backend` como root directory
3. Build Command: `mvn clean package`
4. Start Command: `java -jar target/*.jar`

#### Database (PostgreSQL)
1. Create PostgreSQL database
2. Copiar Internal Database URL
3. Configurar en variables de entorno

#### Frontend (Static Site)
1. Root directory: `frontend`
2. Build Command: `npm install && npm run build`
3. Publish Directory: `dist`

### 3. Heroku

```bash
# Login
heroku login

# Crear app backend
heroku create finanza-backend

# Agregar PostgreSQL
heroku addons:create heroku-postgresql:mini

# Deploy
git push heroku main

# Crear app frontend
heroku create finanza-frontend

# Deploy frontend
git subtree push --prefix frontend heroku main
```

### 4. AWS (EC2 + RDS)

#### RDS PostgreSQL
1. Crear instancia RDS PostgreSQL
2. Configurar security groups
3. Anotar endpoint y credenciales

#### EC2 Backend
```bash
# Instalar Java
sudo yum install java-17-amazon-corretto

# Copiar JAR
scp target/*.jar ec2-user@instance:/home/ec2-user/

# Ejecutar
java -jar app.jar
```

#### S3 + CloudFront (Frontend)
```bash
# Build frontend
npm run build

# Subir a S3
aws s3 sync dist/ s3://finanza-frontend/

# Crear CloudFront distribution
```

### 5. DigitalOcean

#### App Platform
1. Conectar GitHub repo
2. Configurar componentes:
   - Backend: Dockerfile en `/backend`
   - Frontend: Dockerfile en `/frontend`
   - Database: PostgreSQL managed database

#### Droplet (Manual)
```bash
# Instalar Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sh get-docker.sh

# Clonar repo
git clone https://github.com/tu-usuario/finanza.git
cd finanza

# Ejecutar
docker-compose up -d
```

## 🔐 Variables de Entorno por Plataforma

### Production application.properties
```properties
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DATABASE_USER}
spring.datasource.password=${DATABASE_PASSWORD}
spring.jpa.hibernate.ddl-auto=validate
jwt.secret=${JWT_SECRET}
cors.allowed-origins=${FRONTEND_URL}
```

### Frontend (.env.production)
```
VITE_API_URL=https://tu-backend.com/api
```

## 📊 Checklist de Deployment

- [ ] Cambiar `ddl-auto` a `validate` en producción
- [ ] Usar secreto JWT seguro (min 256 bits)
- [ ] Configurar CORS correctamente
- [ ] Habilitar HTTPS
- [ ] Configurar variables de entorno
- [ ] Hacer backup de base de datos
- [ ] Configurar logging apropiado
- [ ] Implementar rate limiting
- [ ] Configurar health checks
- [ ] Documentar API (Swagger)

## 🔍 Monitoring

### Health Checks
- Backend: `https://api.finanza.com/health`
- Database: Verificar conexión en logs

### Logs
```bash
# Railway
railway logs

# Render
# Ver en dashboard

# Docker
docker-compose logs -f
```

## 🔄 CI/CD con GitHub Actions

Crear `.github/workflows/deploy.yml`:

```yaml
name: Deploy to Production

on:
  push:
    branches: [main]

jobs:
  deploy-backend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '17'
      - run: cd backend && mvn clean package
      - uses: railway/cli@v1
        with:
          command: up

  deploy-frontend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
      - run: cd frontend && npm ci && npm run build
      - uses: netlify/actions/cli@master
        with:
          args: deploy --prod
```

## 🛡️ Security Best Practices

1. **Nunca commitear** secrets en el código
2. Usar **variables de entorno** para configuración
3. Habilitar **HTTPS** siempre
4. Implementar **rate limiting**
5. Configurar **CORS** restrictivamente
6. Usar **JWT con expiración corta**
7. Hacer **backups regulares** de la DB
8. Implementar **logging** y monitoring

## 💾 Backup de Base de Datos

```bash
# Backup local
pg_dump -U finanza_user finanza_db > backup.sql

# Restore
psql -U finanza_user finanza_db < backup.sql

# Backup en producción (Railway)
railway run pg_dump > backup.sql
```

## 🌐 Dominios Personalizados

### Railway
```bash
railway domain
```

### Render
1. Settings > Custom Domain
2. Agregar CNAME record en tu DNS

### CloudFlare (SSL gratis)
1. Agregar sitio en CloudFlare
2. Actualizar nameservers
3. SSL automático

## 📈 Escalabilidad

### Horizontal Scaling
- Múltiples instancias del backend
- Load balancer (Nginx, AWS ELB)
- Session compartida (Redis)

### Database
- Connection pooling configurado
- Read replicas para lectura
- Índices optimizados

### Caching
- Redis para sesiones
- CDN para assets estáticos
- API response caching

## 🎯 Próximos Pasos Post-Deployment

1. Configurar dominio personalizado
2. Implementar SSL/TLS
3. Configurar monitoring (Sentry, Datadog)
4. Setup CI/CD pipeline
5. Implementar backup automatizado
6. Configurar alertas
7. Documentar API con Swagger
8. Agregar tests de integración
