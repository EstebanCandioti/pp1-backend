CREATE TABLE usuario (
    id_usuario INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    correo VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(20) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    direccion VARCHAR(255) NOT NULL,
    es_usuario_restaurante BOOLEAN NOT NULL,
    activo BOOLEAN NOT NULL
);

CREATE TABLE pedido (
    id_pedido INT PRIMARY KEY AUTO_INCREMENT,
    fecha_pedido DATE NOT NULL,
    estado VARCHAR(20) NOT NULL,
    cantidad_personas INT NOT NULL,
    id_usuario INT NOT NULL,
    CONSTRAINT fk_pedido_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

CREATE TABLE notificacion (
    id_notificacion INT PRIMARY KEY AUTO_INCREMENT,
    fecha_envio DATE,
    asunto VARCHAR(100),
    mensaje VARCHAR(200),
    id_usuario INT NOT NULL,
    CONSTRAINT fk_notificacion_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

CREATE TABLE menuDia (
    id_menu_dia INT PRIMARY KEY AUTO_INCREMENT,
    fecha DATE,
    descripcion VARCHAR(250),
    publicado BOOLEAN,
    stock_total INT,
    id_usuario_creador INT NOT NULL,
    CONSTRAINT fk_menudia_creador
        FOREIGN KEY (id_usuario_creador) REFERENCES usuario(id_usuario)
);

CREATE TABLE plato (
    id_plato INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100),
    descripcion VARCHAR(200),
    imagen VARCHAR(500),
    categoria VARCHAR(200)
);

CREATE TABLE pedidoDia (
    id_pedido_dia INT PRIMARY KEY AUTO_INCREMENT,
    fecha_entrega DATE,
    id_pedido INT NOT NULL,
    id_menu_dia INT NOT NULL,
    id_plato INT NOT NULL,
    CONSTRAINT fk_pedidodia_pedido
        FOREIGN KEY (id_pedido) REFERENCES pedido(id_pedido),
    CONSTRAINT fk_pedidodia_menu
        FOREIGN KEY (id_menu_dia) REFERENCES menuDia(id_menu_dia),
    CONSTRAINT fk_pedidodia_plato
        FOREIGN KEY (id_plato) REFERENCES plato(id_plato)
);

CREATE TABLE menuPlato (
    id_menu_plato INT PRIMARY KEY AUTO_INCREMENT,
    id_plato INT NOT NULL,
    id_menu_dia INT NOT NULL,
    stock_disponible INT,
    CONSTRAINT fk_menuplato_plato
        FOREIGN KEY (id_plato) REFERENCES plato(id_plato),
    CONSTRAINT fk_menuplato_menu
        FOREIGN KEY (id_menu_dia) REFERENCES menuDia(id_menu_dia)
);