-- data.players.init
CREATE TABLE IF NOT EXISTS Players
(
    ID       INTEGER PRIMARY KEY AUTOINCREMENT,
    Username VARCHAR(32) NOT NULL COLLATE NOCASE,
    Money    VARCHAR(32) NOT NULL DEFAULT '0.0',
    GroupId  VARCHAR(32) NOT NULL DEFAULT 'default'
);

-- data.players.select.all
SELECT *
FROM Players
WHERE Username = :username;

-- data.players.insert
INSERT INTO Players (Username)
VALUES (:username);

-- data.players.save.all
UPDATE Players
SET Money   = :money,
    GroupId = :group
WHERE Username = :username;