DELETE FROM bookings;
DELETE FROM items;
DELETE FROM requestors;
DELETE FROM users;
INSERT INTO users(id, name, email)
VALUES(1, 'Melody Altenwerth DDS', 'Hermina_Osinski43@hotmail.com');
INSERT INTO users(id, name, email)
VALUES(2, 'Janice Mann', 'Retta_Dibbert@yahoo.com');
INSERT INTO users(id, name, email)
VALUES(3, 'Tara Gerlach', 'Holden_Powlowski@yahoo.com');
INSERT INTO requestors (id, description, created, requestor_id)
VALUES(123, 'lFKhT8GA921dhRyZ7DgxkONmcjTysyn9mGx3ifbpPjnPl2vdhw', '2025-03-16 00:03:58.176', 1);
INSERT INTO items (id, name, description, available, owner_id, request_id)
VALUES(100, 'Test Item', 'Test Description', true, 1, 123);
INSERT INTO items (id, name, description, available, owner_id, request_id)
VALUES(101, 'Test Item Update', 'Test Description Update', true, 1, 123);
INSERT INTO items (id, name, description, available, owner_id, request_id)
VALUES(102, 'WQWQ', 'GFGFG', true, 1, 123);
INSERT INTO items (id, name, description, available, owner_id, request_id)
VALUES(103, 'WQWQ555', 'GFGFG555', true, 2, 123);
INSERT INTO items (id, name, description, available, owner_id, request_id)
VALUES(104, 'WQWQ666', 'GFGFG5556666', true, 3, 123);
INSERT INTO bookings
(id, start_date, end_date, item_id, booker_id, status)
VALUES(12, '2025-01-20 19:22:35.000', '2025-01-25 19:22:35.000', 100, 1, 'APPROVED');
INSERT INTO bookings
(id, start_date, end_date, item_id, booker_id, status)
VALUES(13, '2025-01-20 19:22:35.000', '2025-01-25 19:22:35.000', 103, 2, 'APPROVED');
INSERT INTO bookings
(id, start_date, end_date, item_id, booker_id, status)
VALUES(14, '2025-01-20 19:22:35.000', '2025-01-25 19:22:35.000', 104, 3, 'APPROVED');
INSERT INTO bookings
(id, start_date, end_date, item_id, booker_id, status)
VALUES(15, '2025-01-20 19:22:35.000', '2025-01-25 19:22:35.000', 104, 3, 'APPROVED');
