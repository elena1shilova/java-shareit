DELETE FROM items;
DELETE FROM requestors;
DELETE FROM users;
INSERT INTO users(id, name, email)
VALUES(1, 'Melody Altenwerth DDS', 'Hermina_Osinski43@hotmail.com');
INSERT INTO requestors
(id, description, created, requestor_id)
VALUES(123, 'lFKhT8GA921dhRyZ7DgxkONmcjTysyn9mGx3ifbpPjnPl2vdhw', '2025-03-16 00:03:58.176', 1);
INSERT INTO items (name, description, available, owner_id, request_id)
VALUES('Test Item', 'Test Description', true, 1, 123);
INSERT INTO items (name, description, available, owner_id, request_id)
VALUES('WQWQ', 'GFGFG', true, 1, 123);
