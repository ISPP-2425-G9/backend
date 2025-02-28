-- Delete all data
DELETE FROM admin WHERE TRUE;
DELETE FROM company WHERE TRUE;
DELETE FROM plan WHERE TRUE;
DELETE FROM customer WHERE TRUE;
DELETE FROM emergency_contact WHERE TRUE;
DELETE FROM message WHERE TRUE;
DELETE FROM image WHERE TRUE;
DELETE FROM video WHERE TRUE;
DELETE FROM obituary WHERE TRUE;
DELETE FROM receiver WHERE TRUE;


-- Insert data
INSERT INTO admin (admin_id, email, password) VALUES 
(1, 'admin1@caronte.site', 'hashed_password_1'),
(2, 'admin2@caronte.site', 'hashed_password_2');

INSERT INTO company (user_id, email, name, password, telephone, address, city, description, image_url, nif, zip_code) VALUES 
(1, 'empresa1@caronte.site', 'Empresa Uno', 'hashed_password_1', '645384175', 'Calle Tarfia, 67', 'Sevilla', 'Descripción de Empresa Uno', NULL, 'A12345678', '41001'),
(2, 'empresa2@caronte.site', 'Empresa Dos', 'hashed_password_2', '957501307', 'Calle Mirador de Montepinar, 4', 'Madrid', 'Descripción de Empresa Dos', NULL, 'B87654321', '28001');

INSERT INTO plan (plan_id, billing_address, expire_date, plan_type) VALUES 
(1, 'Calle Contubernio, 67', '2026-12-31', 'FREE'),
(2, 'Calle El Olivo Torcido, 2', '2027-12-31', 'PREMIUM');

INSERT INTO customer (user_id, email, name, password, telephone, dni, is_active, plan_id) VALUES 
(3, 'cliente1@caronte.site', 'Cliente Uno', 'hashed_password_1', '637892263', '26745987T', 0, NULL),
(4, 'cliente2@caronte.site', 'Cliente Dos', 'hashed_password_2', '643287465', '24072003L', 1, NULL);

INSERT INTO emergency_contact (emergency_contact_id, email, name, telephone, user_id) VALUES 
(1, 'contacto1@caronte.site', 'Contacto Uno', '678945638', 3),
(2, 'contacto2@caronte.site', 'Contacto Dos', '643767999', 4);

INSERT INTO message (message_id ,body, code, is_last_will, title, user_id) VALUES 
(1, 'Este es el último mensaje de prueba.', 'MSG001', 1, 'Última Voluntad 1', 3),
(2, 'Otro mensaje de prueba.', 'MSG002', 0, 'Mensaje General', 4);

INSERT INTO image (image_id, image_url, message_id) VALUES 
(1, 'https://static.nationalgeographicla.com/files/styles/image_3200/public/comedy-wildlife-awards-squirel-stop.jpg', 1),
(2, 'https://static.nationalgeographicla.com/files/styles/image_3200/public/comedy-wildlife-awards-squirel-stop.jpg', 2);

INSERT INTO video (video_id, video_url, message_id) VALUES 
(1, 'https://www.youtube.com/watch?v=9bZkp7q19f0', 1),
(2, 'https://www.youtube.com/watch?v=9bZkp7q19f0', 2);

INSERT INTO obituary (obituary_id, is_mine, structure, user_id) VALUES 
(1, 1, 'Estructura del obituario 1', 3),
(2, 0, 'Estructura del obituario 2', 4);

INSERT INTO receiver (receiver_id, message_id, obituary_id, telephone, name, email) VALUES 
(1, 1, NULL, '678945638', 'Reciver Uno', 'reciver1@caronte.site'),
(2, NULL, 1, '643767999', 'Reciver Dos', 'reciver2@caronte.site');


