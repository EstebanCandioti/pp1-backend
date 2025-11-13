/*============================ USUARIOS ===========================*/
INSERT INTO usuario (
    nombre, apellido, correo, password, telefono, direccion,
    es_usuario_restaurante, activo
) VALUES
('Ana',  'Gonzalez', 'ana.gonzalez@empresa.com', '123456', '3511111111', 'Calle 123', FALSE, TRUE),
('Luis', 'Martinez', 'luis.martinez@empresa.com', '123456', '3512222222', 'Av. Siempreviva 742', FALSE, TRUE),
('Sofia','Lopez',    'sofia.lopez@aromaslight.com', '123456', '3513333333', 'Cocina Aromas Light', TRUE, TRUE);

/*========================= PLATOS ================================*/
INSERT INTO plato (nombre, descripcion, imagen, categoria) VALUES
('Milanesa con puré', 'Milanesa con puré de papas', NULL, 'Principal'),
('Ensalada César', 'Ensalada con pollo y croutons', NULL, 'Ensalada'),
('Pasta bolognesa', 'Fideos con salsa bolognesa', NULL, 'Pasta'),
('Tarta de verduras', 'Tarta de zapallito y zanahoria', NULL, 'Vegetariano'),
('Pollo al horno', 'Pollo marinado con papas', NULL, 'Principal'),
('Wok de vegetales', 'Salteado de verduras con soja', NULL, 'Vegano'),
('Hamburguesa completa', 'Hamburguesa con queso y tomate', NULL, 'Sandwich'),
('Sopa de calabaza', 'Sopa crema de calabaza', NULL, 'Entrada');


/*====================== SE CREAN LOS 5 DIAS ========================*/
INSERT INTO menuDia (fecha, descripcion, publicado, stock_total, id_usuario_creador) VALUES
('2024-10-07', 'Menú lunes',     TRUE, 40, 3),
('2024-10-08', 'Menú martes',    TRUE, 40, 3),
('2024-10-09', 'Menú miércoles', TRUE, 40, 3),
('2024-10-10', 'Menú jueves',    TRUE, 40, 3),
('2024-10-11', 'Menú viernes',   TRUE, 40, 3);

/*============================ PLATOS PARA CADA DIA ===============================*/
INSERT INTO menuPlato (id_plato, id_menu_dia, stock_disponible)
SELECT id_plato, 1, 10 FROM plato WHERE nombre IN
('Milanesa con puré', 'Ensalada César', 'Sopa de calabaza');


INSERT INTO menuPlato (id_plato, id_menu_dia, stock_disponible)
SELECT id_plato, 2, 10 FROM plato WHERE nombre IN
('Pasta bolognesa', 'Tarta de verduras', 'Hamburguesa completa');

INSERT INTO menuPlato (id_plato, id_menu_dia, stock_disponible)
SELECT id_plato, 3, 10 FROM plato WHERE nombre IN
('Pollo al horno', 'Ensalada César', 'Wok de vegetales');


INSERT INTO menuPlato (id_plato, id_menu_dia, stock_disponible)
SELECT id_plato, 4, 10 FROM plato WHERE nombre IN
('Sopa de calabaza', 'Tarta de verduras', 'Pasta bolognesa');


INSERT INTO menuPlato (id_plato, id_menu_dia, stock_disponible)
SELECT id_plato, 5, 10 FROM plato WHERE nombre IN
('Hamburguesa completa', 'Milanesa con puré', 'Wok de vegetales');

/*==================== PEDIDOS DE LOS CLIENTES =================*/
INSERT INTO pedido (fecha_pedido, estado, cantidad_personas, id_usuario) VALUES
(CURRENT_DATE, 'Pendiente', 1, 1),
(CURRENT_DATE, 'Pendiente', 1, 2);

/*============= DETALLE DE LOS PEDIDOS =====================*/
INSERT INTO pedidoDia (fecha_entrega, id_pedido, id_menu_dia, id_plato)
VALUES
('2024-10-07', 1, 1, (SELECT id_plato FROM plato WHERE nombre='Milanesa con puré')),
('2024-10-08', 1, 2, (SELECT id_plato FROM plato WHERE nombre='Pasta bolognesa'));

INSERT INTO pedidoDia (fecha_entrega, id_pedido, id_menu_dia, id_plato)
VALUES
('2024-10-09', 2, 3, (SELECT id_plato FROM plato WHERE nombre='Pollo al horno')),
('2024-10-10', 2, 4, (SELECT id_plato FROM plato WHERE nombre='Sopa de calabaza'));


/*======================== NOTIFICACIONES =====================*/
INSERT INTO notificacion (fecha_envio, asunto, mensaje, id_usuario) VALUES
(CURRENT_DATE, 'Recordatorio de pedido', 'No olvides confirmar tu menú de la semana.', 1),
(CURRENT_DATE, 'Menú actualizado', 'El menú del jueves fue actualizado.', 2),
(CURRENT_DATE, 'Pedido confirmado', 'Tu pedido ha sido confirmado exitosamente.', 1);