INSERT INTO plan (id, subscription_id, plan_type) VALUES 
(1, NULL, 'FREE'),
(2, NULL, 'FREE'),
(3, NULL, 'FREE'),
(4, 'sub_1R4QQbGa0d4217RGFKZprtnL', 'PREMIUM'),
(5, NULL, 'FREE'),
(6, NULL, 'FREE'),
(7, NULL, 'PREMIUM'),
(8, NULL, 'PREMIUM'),
(9, NULL, 'PREMIUM'),
(10, NULL, 'PREMIUM'),
(11, NULL, 'PREMIUM'),
(12, NULL, 'PREMIUM'),
(13, NULL, 'PREMIUM'),
(14, NULL, 'PREMIUM'),
(15, NULL, 'PREMIUM'),
(16, NULL, 'PREMIUM'),
(17, NULL, 'PREMIUM'),
(18, NULL, 'PREMIUM'),
(19, NULL, 'PREMIUM'),
(20, NULL, 'PREMIUM'),
(21, NULL, 'PREMIUM'),
(22, NULL, 'PREMIUM'),
(23, NULL, 'PREMIUM'),
(24, NULL, 'PREMIUM'),
(25, NULL, 'PREMIUM'),
(26, NULL, 'PREMIUM'),
(27, NULL, 'PREMIUM'),
(28, NULL, 'PREMIUM'),
(29, NULL, 'PREMIUM'),
(30, NULL, 'PREMIUM'),
(31, NULL, 'PREMIUM'),
(32, NULL, 'PREMIUM'),
(33, NULL, 'PREMIUM'),
(34, NULL, 'PREMIUM'),
(35, NULL, 'PREMIUM'),
(36, NULL, 'PREMIUM'),
(37, NULL, 'PREMIUM'),
(38, NULL, 'PREMIUM');

