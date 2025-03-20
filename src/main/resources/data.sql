INSERT INTO plan (id, subscription_id, plan_type) VALUES
(1, NULL, 'FREE'),
(2, NULL, 'PREMIUM'),
(3, NULL, 'FREE'),
(4, NULL, 'PREMIUM'),
(5, NULL, 'FREE'),
(6, NULL, 'FREE');


INSERT INTO user (id, name, email, password, telephone, plan_id) VALUES
(1, 'admin1', 'admin1@caronte.site', '$2a$10$EJ7goCEpyl.XTDXDf/jeVOz1Mfwn24Q4jbIXnwY9yLh9gJSghZecm', '626077466', 5),
(2, 'admin2', 'admin2@caronte.site', '$2a$10$EJ7goCEpyl.XTDXDf/jeVOz1Mfwn24Q4jbIXnwY9yLh9gJSghZecm', '626077466', 6),
(3, 'empresa1','empresa1@caronte.site', '$2a$10$7vxcruASLb/pJhbG1uJGgeb6RqSIBeHJub0UGXSzwJAYBd0LHPoGu', '645384175', 3),
(4, 'empresa2','empresa2@caronte.site', '$2a$10$7vxcruASLb/pJhbG1uJGgeb6RqSIBeHJub0UGXSzwJAYBd0LHPoGu', '957501307', 4),
(5, 'cliente1', 'cliente1@caronte.site', '$2a$10$.qePDyM..BhRQr829JMtGOuRLM7i0K6/im0tgGUWxHvoLQRTs9956', '637892263', 1),
(6, 'cliente2', 'cliente2@caronte.site', '$2a$10$.qePDyM..BhRQr829JMtGOuRLM7i0K6/im0tgGUWxHvoLQRTs9956', '643287465', 2);

INSERT INTO admin (id) VALUES 
(1),
(2);


INSERT INTO company (id, address, city, description, image_url, nif, zip_code) VALUES 
(3, 'Calle Tarfia, 67', 'Sevilla', 'Descripción de Empresa Uno', NULL, 'A12345678', '41001'),
(4, 'Calle Mirador de Montepinar, 4', 'Madrid', 'Descripción de Empresa Dos', NULL, 'B87654321', '28001');

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
 
INSERT INTO obituary (id, name, birth_date, death_date, custom_image_url, farewell_message, farewell_phrase, is_mine, customer_id, image_template_id) VALUES 
(1, 'Nombre Ejemplo 1', '1980-01-01', '2023-01-01', 'https://static.nationalgeographicla.com/files/styles/image_3200/public/comedy-wildlife-awards-squirel-stop.jpg', 'Mensaje de despedida 1', 'Frase de despedida 1', 1, 5, 1),
(2, 'Nombre Ejemplo 2', '1975-05-15', '2022-12-20', 'https://static.nationalgeographicla.com/files/styles/image_3200/public/comedy-wildlife-awards-squirel-stop.jpg', 'Mensaje de despedida 2', 'Frase de despedida 2', 0, 6, 1);

INSERT INTO receiver (id, message_id, obituary_id, telephone, name, email) VALUES 
(1, 1, NULL, '678945638', 'Reciver Uno', 'reciver1@caronte.site'),
(3, 2, NULL, '678945638', 'Reciver Uno', 'reciver1@caronte.site'),
(2, NULL, 1, '643767999', 'Reciver Dos', 'reciver2@caronte.site');


