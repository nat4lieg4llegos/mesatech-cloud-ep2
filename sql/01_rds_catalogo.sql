-- Ejecutar en la RDS de CATÁLOGO (mesatech-catalogo-db), conectada como el usuario maestro.
-- Reemplazar la clave antes de ejecutar y NO guardar este archivo con la clave real.

CREATE DATABASE IF NOT EXISTS catalogo CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Usuario de aplicación: solo puede trabajar en su base, no es administrador
CREATE USER IF NOT EXISTS 'app_catalogo'@'%' IDENTIFIED BY 'CAMBIAR_CLAVE';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, INDEX, REFERENCES ON catalogo.* TO 'app_catalogo'@'%';
FLUSH PRIVILEGES;

USE catalogo;

-- Misma estructura que crea Hibernate (ddl-auto=update); se deja para que sea reproducible
CREATE TABLE IF NOT EXISTS categoria (
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    nombre         VARCHAR(100) NOT NULL,
    descripcion    VARCHAR(255) NULL,
    fecha_creacion DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_categoria_nombre UNIQUE (nombre)
) ENGINE=InnoDB;

-- Consulta de evidencia
-- SELECT id, nombre, fecha_creacion FROM categoria ORDER BY id DESC;