INSERT INTO user (id, name, email, password, telephone, plan_id) VALUES
(1, 'admin1', 'admin1@caronte.site', '$2a$10$EJ7goCEpyl.XTDXDf/jeVOz1Mfwn24Q4jbIXnwY9yLh9gJSghZecm', '626077466', 1),
(2, 'admin2', 'admin2@caronte.site', '$2a$10$EJ7goCEpyl.XTDXDf/jeVOz1Mfwn24Q4jbIXnwY9yLh9gJSghZecm', '626077466', 2),
(3, 'empresa1','empresa1@caronte.site', '$2a$10$7vxcruASLb/pJhbG1uJGgeb6RqSIBeHJub0UGXSzwJAYBd0LHPoGu', '645384175', 3),
(4, 'empresa2','empresa2@caronte.site', '$2a$10$7vxcruASLb/pJhbG1uJGgeb6RqSIBeHJub0UGXSzwJAYBd0LHPoGu', '957501307', 4),
(5, 'cliente1', 'cliente1@caronte.site', '$2a$10$.qePDyM..BhRQr829JMtGOuRLM7i0K6/im0tgGUWxHvoLQRTs9956', '637892263', 5),
(6, 'cliente2', 'cliente2@caronte.site', '$2a$10$.qePDyM..BhRQr829JMtGOuRLM7i0K6/im0tgGUWxHvoLQRTs9956', '643287465', 6),
(7, 'Funeraria El Descanso', 'descanso@caronte.site', 'pass', '600000001', 7),
(8, 'Floristería Rosas Blancas', 'rosasblancas@caronte.site', 'pass', '600000002', 8),
(9, 'Funeraria Paz Eterna', 'pazeterna@caronte.site', 'pass', '600000003', 9),
(10, 'Floristería El Jazmín', 'jazmin@caronte.site', 'pass', '600000004', 10),
(11, 'Funeraria Camino de Luz', 'caminoluz@caronte.site', 'pass', '600000005', 11),
(12, 'Floristería Aroma Floral', 'aromafloral@caronte.site', 'pass', '600000006', 12),
(13, 'Funeraria Vida Serena', 'vidaserena@caronte.site', 'pass', '600000007', 13),
(14, 'Floristería Petalos de Vida', 'petalos@caronte.site', 'pass', '600000008', 14),
(15, 'Funeraria Luz Celestial', 'luzcelestial@caronte.site', 'pass', '600000009', 15),
(16, 'Floristería El Ramo Perfecto', 'ramoperfecto@caronte.site', 'pass', '600000010', 16),
(17, 'Funeraria Último Adiós', 'ultimoadios@caronte.site', 'pass', '600000011', 17),
(18, 'Floristería Naturaleza Viva', 'naturalezaviva@caronte.site', 'pass', '600000012', 18),
(19, 'Floristería Primavera', 'primavera@caronte.site', 'pass', '600001001', 19),
(20, 'Floristería Tulipán', 'tulipan@caronte.site', 'pass', '600001002', 20),
(21, 'Floristería El Trébol', 'trebol@caronte.site', 'pass', '600001003', 21),
(22, 'Floristería La Flor de Lis', 'flordelis@caronte.site', 'pass', '600001004', 22),
(23, 'Floristería Aromas del Sur', 'aromas@caronte.site', 'pass', '600001005', 23),
(24, 'Notaría Santa Clara', 'notaria1@caronte.site', 'pass', '600001006', 24),
(25, 'Notaría San Jorge', 'notaria2@caronte.site', 'pass', '600001007', 25),
(26, 'Notaría del Centro', 'notaria3@caronte.site', 'pass', '600001008', 26),
(27, 'Notaría Las Columnas', 'notaria4@caronte.site', 'pass', '600001009', 27),
(28, 'Notaría Plaza Mayor', 'notaria5@caronte.site', 'pass', '600001010', 28),
(29, 'Funeraria Cielo Azul', 'funeraria1@caronte.site', 'pass', '600001011', 29),
(30, 'Funeraria Descanso Eterno', 'funeraria2@caronte.site', 'pass', '600001012', 30),
(31, 'Funeraria Luto y Paz', 'funeraria3@caronte.site', 'pass', '600001013', 31),
(32, 'Funeraria El Silencio', 'funeraria4@caronte.site', 'pass', '600001014', 32),
(33, 'Funeraria Camino Sereno', 'funeraria5@caronte.site', 'pass', '600001015', 33),
(34, 'Bufete Justicia y Ley', 'lawfirm1@caronte.site', 'pass', '600001016', 34),
(35, 'Abogados de Sevilla', 'lawfirm2@caronte.site', 'pass', '600001017', 35),
(36, 'LegalConsult', 'lawfirm3@caronte.site', 'pass', '600001018', 36),
(37, 'Bufete Martínez & Asociados', 'lawfirm4@caronte.site', 'pass', '600001019', 37),
(38, 'Lex Jurídico', 'lawfirm5@caronte.site', 'pass', '600001020', 38);

INSERT INTO admin (id) VALUES 
(1),
(2);

