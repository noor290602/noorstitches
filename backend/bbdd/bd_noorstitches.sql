DROP DATABASE IF EXISTS noorstitches;
CREATE SCHEMA `noorstitches` DEFAULT CHARACTER SET latin1 COLLATE latin1_spanish_ci;
USE noorstitches;

-- Tabla usuarios
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    apellidos VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    passwd VARCHAR(255) NOT NULL,
    foto_perfil VARCHAR(255) NOT NULL DEFAULT 'user.png',
    telefono VARCHAR(15) NOT NULL,
    direccion VARCHAR(255) NOT NULL,
    ciudad VARCHAR(100) NOT NULL,
    provincia VARCHAR(100) NOT NULL,
    codigo_postal VARCHAR(10) NOT NULL,
    pais VARCHAR(100) NOT NULL
);


-- Tabla de categorías
CREATE TABLE categorias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL
);

-- Tabla de subcategorías
CREATE TABLE subcategorias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    imagen VARCHAR(255) NOT NULL,
    id_categoria BIGINT NOT NULL,
    FOREIGN KEY (id_categoria) REFERENCES categorias(id)
);

-- Tabla productos
CREATE TABLE productos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion TEXT,
    imagen VARCHAR(255) NOT NULL,
    precio DECIMAL(10, 2) NOT NULL,
    peso DECIMAL(6, 2), -- opcional, en gramos
    longitud VARCHAR(255), -- opcional, en metros o cm
    material VARCHAR(255), -- opcional: algodón, acrílico, etc.
    composicion VARCHAR(255), -- opcional: 60% algodón, 40% acrílico
    marca VARCHAR(255),
    activo boolean default true,
	es_destacado boolean default false,
	id_subcategoria BIGINT NOT NULL,
    FOREIGN KEY (id_subcategoria) REFERENCES subcategorias(id)
);

-- Tabla pedidos
CREATE TABLE pedidos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT NOT NULL,
    fecha DATETIME NOT NULL,
    importe DECIMAL(10, 2) NOT NULL, 
    estado ENUM('pendiente', 'completado', 'cancelado') DEFAULT 'pendiente' NOT NULL,
    -- num_seguimiento VARCHAR(255),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
);

-- Tabla lineas de pedido: cuantos articulos metes, la cantidad
CREATE TABLE lineas_pedido (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_pedido BIGINT NOT NULL,
    id_producto BIGINT NOT NULL,
    cantidad INT NOT NULL,
    importe FLOAT NOT NULL,
    FOREIGN KEY (id_pedido) REFERENCES pedidos(id),
    FOREIGN KEY (id_producto) REFERENCES productos(id)
);

-- Tabla productos guardados (Lista de deseos)
CREATE TABLE productos_guardados (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT NOT NULL,
    id_producto BIGINT NOT NULL,
    -- fecha_guardado DATETIME NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id),
    FOREIGN KEY (id_producto) REFERENCES productos(id)
);

-- Tabla sessiones para cookies
CREATE TABLE sessions (
	id VARCHAR(100) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES usuarios(id)
);
