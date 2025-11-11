-- Script para configurar la base de datos PostgreSQL para Finanza

-- Crear el usuario
CREATE USER finanza_user WITH PASSWORD 'finanza_pass';

-- Crear la base de datos
CREATE DATABASE finanza_db;

-- Otorgar todos los privilegios al usuario
GRANT ALL PRIVILEGES ON DATABASE finanza_db TO finanza_user;

-- Conectarse a la base de datos (esto debe ejecutarse después)
\c finanza_db

-- Otorgar privilegios en el schema public
GRANT ALL ON SCHEMA public TO finanza_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO finanza_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO finanza_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO finanza_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO finanza_user;
