CREATE TABLE IF NOT EXISTS Players
(
    ID       INTEGER PRIMARY KEY AUTOINCREMENT,
    Username VARCHAR(32) NOT NULL COLLATE NOCASE,
    Money    VARCHAR(32) NOT NULL DEFAULT '0.0'
);

DROP TABLE Players;

INSERT INTO Players (Username)
VALUES (:username);

INSERT INTO Players (Username)
VALUES ('IWareQQ');

UPDATE Players
SET Money = :money
WHERE Username = :username;

UPDATE Players
SET Money = '1.0'
WHERE Username = 'iwareqq';

SELECT *
FROM Players;