INSERT INTO company (id, address, city, description, image_url, nif, zip_code, company_type) VALUES
(3, 'Calle Tarfia, 67', 'Sevilla', 'Descripción de Empresa Uno', NULL, 'A12345678', '41001', 'OTRO'),
(4, 'Calle Mirador de Montepinar, 4', 'Madrid', 'Descripción de Empresa Dos', NULL, 'B87654321', '28001', 'OTRO'),
(7, 'Calle del Silencio 12', 'Sevilla', 'Servicios funerarios completos', NULL, 'M12345678', '41002', 'OTRO'),
(8, 'Avda. Las Rosas 45', 'Madrid', 'Flores frescas y ramos personalizados', NULL, 'N23456789', '28041', 'OTRO'),
(9, 'Camino de la Paz 100', 'Granada', 'Atención funeraria 24h', NULL, 'O34567890', '18012', 'OTRO'),
(10, 'Calle Jazmín 22', 'Valencia', 'Ramos y decoración floral', NULL, 'P45678901', '46002', 'OTRO'),
(11, 'Calle Luminosa 55', 'Barcelona', 'Funeraria con servicios integrales', NULL, 'Q56789012', '08003', 'OTRO'),
(12, 'Avda. del Aroma 8', 'Bilbao', 'Floristería especializada en eventos', NULL, 'R67890123', '48002', 'OTRO'),
(13, 'Calle Serena 78', 'Málaga', 'Acompañamiento y apoyo funerario', NULL, 'S78901234', '29001', 'OTRO'),
(14, 'Calle de los Pétalos 33', 'Alicante', 'Flores para cada ocasión', NULL, 'T89012345', '03006', 'OTRO'),
(15, 'Avda. Celestial 90', 'Córdoba', 'Tanatorio y servicios funerarios', NULL, 'U90123456', '14004', 'OTRO'),
(16, 'Calle Ramo 19', 'Zaragoza', 'Floristería con entrega rápida', NULL, 'V01234567', '50005', 'OTRO'),
(17, 'Camino del Adiós 4', 'Toledo', 'Funeraria con atención personalizada', NULL, 'W12345678', '45004', 'OTRO'),
(18, 'Calle Verde 7', 'Sevilla', 'Flores naturales y decoración', NULL, 'X23456789', '41003', 'OTRO'),
(19, 'Calle Gardenia 12', 'Sevilla', 'Floristería elegante', NULL, 'Y11111111', '41010', 'FLORISTERIA'),
(20, 'Avda. de los Tulipanes', 'Madrid', 'Decoraciones florales', NULL, 'Y11111112', '28010', 'FLORISTERIA'),
(21, 'Calle Trébol 4', 'Valencia', 'Floristería artesanal', NULL, 'Y11111113', '46003', 'FLORISTERIA'),
(22, 'Calle Lis 88', 'Zaragoza', 'Flores para eventos', NULL, 'Y11111114', '50002', 'FLORISTERIA'),
(23, 'Avda. del Sur 5', 'Cádiz', 'Floristería tropical', NULL, 'Y11111115', '11001', 'FLORISTERIA'),
(24, 'Calle Notario Real 7', 'Sevilla', 'Gestiones notariales', NULL, 'Y22222221', '41011', 'NOTARIA'),
(25, 'Calle Jorge 3', 'Madrid', 'Notaría de confianza', NULL, 'Y22222222', '28012', 'NOTARIA'),
(26, 'Avda. Constitución 6', 'Granada', 'Trámites legales', NULL, 'Y22222223', '18004', 'NOTARIA'),
(27, 'Calle Columnas 9', 'Toledo', 'Documentación oficial', NULL, 'Y22222224', '45002', 'NOTARIA'),
(28, 'Plaza Mayor 1', 'Segovia', 'Asesoría notarial', NULL, 'Y22222225', '40001', 'NOTARIA'),
(29, 'Camino del Cielo 12', 'Sevilla', 'Servicios funerarios', NULL, 'Y33333331', '41020', 'FUNERARIA'),
(30, 'Calle Descanso 4', 'Bilbao', 'Atención 24h', NULL, 'Y33333332', '48003', 'FUNERARIA'),
(31, 'Avda. Paz 15', 'Córdoba', 'Funeraria integral', NULL, 'Y33333333', '14002', 'FUNERARIA'),
(32, 'Calle Silencio 17', 'Málaga', 'Tanatorio y capilla', NULL, 'Y33333334', '29005', 'FUNERARIA'),
(33, 'Camino Sereno 2', 'Alicante', 'Apoyo familiar', NULL, 'Y33333335', '03002', 'FUNERARIA'),
(34, 'Calle Justicia 7', 'Sevilla', 'Bufete profesional', NULL, 'Y44444441', '41013', 'DESPACHO_DE_ABOGADOS'),
(35, 'Avda. Constitución 45', 'Madrid', 'Despacho jurídico', NULL, 'Y44444442', '28013', 'DESPACHO_DE_ABOGADOS'),
(36, 'Calle Legal 33', 'Barcelona', 'Asesoría legal', NULL, 'Y44444443', '08004', 'DESPACHO_DE_ABOGADOS'),
(37, 'Calle Abogados 8', 'Granada', 'Asistencia jurídica', NULL, 'Y44444444', '18003', 'DESPACHO_DE_ABOGADOS'),
(38, 'Calle Lex 9', 'Valencia', 'Consultoría jurídica', NULL, 'Y44444445', '46005', 'DESPACHO_DE_ABOGADOS');

