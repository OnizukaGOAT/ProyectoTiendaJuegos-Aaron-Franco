-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: gamepyramid
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `administrador`
--

DROP TABLE IF EXISTS `administrador`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `administrador` (
  `id_admin` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `apellidos` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `correo` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `contrasena` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id_admin`),
  UNIQUE KEY `correo` (`correo`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `administrador`
--

LOCK TABLES `administrador` WRITE;
/*!40000 ALTER TABLE `administrador` DISABLE KEYS */;
INSERT INTO `administrador` VALUES (1,'Admin','Principal','admin@gamepyramid.com','admin123');
/*!40000 ALTER TABLE `administrador` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `compra`
--

DROP TABLE IF EXISTS `compra`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `compra` (
  `cod_compra` int NOT NULL AUTO_INCREMENT,
  `id_usuario` int NOT NULL,
  `id_juego` int NOT NULL,
  `cantidad` int NOT NULL DEFAULT '1',
  `coste` decimal(10,2) NOT NULL,
  `fecha` date NOT NULL DEFAULT (curdate()),
  PRIMARY KEY (`cod_compra`),
  KEY `id_usuario` (`id_usuario`),
  KEY `id_juego` (`id_juego`),
  CONSTRAINT `compra_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`) ON DELETE CASCADE,
  CONSTRAINT `compra_ibfk_2` FOREIGN KEY (`id_juego`) REFERENCES `juego` (`id_juego`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=87 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `compra`
--

LOCK TABLES `compra` WRITE;
/*!40000 ALTER TABLE `compra` DISABLE KEYS */;
INSERT INTO `compra` VALUES (1,1,1,1,29.99,'2026-05-19'),(2,1,3,1,19.99,'2026-05-19'),(3,2,5,1,59.99,'2026-05-19'),(4,3,1,1,29.99,'2026-05-19'),(5,3,4,1,34.99,'2026-05-19'),(6,1,7,1,20.00,'2026-05-19'),(7,1,4,1,34.99,'2026-05-19'),(8,4,11,1,59.99,'2026-05-20'),(9,5,11,1,59.99,'2026-05-20'),(10,6,11,1,59.99,'2026-05-20'),(11,7,11,1,59.99,'2026-05-20'),(12,8,11,1,59.99,'2026-05-20'),(13,9,11,1,59.99,'2026-05-20'),(14,10,11,1,59.99,'2026-05-20'),(15,11,11,1,59.99,'2026-05-20'),(16,12,11,1,59.99,'2026-05-20'),(17,13,11,1,59.99,'2026-05-20'),(18,4,17,1,19.99,'2026-05-20'),(19,5,17,1,19.99,'2026-05-20'),(20,6,17,1,19.99,'2026-05-20'),(21,7,17,1,19.99,'2026-05-20'),(22,8,17,1,19.99,'2026-05-20'),(23,9,17,1,19.99,'2026-05-20'),(24,10,17,1,19.99,'2026-05-20'),(25,4,21,1,49.99,'2026-05-20'),(26,5,21,1,49.99,'2026-05-20'),(27,6,21,1,49.99,'2026-05-20'),(28,7,21,1,49.99,'2026-05-20'),(29,8,21,1,49.99,'2026-05-20'),(30,9,21,1,49.99,'2026-05-20'),(31,10,14,1,69.99,'2026-05-20'),(32,11,14,1,69.99,'2026-05-20'),(33,12,14,1,69.99,'2026-05-20'),(34,13,14,1,69.99,'2026-05-20'),(35,4,15,1,49.99,'2026-05-20'),(36,5,15,1,49.99,'2026-05-20'),(37,6,15,1,49.99,'2026-05-20'),(38,7,15,1,49.99,'2026-05-20'),(39,1,26,1,24.99,'2026-05-20'),(40,1,27,1,39.99,'2026-05-20'),(41,1,28,1,19.99,'2026-05-20'),(42,1,29,1,49.99,'2026-05-20'),(43,1,30,1,29.99,'2026-05-20'),(44,3,31,1,19.99,'2026-05-20'),(45,3,32,1,14.99,'2026-05-20'),(46,3,33,1,29.99,'2026-05-20'),(47,2,34,1,14.99,'2026-05-20'),(48,2,35,1,34.99,'2026-05-20'),(49,2,3,1,19.99,'2026-05-20'),(50,2,4,1,34.99,'2026-05-20'),(51,4,36,1,69.99,'2026-05-20'),(52,4,37,1,49.99,'2026-05-20'),(53,4,38,1,44.99,'2026-05-20'),(54,5,36,1,69.99,'2026-05-20'),(55,6,36,1,69.99,'2026-05-20'),(56,7,27,1,39.99,'2026-05-20'),(57,8,32,1,14.99,'2026-05-20'),(58,9,29,1,49.99,'2026-05-20'),(59,10,35,1,34.99,'2026-05-20'),(60,11,37,1,49.99,'2026-05-20'),(61,12,31,1,19.99,'2026-05-20'),(62,13,36,1,69.99,'2026-05-20'),(63,1,26,1,24.99,'2026-05-20'),(64,1,27,1,39.99,'2026-05-20'),(65,1,28,1,19.99,'2026-05-20'),(66,1,29,1,49.99,'2026-05-20'),(67,1,30,1,29.99,'2026-05-20'),(68,3,31,1,19.99,'2026-05-20'),(69,3,32,1,14.99,'2026-05-20'),(70,3,33,1,29.99,'2026-05-20'),(71,2,34,1,14.99,'2026-05-20'),(72,2,35,1,34.99,'2026-05-20'),(73,2,3,1,19.99,'2026-05-20'),(74,2,4,1,34.99,'2026-05-20'),(75,4,36,1,69.99,'2026-05-20'),(76,4,37,1,49.99,'2026-05-20'),(77,4,38,1,44.99,'2026-05-20'),(78,5,36,1,69.99,'2026-05-20'),(79,6,36,1,69.99,'2026-05-20'),(80,7,27,1,39.99,'2026-05-20'),(81,8,32,1,14.99,'2026-05-20'),(82,9,29,1,49.99,'2026-05-20'),(83,10,35,1,34.99,'2026-05-20'),(84,11,37,1,49.99,'2026-05-20'),(85,12,31,1,19.99,'2026-05-20'),(86,13,36,1,69.99,'2026-05-20');
/*!40000 ALTER TABLE `compra` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `desarrollador`
--

DROP TABLE IF EXISTS `desarrollador`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `desarrollador` (
  `id_desarrollador` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `apellidos` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `anos_experiencia` int DEFAULT '0',
  `puesto_actual` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `id_estudio` int DEFAULT NULL,
  PRIMARY KEY (`id_desarrollador`),
  KEY `id_estudio` (`id_estudio`),
  CONSTRAINT `desarrollador_ibfk_1` FOREIGN KEY (`id_estudio`) REFERENCES `estudio` (`id_estudio`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `desarrollador`
--

LOCK TABLES `desarrollador` WRITE;
/*!40000 ALTER TABLE `desarrollador` DISABLE KEYS */;
INSERT INTO `desarrollador` VALUES (1,'Dan','Houser',20,'Director Creativo',1),(2,'Sam','Houser',20,'Productor',1),(3,'Adam','Badowski',18,'Director de Juego',2),(4,'Shigeru','Miyamoto',40,'Productor',3),(5,'Tetsuya','Nomura',30,'Director',4),(6,'Gabe','Newell',30,'Director Ejecutivo',5),(7,'Hidetaka','Miyazaki',22,'Director Creativo',6),(8,'Alex','Hutchinson',18,'Director de Juego',7),(9,'Cory','Barlog',20,'Director Narrativo',8),(10,'Neil','Druckmann',19,'Copresidente',9),(11,'Jeff','Kaplan',20,'Diseñador Principal',10),(12,'Todd','Howard',28,'Director Ejecutivo',11),(13,'Hideaki','Itsuno',24,'Director',12),(14,'Toshihiro','Nagoshi',30,'Productor',13),(15,'Jason','Jones',25,'Director Técnico',14),(16,'Bryan','Intihar',17,'Director Creativo',15),(17,'Yoko','Taro',21,'Director',13),(18,'Amy','Hennig',26,'Guionista',9),(19,'Koji','Igarashi',28,'Productor',12),(20,'Josef','Fares',15,'Director',7);
/*!40000 ALTER TABLE `desarrollador` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `desarrollador_juego`
--

DROP TABLE IF EXISTS `desarrollador_juego`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `desarrollador_juego` (
  `id_desarrollador` int NOT NULL,
  `id_juego` int NOT NULL,
  PRIMARY KEY (`id_desarrollador`,`id_juego`),
  KEY `id_juego` (`id_juego`),
  CONSTRAINT `desarrollador_juego_ibfk_1` FOREIGN KEY (`id_desarrollador`) REFERENCES `desarrollador` (`id_desarrollador`) ON DELETE CASCADE,
  CONSTRAINT `desarrollador_juego_ibfk_2` FOREIGN KEY (`id_juego`) REFERENCES `juego` (`id_juego`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `desarrollador_juego`
--

LOCK TABLES `desarrollador_juego` WRITE;
/*!40000 ALTER TABLE `desarrollador_juego` DISABLE KEYS */;
INSERT INTO `desarrollador_juego` VALUES (1,1),(2,1),(1,2),(2,2),(3,3),(3,4),(4,5),(4,6),(5,7),(5,8),(6,9),(6,10),(7,11),(7,12),(8,13),(9,14),(10,15),(11,16),(12,17),(13,18),(14,19),(15,20),(16,21),(17,22),(18,23),(19,24),(20,25),(5,26),(5,27),(5,28),(5,29),(5,30),(1,31),(2,31),(1,32),(2,32),(1,33),(2,33),(3,34),(3,35),(4,36),(4,37),(4,38);
/*!40000 ALTER TABLE `desarrollador_juego` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `estudio`
--

DROP TABLE IF EXISTS `estudio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `estudio` (
  `id_estudio` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id_estudio`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `estudio`
--

LOCK TABLES `estudio` WRITE;
/*!40000 ALTER TABLE `estudio` DISABLE KEYS */;
INSERT INTO `estudio` VALUES (1,'Rockstar Games'),(2,'CD Projekt Red'),(3,'Nintendo EPD'),(4,'SquareEnix'),(5,'Valve'),(6,'FromSoftware'),(7,'Ubisoft Montreal'),(8,'Santa Monica Studio'),(9,'Naughty Dog'),(10,'Blizzard Entertainment'),(11,'Bethesda Game Studios'),(12,'Capcom'),(13,'SEGA'),(14,'Bungie'),(15,'Insomniac Games');
/*!40000 ALTER TABLE `estudio` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `juego`
--

DROP TABLE IF EXISTS `juego`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `juego` (
  `id_juego` int NOT NULL AUTO_INCREMENT,
  `titulo` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `genero` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `plataforma` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `precio` decimal(10,2) NOT NULL DEFAULT '0.00',
  `stock` int NOT NULL DEFAULT '0',
  `director` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `id_estudio` int DEFAULT NULL,
  PRIMARY KEY (`id_juego`),
  KEY `id_estudio` (`id_estudio`),
  CONSTRAINT `juego_ibfk_1` FOREIGN KEY (`id_estudio`) REFERENCES `estudio` (`id_estudio`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `juego`
--

LOCK TABLES `juego` WRITE;
/*!40000 ALTER TABLE `juego` DISABLE KEYS */;
INSERT INTO `juego` VALUES (1,'Grand Theft Auto V','Acción','PC / PS5 / Xbox',29.99,48,'Dan Houser',1),(2,'Red Dead Redemption 2','Aventura','PC / PS4 / Xbox',39.99,30,'Dan Houser',1),(3,'The Witcher 3','RPG','PC / PS5 / Xbox',19.99,44,'Adam Badowski',2),(4,'Cyberpunk 2077','RPG','PC / PS5 / Xbox',34.99,38,'Adam Badowski',2),(5,'The Legend of Zelda: BotW','Aventura','Switch',59.99,19,'Shigeru Miyamoto',3),(6,'Mario Kart 8 Deluxe','Carreras','Switch',49.99,35,'Shigeru Miyamoto',3),(7,'Kingdom Hearts','Acción, RPG','PS2, PS3, PS4, PS5, PC.',20.00,99,'Tetsuya Nomura',4),(8,'Final Fantasy X','JRPG','PS2',10.00,100,'Tetsuya Nomura',4),(9,'Half-Life 2','FPS','PC',14.99,120,'Gabe Newell',5),(10,'Portal 2','Puzles','PC / PS3 / Xbox 360',9.99,140,'Gabe Newell',5),(11,'Elden Ring','RPG','PC / PS5 / Xbox',59.99,250,'Hidetaka Miyazaki',6),(12,'Dark Souls III','RPG','PC / PS4 / Xbox',39.99,150,'Hidetaka Miyazaki',6),(13,'Assassin\'s Creed Odyssey','Acción RPG','PC / PS4 / Xbox',29.99,180,'Alex Hutchinson',7),(14,'God of War Ragnarök','Acción','PS5',69.99,220,'Cory Barlog',8),(15,'The Last of Us Part II','Aventura','PS4 / PS5',49.99,200,'Neil Druckmann',9),(16,'Overwatch 2','Hero Shooter','PC / Consolas',0.00,999,'Jeff Kaplan',10),(17,'The Elder Scrolls V: Skyrim','RPG','PC / PS5 / Xbox / Switch',19.99,300,'Todd Howard',11),(18,'Devil May Cry 5','Hack and Slash','PC / PS5 / Xbox',24.99,130,'Hideaki Itsuno',12),(19,'Yakuza: Like a Dragon','JRPG','PC / PS5 / Xbox',34.99,110,'Toshihiro Nagoshi',13),(20,'Destiny 2','FPS Online','PC / PS5 / Xbox',0.00,999,'Jason Jones',14),(21,'Marvel\'s Spider-Man','Acción','PS4 / PS5 / PC',49.99,270,'Bryan Intihar',15),(22,'NieR: Automata','Acción RPG','PC / PS4 / Xbox',29.99,170,'Yoko Taro',13),(23,'Uncharted 4','Aventura','PS4 / PS5',24.99,160,'Amy Hennig',9),(24,'Castlevania: Symphony of the Night','Metroidvania','PS1 / PSP',9.99,90,'Koji Igarashi',12),(25,'It Takes Two','Cooperativo','PC / PS5 / Xbox / Switch',39.99,145,'Josef Fares',7),(26,'Kingdom Hearts II','Acción RPG','PS2 / PS3 / PS4',24.99,120,'Tetsuya Nomura',4),(27,'Kingdom Hearts III','Acción RPG','PS4 / Xbox / PC',39.99,180,'Tetsuya Nomura',4),(28,'Kingdom Hearts Birth by Sleep','Acción RPG','PSP / PS4',19.99,90,'Tetsuya Nomura',4),(29,'Final Fantasy VII Remake','JRPG','PS4 / PS5 / PC',49.99,210,'Tetsuya Nomura',4),(30,'Final Fantasy XV','JRPG','PS4 / Xbox / PC',29.99,160,'Tetsuya Nomura',4),(31,'GTA IV','Acción','PC / PS3 / Xbox 360',19.99,130,'Dan Houser',1),(32,'GTA San Andreas','Acción','PC / PS2 / Xbox',14.99,250,'Dan Houser',1),(33,'Red Dead Redemption','Aventura','PS3 / Xbox / Switch',29.99,140,'Dan Houser',1),(34,'The Witcher 2','RPG','PC / Xbox 360',14.99,100,'Adam Badowski',2),(35,'Cyberpunk Phantom Liberty','RPG','PC / PS5 / Xbox',34.99,190,'Adam Badowski',2),(36,'Zelda Tears of the Kingdom','Aventura','Switch',69.99,320,'Shigeru Miyamoto',3),(37,'Super Mario Odyssey','Plataformas','Switch',49.99,280,'Shigeru Miyamoto',3),(38,'Luigi\'s Mansion 3','Aventura','Switch',44.99,150,'Shigeru Miyamoto',3);
/*!40000 ALTER TABLE `juego` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `resena`
--

DROP TABLE IF EXISTS `resena`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `resena` (
  `id_resena` int NOT NULL AUTO_INCREMENT,
  `id_usuario` int NOT NULL,
  `id_juego` int NOT NULL,
  `comentario` text COLLATE utf8mb4_unicode_ci,
  `puntuacion` tinyint NOT NULL,
  `idioma` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'Español',
  `fecha` date NOT NULL DEFAULT (curdate()),
  PRIMARY KEY (`id_resena`),
  UNIQUE KEY `uq_usuario_juego` (`id_usuario`,`id_juego`),
  KEY `id_juego` (`id_juego`),
  CONSTRAINT `resena_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`) ON DELETE CASCADE,
  CONSTRAINT `resena_ibfk_2` FOREIGN KEY (`id_juego`) REFERENCES `juego` (`id_juego`) ON DELETE CASCADE,
  CONSTRAINT `resena_chk_1` CHECK ((`puntuacion` between 1 and 10))
) ENGINE=InnoDB AUTO_INCREMENT=321 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resena`
--

LOCK TABLES `resena` WRITE;
/*!40000 ALTER TABLE `resena` DISABLE KEYS */;
INSERT INTO `resena` VALUES (1,1,1,'Increíble juego, horas y horas de entretenimiento.',9,'Español','2026-05-19'),(2,1,3,'El mejor RPG que he jugado nunca.',10,'Español','2026-05-19'),(3,2,5,'Una obra maestra de Nintendo.',10,'Español','2026-05-19'),(4,3,1,'Fantastic open world experience!',8,'English','2026-05-19'),(5,3,4,'Buggy at launch but now great.',7,'English','2026-05-19'),(6,1,7,'Nomura Cabrón',3,'Español','2026-05-19'),(7,1,4,'Buena estética, Keanu Reeves no sale desnudo :(',7,'Español','2026-05-19'),(8,4,11,'Una experiencia RPG impresionante y enorme.',10,'Español','2026-05-20'),(9,5,11,'Un monde incroyable et des combats excellents.',10,'Français','2026-05-20'),(10,6,11,'Capolavoro assoluto del genere soulslike.',10,'Italiano','2026-05-20'),(11,7,11,'Best open world RPG in years.',10,'English','2026-05-20'),(12,8,11,'Fantastisches Kampfsystem und grandiose Welt.',9,'Deutsch','2026-05-20'),(13,9,11,'????????????????',10,'???','2026-05-20'),(14,10,11,'Mundo gigantesco e viciante.',9,'Português','2026-05-20'),(15,11,11,'Otroligt beroendeframkallande spel.',9,'Svenska','2026-05-20'),(16,12,11,'Difícil pero extremadamente satisfactorio.',10,'Español','2026-05-20'),(17,13,11,'An unforgettable adventure.',10,'English','2026-05-20'),(18,4,17,'Skyrim nunca pasa de moda.',10,'Español','2026-05-20'),(19,5,17,'Toujours aussi amusant après toutes ces années.',9,'Français','2026-05-20'),(20,6,17,'Libertà totale e centinaia di ore di gioco.',10,'Italiano','2026-05-20'),(21,7,17,'Still one of the greatest RPGs ever made.',10,'English','2026-05-20'),(22,8,17,'Atmosphäre und Musik sind perfekt.',9,'Deutsch','2026-05-20'),(23,9,17,'??????????',9,'???','2026-05-20'),(24,10,17,'Muito conteúdo e liberdade.',9,'Português','2026-05-20'),(25,4,21,'Balance perfecto entre historia y acción.',9,'Español','2026-05-20'),(26,5,21,'Le meilleur jeu de super-héros.',9,'Français','2026-05-20'),(27,6,21,'Movimento fluidissimo e spettacolare.',10,'Italiano','2026-05-20'),(28,7,21,'Swinging through New York feels amazing.',10,'English','2026-05-20'),(29,8,21,'Sehr spaßiges Gameplay.',9,'Deutsch','2026-05-20'),(30,9,21,'???????????????????',10,'???','2026-05-20'),(31,10,14,'Visualmente espectacular.',9,'Español','2026-05-20'),(32,11,14,'Fantastisk berättelse och action.',10,'Svenska','2026-05-20'),(33,12,14,'Kratos está mejor que nunca.',10,'Español','2026-05-20'),(34,13,14,'Epic conclusion to the Norse saga.',10,'English','2026-05-20'),(35,4,15,'Durísimo emocionalmente pero brillante.',10,'Español','2026-05-20'),(36,5,15,'Narration incroyable et personnages excellents.',10,'Français','2026-05-20'),(37,6,15,'Uno dei giochi più cinematografici mai creati.',9,'Italiano','2026-05-20'),(38,7,15,'Masterpiece storytelling.',10,'English','2026-05-20'),(39,8,9,'Half-Life 2 revolucionó los FPS.',10,'Español','2026-05-20'),(40,9,10,'Portal 2 sigue siendo divertidísimo.',9,'Español','2026-05-20'),(41,10,18,'Combate increíblemente satisfactorio.',9,'Español','2026-05-20'),(42,11,19,'Historia loca pero muy divertida.',8,'Español','2026-05-20'),(43,12,22,'Banda sonora y narrativa únicas.',10,'Español','2026-05-20'),(44,13,25,'Perfect game to play with friends or partner.',10,'English','2026-05-20'),(283,4,26,'El mejor Kingdom Hearts de largo.',10,'Español','2026-05-20'),(284,7,26,'Axel and Roxas are the best, i cry like a small child everytime I replay the game. I just wanted to meet Roxas... T-T',10,'Español','2026-05-20'),(285,1,27,'La historia es un caos absoluto pero me encanta.',8,'Español','2026-05-20'),(286,1,28,'Underrated total.',9,'English','2026-05-20'),(287,1,29,'Tetsuya Nomura no sabe cerrar una trama.',5,'Español','2026-05-20'),(288,1,30,'Buen combate pero historia horrible.',6,'Español','2026-05-20'),(289,2,34,'Muy infravalorado comparado con Witcher 3.',9,'Español','2026-05-20'),(290,2,35,'Sigue teniendo bugs ridículos.',4,'Español','2026-05-20'),(291,2,3,'Una obra maestra absoluta.',10,'Español','2026-05-20'),(292,2,4,'Keanu salva este desastre.',5,'Español','2026-05-20'),(293,3,31,'La mejor historia de GTA.',10,'English','2026-05-20'),(294,3,32,'CJ > cualquier protagonista moderno.',10,'Español','2026-05-20'),(295,3,33,'John Marston es top personajes de Rockstar.',10,'Español','2026-05-20'),(296,4,36,'Nintendo nunca falla con Zelda.',10,'Español','2026-05-20'),(297,4,37,'Literalmente imposible aburrirse.',10,'Español','2026-05-20'),(298,4,38,'Muy divertido pero demasiado fácil.',7,'Español','2026-05-20'),(299,5,36,'Incroyable mais les armes cassables sont agaçantes.',8,'Français','2026-05-20'),(300,6,36,'Troppo parecido a Breath of the Wild.',7,'Italiano','2026-05-20'),(301,7,27,'Kingdom Hearts fans pretending this story makes sense lol.',4,'English','2026-05-20'),(302,8,32,'Nostalgia pura, aunque gráficamente envejeció fatal.',7,'Español','2026-05-20'),(303,9,29,'??????????????',9,'???','2026-05-20'),(304,10,35,'Prometeron demasiado y no cumplieron.',3,'Português','2026-05-20'),(305,11,37,'Förmodligen det bästa Mario-spelet någonsin.',10,'Svenska','2026-05-20'),(306,12,31,'Conducir en GTA IV es horrible.',4,'Español','2026-05-20'),(307,13,36,'Game of the year easily.',10,'English','2026-05-20'),(308,7,4,'Still feels unfinished in some areas.',5,'English','2026-05-20'),(309,8,15,'La gente exagera muchísimo con este juego.',6,'Español','2026-05-20'),(310,9,14,'????????????????',7,'???','2026-05-20'),(311,10,21,'Muy repetitivo después de unas horas.',6,'Español','2026-05-20'),(312,11,17,'Demasiados bugs incluso hoy.',6,'Español','2026-05-20'),(313,13,22,'Pretentious story but amazing soundtrack.',7,'English','2026-05-20'),(314,5,27,'Nomura needs to stop adding random plot twists.',3,'English','2026-05-20'),(315,6,26,'Sora lleva 20 años sin madurar como personaje.',5,'Español','2026-05-20'),(316,7,32,'Quién diseñó las misiones del tren merece cárcel.',2,'Español','2026-05-20'),(317,8,35,'Cyberbug 2077.',1,'English','2026-05-20'),(318,11,15,'Aburrido y sobrevalorado.',3,'Español','2026-05-20'),(319,12,36,'Nintendo reciclando el mismo mapa y cobrando 70?.',4,'Español','2026-05-20'),(320,13,29,'Too many cringe anime grunts every 5 seconds.',5,'English','2026-05-20');
/*!40000 ALTER TABLE `resena` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `id_usuario` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `apellidos` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `correo` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `contrasena` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `saldo` decimal(10,2) DEFAULT '50.00',
  `idioma` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'Español',
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `correo` (`correo`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (1,'Carlos','García','carlos@email.com','pass123',495.01,'Español'),(2,'Ana','Martínez','ana@email.com','Ana123',150.00,'Español'),(3,'John','Smith','john@email.com','pass123',300.00,'English'),(4,'Lucía','Fernández','lucia@email.com','lucia123',420.00,'Español'),(5,'Pierre','Dubois','pierre@email.com','pierre123',310.00,'Français'),(6,'Luca','Bianchi','luca@email.com','luca123',280.00,'Italiano'),(7,'Emily','Johnson','emily@email.com','emily123',530.00,'English'),(8,'Hans','Muller','hans@email.com','hans123',390.00,'Deutsch'),(9,'Akira','Tanaka','akira123@email.com','akira123',800.00,'???'),(10,'Maria','Silva','maria@email.com','maria123',260.00,'Português'),(11,'Sven','Larsson','sven@email.com','sven123',190.00,'Svenska'),(12,'Fatima','Alvarez','fatima@email.com','fatima123',610.00,'Español'),(13,'Oliver','Brown','oliver@email.com','oliver123',720.00,'English');
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-20 13:53:37
