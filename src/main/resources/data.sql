INSERT INTO plan (id, subscription_id, plan_type) VALUES
(1, NULL, 'FREE'),
(2, NULL, 'PREMIUM'),
(3, NULL, 'FREE'),
(4, NULL, 'PREMIUM'),
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
(18, NULL, 'PREMIUM');


INSERT INTO user (id, name, email, password, telephone, plan_id) VALUES
(1, 'admin1', 'admin1@caronte.site', '$2a$10$EJ7goCEpyl.XTDXDf/jeVOz1Mfwn24Q4jbIXnwY9yLh9gJSghZecm', '626077466', 5),
(2, 'admin2', 'admin2@caronte.site', '$2a$10$EJ7goCEpyl.XTDXDf/jeVOz1Mfwn24Q4jbIXnwY9yLh9gJSghZecm', '626077466', 6),
(3, 'empresa1','empresa1@caronte.site', '$2a$10$7vxcruASLb/pJhbG1uJGgeb6RqSIBeHJub0UGXSzwJAYBd0LHPoGu', '645384175', 3),
(4, 'empresa2','empresa2@caronte.site', '$2a$10$7vxcruASLb/pJhbG1uJGgeb6RqSIBeHJub0UGXSzwJAYBd0LHPoGu', '957501307', 4),
(5, 'cliente1', 'cliente1@caronte.site', '$2a$10$.qePDyM..BhRQr829JMtGOuRLM7i0K6/im0tgGUWxHvoLQRTs9956', '637892263', 1),
(6, 'cliente2', 'cliente2@caronte.site', '$2a$10$.qePDyM..BhRQr829JMtGOuRLM7i0K6/im0tgGUWxHvoLQRTs9956', '643287465', 2),
(7, 'Funeraria El Descanso', 'descanso@caronte.site', '$2a$10$pw1pass123456789u8ZpXnZn', '600000001', 7),
(8, 'Floristería Rosas Blancas', 'rosasblancas@caronte.site', '$2a$10$pw2pass123456789u8ZpXnZn', '600000002', 8),
(9, 'Funeraria Paz Eterna', 'pazeterna@caronte.site', '$2a$10$pw3pass123456789u8ZpXnZn', '600000003', 9),
(10, 'Floristería El Jazmín', 'jazmin@caronte.site', '$2a$10$pw4pass123456789u8ZpXnZn', '600000004', 10),
(11, 'Funeraria Camino de Luz', 'caminoluz@caronte.site', '$2a$10$pw5pass123456789u8ZpXnZn', '600000005', 11),
(12, 'Floristería Aroma Floral', 'aromafloral@caronte.site', '$2a$10$pw6pass123456789u8ZpXnZn', '600000006', 12),
(13, 'Funeraria Vida Serena', 'vidaserena@caronte.site', '$2a$10$pw7pass123456789u8ZpXnZn', '600000007', 13),
(14, 'Floristería Petalos de Vida', 'petalos@caronte.site', '$2a$10$pw8pass123456789u8ZpXnZn', '600000008', 14),
(15, 'Funeraria Luz Celestial', 'luzcelestial@caronte.site', '$2a$10$pw9pass123456789u8ZpXnZn', '600000009', 15),
(16, 'Floristería El Ramo Perfecto', 'ramoperfecto@caronte.site', '$2a$10$pw10pass123456789u8ZpXnZn', '600000010', 16),
(17, 'Funeraria Último Adiós', 'ultimoadios@caronte.site', '$2a$10$pw11pass123456789u8ZpXnZn', '600000011', 17),
(18, 'Floristería Naturaleza Viva', 'naturalezaviva@caronte.site', '$2a$10$pw12pass123456789u8ZpXnZn', '600000012', 18);

INSERT INTO admin (id) VALUES 
(1),
(2);


