DELETE FROM bookings;
DELETE FROM items;
DELETE FROM requestors;
DELETE FROM users;
INSERT INTO users(id, name, email)
VALUES(100, 'Melody Altenwerth DDS', 'Hermina_Osinski43@hotmail.com');
INSERT INTO users(id, name, email)
VALUES(101, 'Roman Ferry', 'Carol_Rodriguez5@hotmail.com');
INSERT INTO items (id, name, description, available, owner_id)
VALUES(100, 'Test Item', 'Test Description', true, 100);
INSERT INTO bookings
(id, start_date, end_date, item_id, booker_id, status)
VALUES(100, '2025-05-16 19:22:35.000', '2025-05-17 19:22:35.000', 100, 100, 'WAITING');
INSERT INTO bookings
(id, start_date, end_date, item_id, booker_id, status)
VALUES(101, '2025-05-20 19:22:35.000', '2025-05-25 19:22:35.000', 100, 100, 'REJECTED');
INSERT INTO bookings
(id, start_date, end_date, item_id, booker_id, status)
VALUES(102, '2025-05-20 19:22:35.000', '2025-05-25 19:22:35.000', 100, 100, 'WAITING');