INSERT INTO customer (id, dni, is_active) VALUES 
(5, '26745987T', 0),
(6, '24072003L', 1);

INSERT INTO emergency_contact (id, email, name, telephone, customer_id) VALUES 
(1, 'contacto1@caronte.site', 'Contacto Uno', '678945638', 5),
(2, 'contacto2@caronte.site', 'Contacto Dos', '643767999', 5),
(3, 'contacto3@caronte.site', 'Contacto Tres', '674945638', 5),
(4, 'contacto4@caronte.site', 'Contacto Cuatro', '645767999', 5);

INSERT INTO image_template (id , image_url) VALUES (1 ,'https://res.cloudinary.com/ds02duuid/image/upload/v1741707864/images/obituaries/templates/fbrpmmujaksc5dbbrbbi.jpg'),
 (2 ,'https://res.cloudinary.com/ds02duuid/image/upload/v1741707864/images/obituaries/templates/jqqcvnp28w3adaymwkcr.jpg'),
 (3, 'https://res.cloudinary.com/ds02duuid/image/upload/v1741707864/images/obituaries/templates/pl6ual71zcpcsfiv8n3q.jpg');

INSERT INTO death_certificate(id, url, is_verified) VALUES (1, 'https://res.cloudinary.com/ds02duuid/image/upload/v1741707864/images/obituaries/death_certificates/1.jpg',0); 
INSERT INTO obituary (id, name, birth_date, death_date, custom_image_url, farewell_message, farewell_phrase, is_mine, customer_id, image_template_id, death_certificate_id, word_color) VALUES 
(1, 'Nombre Ejemplo 1', '1980-01-01', '2023-01-01', 'https://static.nationalgeographicla.com/files/styles/image_3200/public/comedy-wildlife-awards-squirel-stop.jpg', 'Mensaje de despedida 1', 'Frase de despedida 1', 1, 5, 1, 1 , NULL),
(2, 'Nombre Ejemplo 2', '1975-05-15', '2022-12-20', 'https://static.nationalgeographicla.com/files/styles/image_3200/public/comedy-wildlife-awards-squirel-stop.jpg', 'Mensaje de despedida 2', 'Frase de despedida 2', 0, 6, 1, 1, NULL);

INSERT INTO message (id ,body, code, is_last_will, title, customer_id, death_certificate_id ) VALUES 
(1, 'Este es el último mensaje de prueba.', 'MSG001', 1, 'Última Voluntad 1', 5,1),
(2, 'Otro mensaje de prueba.', 'MSG002', 0, 'Mensaje General', 5,1),
(3, 'Otro mensaje de prueba.', 'MSG003', 0, 'Mensaje General', 5,1);

INSERT INTO image (id, image_url, message_id) VALUES 
(1, 'https://static.nationalgeographicla.com/files/styles/image_3200/public/comedy-wildlife-awards-squirel-stop.jpg', 1),
(2, 'https://static.nationalgeographicla.com/files/styles/image_3200/public/comedy-wildlife-awards-squirel-stop.jpg', 1),
(3, 'https://static.nationalgeographicla.com/files/styles/image_3200/public/comedy-wildlife-awards-squirel-stop.jpg', 3);

INSERT INTO video (id, video_url, message_id) VALUES 
(1, 'https://www.youtube.com/watch?v=9bZkp7q19f0', 1),
(2, 'https://www.youtube.com/watch?v=9bZkp7q19f0', 2);

INSERT INTO receiver (id, message_id, obituary_id, telephone, name, email) VALUES 
(1, 1, 1, '678945634', 'Reciver Uno', 'isaacsolpad@gmail.com'),
(3, 1 , 1, '678945638', 'Reciver Uno', 'javrodrei@alum.us.es'),
(2, 1, 2, '643767999', 'Reciver Dos', 'javierrodriguezreina@gmail.com');
