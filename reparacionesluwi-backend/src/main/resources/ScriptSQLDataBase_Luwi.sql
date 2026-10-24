CREATE DATABASE IF NOT EXISTS luwi_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE luwi_db;

CREATE TABLE usuarios (
    id_usuario BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(150) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    rol ENUM('CLIENTE', 'REPARADOR') NOT NULL,
    ciudad VARCHAR(100) NOT NULL,
    direccion_referencia VARCHAR(200) NULL,
    fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_usuarios_correo UNIQUE (correo),
    CONSTRAINT chk_usuarios_nombre CHECK (CHAR_LENGTH(TRIM(nombre)) > 0),
    CONSTRAINT chk_usuarios_correo CHECK (CHAR_LENGTH(TRIM(correo)) > 0)
)  ENGINE=INNODB;

CREATE TABLE reparaciones (
    id_reparacion BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    titulo VARCHAR(120) NOT NULL,
    descripcion TEXT NOT NULL,
    categoria VARCHAR(80) NOT NULL,
    marca_objeto VARCHAR(100) NULL,
    presupuesto_minimo DECIMAL(12 , 2 ) NULL,
    presupuesto_maximo DECIMAL(12 , 2 ) NULL,
    ciudad VARCHAR(100) NOT NULL,
    zona VARCHAR(150) NOT NULL,
    estado ENUM('PUBLICADA', 'ASIGNADA', 'CANCELADA', 'FINALIZADA') NOT NULL DEFAULT 'PUBLICADA',
    fecha_publicacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_reparaciones_cliente FOREIGN KEY (cliente_id)
        REFERENCES usuarios (id_usuario)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT ch_reparaciones_presupuesto CHECK (presupuesto_minimo IS NULL
        OR presupuesto_minimo >= 0),
    CONSTRAINT chk_reparaciones_presupuesto_max CHECK (presupuesto_maximo IS NULL
        OR presupuesto_maximo >= 0),
    CONSTRAINT chk_reparaciones_rango CHECK (presupuesto_minimo IS NULL
        OR presupuesto_maximo IS NULL
        OR presupuesto_minimo <= presupuesto_maximo),
    CONSTRAINT chk_reparaciones_titulo CHECK (CHAR_LENGTH(TRIM(titulo)) > 0),
    INDEX idx_reparaciones_estado_ciudad (estado , ciudad),
    INDEX idx_reparaciones_cliente (cliente_id)
)  ENGINE=INNODB;

CREATE TABLE ofertas (
    id_oferta BIGINT AUTO_INCREMENT PRIMARY KEY,
    reparacion_id BIGINT NOT NULL,
    reparador_id BIGINT NOT NULL,
    precio_propuesto DECIMAL(12 , 2 ) NOT NULL,
    descripcion TEXT NOT NULL,
    tiempo_estimado VARCHAR(100) NOT NULL,
    estado ENUM('PENDIENTE', 'ACEPTADA', 'RECHAZADA', 'RETIRADA') NOT NULL DEFAULT 'PENDIENTE',
    fecha_oferta TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_ofertas_reparacion FOREIGN KEY (reparacion_id)
        REFERENCES reparaciones (id_reparacion)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_ofertas_reparador FOREIGN KEY (reparador_id)
        REFERENCES usuarios (id_usuario)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT uq_oferta_id_reparacion UNIQUE (id_oferta , reparacion_id),
    CONSTRAINT chk_ofertas_precio CHECK (precio_propuesto > 0),
    INDEX idx_ofertas_reparacion_estado (reparacion_id , estado),
    INDEX id_ofertas_reparador (reparador_id)
)  ENGINE=INNODB;

CREATE TABLE acuerdos (
    id_acuerdo BIGINT AUTO_INCREMENT PRIMARY KEY,
    reparacion_id BIGINT NOT NULL,
    oferta_id BIGINT NOT NULL,
    precio_acordado DECIMAL(12 , 2 ) NOT NULL,
    estado ENUM('CONFIRMADO', 'EN_PROCESO', 'FINALIZADO', 'CANCELADO') NOT NULL DEFAULT 'CONFIRMADO',
    fecha_acuerdo TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_acuerdo_reparacion UNIQUE (reparacion_id),
    CONSTRAINT uq_acuerdo_oferta UNIQUE (oferta_id),
    CONSTRAINT fk_acuerdos_reparacion FOREIGN KEY (reparacion_id)
        REFERENCES reparaciones (id_reparacion)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_acuerdos_oferta_reparacion FOREIGN KEY (oferta_id , reparacion_id)
        REFERENCES ofertas (id_oferta , reparacion_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_acuerdos_precio CHECK (precio_acordado > 0),
    INDEX id_acuerdos_estado (estado)
)  ENGINE=INNODB;