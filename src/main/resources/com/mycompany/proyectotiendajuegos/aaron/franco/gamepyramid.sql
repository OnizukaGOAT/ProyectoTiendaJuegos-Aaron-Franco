-- ============================================================
--  GamePyramid – Script de base de datos MySQL 8.x
--  Ejecutar: mysql -u root -p < gamepyramid.sql
-- ============================================================

DROP DATABASE IF EXISTS gamepyramid;
CREATE DATABASE gamepyramid CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE gamepyramid;

-- ── Tablas ──────────────────────────────────────────────────

CREATE TABLE administrador (
    id_admin    INT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    apellidos   VARCHAR(100),
    correo      VARCHAR(150) NOT NULL UNIQUE,
    contrasena  VARCHAR(255) NOT NULL
);

CREATE TABLE usuario (
    id_usuario  INT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    apellidos   VARCHAR(100),
    correo      VARCHAR(150) NOT NULL UNIQUE,
    contrasena  VARCHAR(255) NOT NULL,
    saldo       DECIMAL(10,2) DEFAULT 50.00,
    idioma      VARCHAR(50)   DEFAULT 'Español'
);

CREATE TABLE estudio (
    id_estudio  INT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(150) NOT NULL
);

CREATE TABLE juego (
    id_juego    INT AUTO_INCREMENT PRIMARY KEY,
    titulo      VARCHAR(200) NOT NULL,
    genero      VARCHAR(100),
    plataforma  VARCHAR(150),
    precio      DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    stock       INT           NOT NULL DEFAULT 0,
    director    VARCHAR(150),
    id_estudio  INT,
    FOREIGN KEY (id_estudio) REFERENCES estudio(id_estudio) ON DELETE SET NULL
);

CREATE TABLE desarrollador (
    id_desarrollador INT AUTO_INCREMENT PRIMARY KEY,
    nombre           VARCHAR(100) NOT NULL,
    apellidos        VARCHAR(100),
    anos_experiencia INT DEFAULT 0,
    puesto_actual    VARCHAR(150),
    id_estudio       INT,
    FOREIGN KEY (id_estudio) REFERENCES estudio(id_estudio) ON DELETE SET NULL
);

CREATE TABLE desarrollador_juego (
    id_desarrollador INT NOT NULL,
    id_juego         INT NOT NULL,
    PRIMARY KEY (id_desarrollador, id_juego),
    FOREIGN KEY (id_desarrollador) REFERENCES desarrollador(id_desarrollador) ON DELETE CASCADE,
    FOREIGN KEY (id_juego)         REFERENCES juego(id_juego)                 ON DELETE CASCADE
);

CREATE TABLE compra (
    cod_compra  INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario  INT NOT NULL,
    id_juego    INT NOT NULL,
    cantidad    INT           NOT NULL DEFAULT 1,
    coste       DECIMAL(10,2) NOT NULL,
    fecha       DATE          NOT NULL DEFAULT (CURRENT_DATE),
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)  ON DELETE CASCADE,
    FOREIGN KEY (id_juego)   REFERENCES juego(id_juego)      ON DELETE CASCADE
);

CREATE TABLE resena (
    id_resena   INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario  INT NOT NULL,
    id_juego    INT NOT NULL,
    comentario  TEXT,
    puntuacion  TINYINT NOT NULL CHECK (puntuacion BETWEEN 1 AND 10),
    idioma      VARCHAR(50) DEFAULT 'Español',
    fecha       DATE NOT NULL DEFAULT (CURRENT_DATE),
    UNIQUE KEY uq_usuario_juego (id_usuario, id_juego),
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE,
    FOREIGN KEY (id_juego)   REFERENCES juego(id_juego)     ON DELETE CASCADE
);

-- ── Datos de demostración ────────────────────────────────────

-- Administradores
INSERT INTO administrador (nombre, apellidos, correo, contrasena) VALUES
('Admin', 'Principal', 'admin@gamepyramid.com', 'admin123');

-- Estudios
INSERT INTO estudio (nombre) VALUES
('Rockstar Games'),
('CD Projekt Red'),
('Nintendo EPD');

-- Desarrolladores
INSERT INTO desarrollador (nombre, apellidos, anos_experiencia, puesto_actual, id_estudio) VALUES
('Dan',      'Houser',   20, 'Director Creativo', 1),
('Sam',      'Houser',   20, 'Productor',         1),
('Adam',     'Badowski', 18, 'Director de Juego', 2),
('Shigeru',  'Miyamoto', 40, 'Productor',         3);

-- Juegos
INSERT INTO juego (titulo, genero, plataforma, precio, stock, director, id_estudio) VALUES
('Grand Theft Auto V',          'Acción',   'PC / PS5 / Xbox', 29.99, 50, 'Dan Houser',      1),
('Red Dead Redemption 2',       'Aventura', 'PC / PS4 / Xbox', 39.99, 30, 'Dan Houser',      1),
('The Witcher 3',                'RPG',      'PC / PS5 / Xbox', 19.99, 45, 'Adam Badowski',   2),
('Cyberpunk 2077',               'RPG',      'PC / PS5 / Xbox', 34.99, 40, 'Adam Badowski',   2),
('The Legend of Zelda: BotW',   'Aventura', 'Switch',          59.99, 20, 'Shigeru Miyamoto',3),
('Mario Kart 8 Deluxe',         'Carreras', 'Switch',          49.99, 35, 'Shigeru Miyamoto',3);

-- Relaciones desarrollador ↔ juego
INSERT INTO desarrollador_juego VALUES (1,1),(1,2),(2,1),(2,2),(3,3),(3,4),(4,5),(4,6);

-- Usuarios
INSERT INTO usuario (nombre, apellidos, correo, contrasena, saldo, idioma) VALUES
('Carlos', 'García',   'carlos@email.com', 'pass123', 200.00, 'Español'),
('Ana',    'Martínez', 'ana@email.com',    'pass123', 150.00, 'Español'),
('John',   'Smith',    'john@email.com',   'pass123', 300.00, 'English');

-- Compras demo  (reducen el stock manualmente; triggers no incluidos para simplicidad)
INSERT INTO compra (id_usuario, id_juego, cantidad, coste, fecha) VALUES
(1, 1, 1, 29.99, CURDATE()),
(1, 3, 1, 19.99, CURDATE()),
(2, 5, 1, 59.99, CURDATE()),
(3, 1, 1, 29.99, CURDATE()),
(3, 4, 1, 34.99, CURDATE());

-- Actualizar stock tras compras demo
UPDATE juego SET stock = stock - 2 WHERE id_juego = 1;
UPDATE juego SET stock = stock - 1 WHERE id_juego = 3;
UPDATE juego SET stock = stock - 1 WHERE id_juego = 4;
UPDATE juego SET stock = stock - 1 WHERE id_juego = 5;

-- Reseñas demo
INSERT INTO resena (id_usuario, id_juego, comentario, puntuacion, idioma, fecha) VALUES
(1, 1, 'Increíble juego, horas y horas de entretenimiento.', 9, 'Español', CURDATE()),
(1, 3, 'El mejor RPG que he jugado nunca.',                  10,'Español', CURDATE()),
(2, 5, 'Una obra maestra de Nintendo.',                      10,'Español', CURDATE()),
(3, 1, 'Fantastic open world experience!',                    8, 'English', CURDATE()),
(3, 4, 'Buggy at launch but now great.',                      7, 'English', CURDATE());