INSERT INTO company (id, address, city, description, image_url, nif, zip_code, company_type) VALUES
(3, 'Calle Tarfia, 67', 'Sevilla', 'Descripción de Empresa Uno', NULL, 'A12345678', '41001', 'OTHER'),
(4, 'Calle Mirador de Montepinar, 4', 'Madrid', 'Descripción de Empresa Dos', NULL, 'B87654321', '28001', 'OTHER'),
(7, 'Calle del Silencio 12', 'Sevilla', 'Servicios funerarios completos', NULL, 'M12345678', '41002', 'OTHER'),
(8, 'Avda. Las Rosas 45', 'Madrid', 'Flores frescas y ramos personalizados', NULL, 'N23456789', '28041', 'OTHER'),
(9, 'Camino de la Paz 100', 'Granada', 'Atención funeraria 24h', NULL, 'O34567890', '18012', 'OTHER'),
(10, 'Calle Jazmín 22', 'Valencia', 'Ramos y decoración floral', NULL, 'P45678901', '46002', 'OTHER'),
(11, 'Calle Luminosa 55', 'Barcelona', 'Funeraria con servicios integrales', NULL, 'Q56789012', '08003', 'OTHER'),
(12, 'Avda. del Aroma 8', 'Bilbao', 'Floristería especializada en eventos', NULL, 'R67890123', '48002', 'OTHER'),
(13, 'Calle Serena 78', 'Málaga', 'Acompañamiento y apoyo funerario', NULL, 'S78901234', '29001', 'OTHER'),
(14, 'Calle de los Pétalos 33', 'Alicante', 'Flores para cada ocasión', NULL, 'T89012345', '03006', 'OTHER'),
(15, 'Avda. Celestial 90', 'Córdoba', 'Tanatorio y servicios funerarios', NULL, 'U90123456', '14004', 'OTHER'),
(16, 'Calle Ramo 19', 'Zaragoza', 'Floristería con entrega rápida', NULL, 'V01234567', '50005', 'OTHER'),
(17, 'Camino del Adiós 4', 'Toledo', 'Funeraria con atención personalizada', NULL, 'W12345678', '45004', 'OTHER'),
(18, 'Calle Verde 7', 'Sevilla', 'Flores naturales y decoración', NULL, 'X23456789', '41003', 'OTHER');

INSERT INTO customer (id, dni, is_active) VALUES 
(5, '26745987T', 0),
(6, '24072003L', 1);

INSERT INTO emergency_contact (id, email, name, telephone, customer_id) VALUES 
(1, 'contacto1@caronte.site', 'Contacto Uno', '678945638', 5),
(2, 'contacto2@caronte.site', 'Contacto Dos', '643767999', 6);

INSERT INTO message (id ,body, code, is_last_will, title, customer_id) VALUES 
(1, 'Este es el último mensaje de prueba.', 'MSG001', 1, 'Última Voluntad 1', 5),
(2, 'Otro mensaje de prueba.', 'MSG002', 0, 'Mensaje General', 6);

INSERT INTO image (id, image_url, message_id) VALUES 
(1, 'https://static.nationalgeographicla.com/files/styles/image_3200/public/comedy-wildlife-awards-squirel-stop.jpg', 1),
(2, 'https://static.nationalgeographicla.com/files/styles/image_3200/public/comedy-wildlife-awards-squirel-stop.jpg', 2);

INSERT INTO video (id, video_url, message_id) VALUES 
(1, 'https://www.youtube.com/watch?v=9bZkp7q19f0', 1),
(2, 'https://www.youtube.com/watch?v=9bZkp7q19f0', 2);

INSERT INTO image_template (id , image_url) VALUES (1 ,'https://res.cloudinary.com/ds02duuid/image/upload/v1741707864/images/obituaries/templates/fbrpmmujaksc5dbbrbbi.jpg'),
 (2 ,'https://res.cloudinary.com/ds02duuid/image/upload/v1741707864/images/obituaries/templates/jqqcvnp28w3adaymwkcr.jpg'),
 (3, 'https://res.cloudinary.com/ds02duuid/image/upload/v1741707864/images/obituaries/templates/pl6ual71zcpcsfiv8n3q.jpg');

INSERT INTO death_certificate(id, url, is_verified) VALUES (1, 'https://res.cloudinary.com/ds02duuid/image/upload/v1741707864/images/obituaries/death_certificates/1.jpg',0); 
INSERT INTO obituary (id, name, birth_date, death_date, custom_image_url, farewell_message, farewell_phrase, is_mine, customer_id, image_template_id, death_certificate_id) VALUES 
(1, 'Nombre Ejemplo 1', '1980-01-01', '2023-01-01', 'https://static.nationalgeographicla.com/files/styles/image_3200/public/comedy-wildlife-awards-squirel-stop.jpg', 'Mensaje de despedida 1', 'Frase de despedida 1', 1, 5, 1, NULL),
(2, 'Nombre Ejemplo 2', '1975-05-15', '2022-12-20', 'https://static.nationalgeographicla.com/files/styles/image_3200/public/comedy-wildlife-awards-squirel-stop.jpg', 'Mensaje de despedida 2', 'Frase de despedida 2', 0, 6, 1, NULL);

INSERT INTO receiver (id, message_id, obituary_id, telephone, name, email) VALUES 
(1, 1, NULL, '678945638', 'Reciver Uno', 'reciver1@caronte.site'),
(3, 2, NULL, '678945638', 'Reciver Uno', 'reciver1@caronte.site'),
(2, NULL, 1, '643767999', 'Reciver Dos', 'reciver2@caronte.site');


