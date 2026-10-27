-- Ejecutar en la RDS de SOLICITUDES (mesatech-solicitudes-db), OTRA instancia distinta a la de Catálogo.

CREATE DATABASE IF NOT EXISTS solicitudes CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'app_solicitudes'@'%' IDENTIFIED BY 'CAMBIAR_CLAVE';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, INDEX, REFERENCES ON solicitudes.* TO 'app_solicitudes'@'%';
FLUSH PRIVILEGES;

USE solicitudes;

-- Copia local de categorías recibidas por RabbitMQ (categoria_id = id de origen en Catálogo)
CREATE TABLE IF NOT EXISTS categoria_local (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    categoria_id    BIGINT       NOT NULL,
    nombre          VARCHAR(100) NOT NULL,
    event_id        VARCHAR(36)  NOT NULL,
    fecha_evento    VARCHAR(40)  NOT NULL,
    fecha_recepcion DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_categoria_local_origen UNIQUE (categoria_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS solicitud (
    id                  BIGINT        NOT NULL AUTO_INCREMENT,
    titulo              VARCHAR(150)  NOT NULL,
    descripcion         VARCHAR(1000) NULL,
    categoria_id        BIGINT        NOT NULL,
    categoria_nombre    VARCHAR(100)  NOT NULL,
    estado              VARCHAR(20)   NOT NULL,
    fecha_creacion      DATETIME(6)   NOT NULL,
    fecha_actualizacion DATETIME(6)   NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- Consultas de evidencia
-- SELECT categoria_id, nombre, event_id, fecha_recepcion FROM categoria_local ORDER BY id DESC;
-- SELECT id, titulo, categoria_id, estado FROM solicitud ORDER BY id DESC;
