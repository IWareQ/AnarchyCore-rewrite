CREATE TABLE IF NOT EXISTS Players
(
    ID       INTEGER PRIMARY KEY AUTOINCREMENT,
    Username VARCHAR(32) NOT NULL COLLATE NOCASE,
    Money    INT         NOT NULL DEFAULT '0.0'
);

DROP TABLE Players;

INSERT INTO Players (Username)
VALUES (:username);

UPDATE Players
SET Money = :money
WHERE Username = :username;