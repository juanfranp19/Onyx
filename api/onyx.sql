-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 08-01-2026 a las 13:08:27
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `onyx`
--

-- --------------------------------------------------------



--
-- Estructura de tabla para la tabla `comentario`
--

CREATE TABLE `comentario` (
  `id` int(9) NOT NULL AUTO_INCREMENT,
  `contenido` varchar(255) NOT NULL,
  `usuario_id` int(9) NOT NULL,
  `tarea_id` int(9) NOT NULL,
  `fecha_creación` datetime(6) DEFAULT current_timestamp(6),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `grupos`
--

CREATE TABLE `grupos` (
  `id` int(9) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(30) NOT NULL,
  `descripcion` varchar(9999) DEFAULT NULL,
  `creador_id` int(9) NOT NULL,
  `fecha_creación` datetime(6) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------





--
-- Estructura de tabla para la tabla `tareas`
--

CREATE TABLE `tareas` (
  `id` int(9) NOT NULL AUTO_INCREMENT,
  `titulo` varchar(30) NOT NULL,
  `descripcion` varchar(9999) DEFAULT NULL,
  `fecha_vencimiento` datetime(6) NOT NULL,

  `creador_id` int(9) NOT NULL,
  `fecha_creacion` datetime(6) NOT NULL DEFAULT current_timestamp(6),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

CREATE TABLE `usuarios` (
  `id` int(9) NOT NULL AUTO_INCREMENT,
  `nombre_usuario` varchar(16) NOT NULL,
  `email` varchar(30) NOT NULL,
  `password_hash` varchar(30) NOT NULL,
  `fecha_registro` datetime(6) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `comentario`
--
ALTER TABLE `comentario`
  ADD KEY `usuario-comentario` (`usuario_id`),
  ADD KEY `tarea-comentario` (`tarea_id`);

--
-- Indices de la tabla `grupos`
--
ALTER TABLE `grupos`
  ADD KEY `creador-grupo` (`creador_id`);



--
-- Indices de la tabla `tareas`
--
ALTER TABLE `tareas`

  ADD KEY `creador-tarea` (`creador_id`);

--
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD UNIQUE KEY `email` (`email`);

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `comentario`
--
ALTER TABLE `comentario`
  ADD CONSTRAINT `comentario_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`),
  ADD CONSTRAINT `comentario_ibfk_2` FOREIGN KEY (`tarea_id`) REFERENCES `tareas` (`id`);

--
-- Filtros para la tabla `grupos`
--
ALTER TABLE `grupos`
  ADD CONSTRAINT `grupos_ibfk_1` FOREIGN KEY (`creador_id`) REFERENCES `usuarios` (`id`);

--
-- Filtros para la tabla `tareas`
--
ALTER TABLE `tareas`

  ADD CONSTRAINT `tareas_ibfk_2` FOREIGN KEY (`creador_id`) REFERENCES `usuarios` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
