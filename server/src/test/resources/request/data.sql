DELETE FROM bookings;
DELETE FROM items;
DELETE FROM requestors;
DELETE FROM users;
INSERT INTO users(id, name, email)
VALUES(10, 'Melody Altenwerth DDS', 'Hermina_Osinski43@hotmail.com');
INSERT INTO users(id, name, email)
VALUES(11, 'Tara Gerlach', 'Holden_Powlowski@yahoo.com');
INSERT INTO users(id, name, email)
VALUES(12, 'Roman Ferry', 'Carol_Rodriguez5@hotmail.com');
INSERT INTO requestors (id, description, created, requestor_id)
VALUES(100, 'requestors1', '2025-03-16 00:03:58.176', 10);
INSERT INTO requestors (id, description, created, requestor_id)
VALUES(101, 'requestors2', '2025-03-16 00:03:58.176', 11);
INSERT INTO requestors (id, description, created, requestor_id)
VALUES(102, 'requestors3', '2025-03-16 00:03:58.176', 12);
INSERT INTO requestors (id, description, created, requestor_id)
VALUES(103, 'requestors4', '2025-03-16 00:03:58.176', 12);
