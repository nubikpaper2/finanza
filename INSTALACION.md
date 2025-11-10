# 📦 GUÍA DE INSTALACIÓN - Finanza

## ⚠️ REQUISITOS NO ENCONTRADOS

Para ejecutar el proyecto Finanza, necesitas instalar las siguientes herramientas:

---

## 🔧 PASO 1: Instalar Java 17+

### Opción A: Amazon Corretto (Recomendado)
1. Visita: https://aws.amazon.com/corretto/
2. Descarga **Amazon Corretto 17** para Windows
3. Ejecuta el instalador
4. Verifica la instalación:
   ```powershell
   java -version
   ```

### Opción B: Oracle JDK
1. Visita: https://www.oracle.com/java/technologies/downloads/
2. Descarga **JDK 17** o superior
3. Instala siguiendo el asistente
4. Verifica la instalación

---

## 🔧 PASO 2: Instalar Maven

### Opción A: Chocolatey (Más fácil)
```powershell
# Instalar Chocolatey primero (si no lo tienes)
Set-ExecutionPolicy Bypass -Scope Process -Force
[System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072
iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# Luego instalar Maven
choco install maven
```

### Opción B: Manual
1. Visita: https://maven.apache.org/download.cgi
2. Descarga el Binary zip archive
3. Extrae en `C:\Program Files\Maven`
4. Agrega a PATH: `C:\Program Files\Maven\bin`
5. Verifica:
   ```powershell
   mvn -version
   ```

---

## 🔧 PASO 3: Instalar Node.js 18+

1. Visita: https://nodejs.org/
2. Descarga la versión **LTS** (Long Term Support)
3. Ejecuta el instalador (incluye npm automáticamente)
4. Verifica la instalación:
   ```powershell
   node --version
   npm --version
   ```

---

## 🔧 PASO 4: Instalar Docker Desktop (Opcional pero recomendado)

1. Visita: https://www.docker.com/products/docker-desktop/
2. Descarga Docker Desktop para Windows
3. Ejecuta el instalador
4. Reinicia tu computadora si es necesario
5. Inicia Docker Desktop
6. Verifica la instalación:
   ```powershell
   docker --version
   docker-compose --version
   ```

---

---

## 🔧 PASO 5: Instalar MySQL (sin Docker)

### Opción A: Chocolatey (rápido y recomendado)
```powershell
# Instalar MySQL Community Server
choco install mysql -y

# Iniciar servicio (si no se inicia automáticamente)
net start mysql

# Verificar cliente MySQL
mysql --version
```

> Nota: El paquete `mysql` en Chocolatey instala MySQL Community Server. Si prefieres el instalador oficial, descarga desde https://dev.mysql.com/downloads/mysql/.

### Opción B: MySQL Installer (manual)
1. Visita: https://dev.mysql.com/downloads/installer/
2. Descarga el instalador para Windows y ejecútalo
3. Sigue el asistente y anota la contraseña del usuario `root`

### Usar Docker (opcional)
Si prefieres Docker, puedes levantar MySQL con `docker run` o `docker-compose` (no es necesario para esta guía).

---

## ✅ VERIFICACIÓN DE INSTALACIÓN

Después de instalar todo, verifica en PowerShell:

```powershell
# Java
java -version
# Debe mostrar: openjdk version "17.x.x" o similar

# Maven
mvn -version
# Debe mostrar: Apache Maven 3.x.x

# Node.js
node --version
# Debe mostrar: v18.x.x o superior

# NPM
npm --version
# Debe mostrar: 9.x.x o superior

# Docker (opcional)
docker --version
# Debe mostrar: Docker version 24.x.x o superior
```

---

## 🚀 DESPUÉS DE INSTALAR TODO

### Con Docker (Recomendado):
```powershell
cd e:\Proyectos\finanza
.\start-docker.ps1
```

### Sin Docker (Manual) usando MySQL:
```powershell
# 1) Asegúrate de que MySQL esté instalado y corriendo
# 2) Crear base de datos y usuario (ejecutar en MySQL CLI como root):
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS finanza_db; CREATE USER IF NOT EXISTS 'finanza_user'@'localhost' IDENTIFIED BY 'finanza_pass'; GRANT ALL PRIVILEGES ON finanza_db.* TO 'finanza_user'@'localhost'; FLUSH PRIVILEGES;"

# 3) Backend
cd e:\Proyectos\finanza\backend
mvn clean install
mvn spring-boot:run

# 4) Frontend
cd e:\Proyectos\finanza\frontend
npm install
npm run dev
```

---

## 🎯 CONFIGURACIÓN MANUAL DE LA BASE DE DATOS

Si instalaste MySQL manualmente, crea la base de datos y el usuario:

```sql
-- Ejecutar en MySQL como root
CREATE DATABASE IF NOT EXISTS finanza_db;
CREATE USER IF NOT EXISTS 'finanza_user'@'localhost' IDENTIFIED BY 'finanza_pass';
GRANT ALL PRIVILEGES ON finanza_db.* TO 'finanza_user'@'localhost';
FLUSH PRIVILEGES;
```

Luego verifica que `backend/src/main/resources/application.properties` tenga:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/finanza_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=finanza_user
spring.datasource.password=finanza_pass
```

---

## 📋 RESUMEN - ORDEN DE INSTALACIÓN

1. ✅ Java 17+ (Amazon Corretto o Oracle JDK)
2. ✅ Maven 3.9+
3. ✅ Node.js 18+ (incluye NPM)
4. ✅ Docker Desktop (opcional, pero facilita mucho)
5. ✅ PostgreSQL (solo si NO usas Docker)

---

## 💡 TIPS

- **Reinicia PowerShell** después de cada instalación para que reconozca los comandos
- **Reinicia Windows** después de instalar Docker Desktop
- Usa **Chocolatey** para instalar todo rápidamente
- Con **Docker** todo es más fácil (base de datos incluida)

---

## 🆘 AYUDA

Si tienes problemas con la instalación:

1. Verifica que agregaste las herramientas al PATH de Windows
2. Reinicia PowerShell o abre una nueva ventana
3. Verifica permisos de administrador
4. Consulta la documentación oficial de cada herramienta

---

## 🎉 UNA VEZ TODO INSTALADO

Vuelve a ejecutar el proyecto:

```powershell
cd e:\Proyectos\finanza
.\start-dev.ps1
```

O si prefieres Docker:

```powershell
.\start-docker.ps1
```

---

**¡Luego de instalar las herramientas, el proyecto estará listo para ejecutarse!** 🚀
