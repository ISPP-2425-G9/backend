INSERT INTO user (user_id, name, email, password, telephone) VALUES
(1, 'admin1', 'admin1@caronte.site', 'hashed_password_1', '626077466'),
(2, 'admin2', 'admin2@caronte.site', 'hashed_password_2', '626077466'),
(3, 'empresa1','empresa1@caronte.site', 'hashed_password_1', '645384175'),
(4, 'empresa2','empresa2@caronte.site', 'hashed_password_2', '957501307'),
(5, 'cliente1', 'cliente1@caronte.site', 'hashed_password_1', '637892263'),
(6, 'cliente2', 'cliente2@caronte.site', 'hashed_password_2', '643287465');

INSERT INTO admin (user_id) VALUES 
(1),
(2);

INSERT INTO company (user_id, address, city, description, image_url, nif, zip_code) VALUES 
(3, 'Calle Tarfia, 67', 'Sevilla', 'Descripción de Empresa Uno', NULL, 'A12345678', '41001'),
(4, 'Calle Mirador de Montepinar, 4', 'Madrid', 'Descripción de Empresa Dos', NULL, 'B87654321', '28001');

INSERT INTO plan (plan_id, billing_address, expire_date, plan_type) VALUES 
(1, 'Calle Contubernio, 67', '2026-12-31', 'FREE'),
(2, 'Calle El Olivo Torcido, 2', '2027-12-31', 'PREMIUM');

INSERT INTO customer (user_id, dni, is_active, plan_id) VALUES 
(5, '26745987T', 0, 1),
(6, '24072003L', 1, 2);

INSERT INTO emergency_contact (emergency_contact_id, email, name, telephone, user_id) VALUES 
(1, 'contacto1@caronte.site', 'Contacto Uno', '678945638', 5),
(2, 'contacto2@caronte.site', 'Contacto Dos', '643767999', 6);

INSERT INTO message (message_id ,body, code, is_last_will, title, user_id) VALUES 
(1, 'Este es el último mensaje de prueba.', 'MSG001', 1, 'Última Voluntad 1', 5),
(2, 'Otro mensaje de prueba.', 'MSG002', 0, 'Mensaje General', 6);

INSERT INTO image (image_id, image_url, message_id) VALUES 
(1, 'https://static.nationalgeographicla.com/files/styles/image_3200/public/comedy-wildlife-awards-squirel-stop.jpg', 1),
(2, 'https://static.nationalgeographicla.com/files/styles/image_3200/public/comedy-wildlife-awards-squirel-stop.jpg', 2);

INSERT INTO video (video_id, video_url, message_id) VALUES 
(1, 'https://www.youtube.com/watch?v=9bZkp7q19f0', 1),
(2, 'https://www.youtube.com/watch?v=9bZkp7q19f0', 2);

INSERT INTO image_template (image_template_id , image_url) VALUES (1 ,'https://static.nationalgeographicla.com/files/styles/image_3200/public/comedy-wildlife-awards-squirel-stop.jpg');

INSERT INTO obituary (obituary_id, name, birth_date, death_date, custom_image_url, farewell_message, farewell_phrase, is_mine, user_id, image_template_id) VALUES 
(1, 'Nombre Ejemplo 1', '1980-01-01', '2023-01-01', 'https://static.nationalgeographicla.com/files/styles/image_3200/public/comedy-wildlife-awards-squirel-stop.jpg', 'Mensaje de despedida 1', 'Frase de despedida 1', 1, 5, 1),
(2, 'Nombre Ejemplo 2', '1975-05-15', '2022-12-20', 'https://static.nationalgeographicla.com/files/styles/image_3200/public/comedy-wildlife-awards-squirel-stop.jpg', 'Mensaje de despedida 2', 'Frase de despedida 2', 0, 6, 1);

INSERT INTO receiver (receiver_id, message_id, obituary_id, telephone, name, email) VALUES 
(1, 1, NULL, '678945638', 'Reciver Uno', 'reciver1@caronte.site'),
(3, 2, NULL, '678945638', 'Reciver Uno', 'reciver1@caronte.site'),
(2, NULL, 1, '643767999', 'Reciver Dos', 'reciver2@caronte.site');


