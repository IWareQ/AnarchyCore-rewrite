-- data.regions.init
CREATE TABLE IF NOT EXISTS Regions
(
    ID        INTEGER PRIMARY KEY AUTOINCREMENT,
    OwnerName VARCHAR(32) NOT NULL COLLATE NOCASE,

    Members   TEXT DEFAULT '',

    MainX     INT         NOT NULL,
    MainY     INT         NOT NULL,
    MainZ     INT         NOT NULL,

    MinX      INT         NOT NULL,
    MinY      INT         NOT NULL,
    MinZ      INT         NOT NULL,

    MaxX      INT         NOT NULL,
    MaxY      INT         NOT NULL,
    MaxZ      INT         NOT NULL
);

-- data.regions.insert
INSERT INTO Regions(ID, OwnerName, Members, MainX, MainY, MainZ, MinX, MinY, MinZ, MaxX, MaxY, MaxZ)
VALUES (:id, :ownerName, :members, :mainX, :mainY, :mainZ, :minX, :minY, :minZ, :maxX, :maxY, :maxZ)
ON CONFLICT (ID) DO UPDATE SET Members = :members;

-- data.regions.delete
DELETE
FROM Regions
WHERE ID = :id;

-- data.regions.select.all
SELECT *
FROM Regions;

-- data.regions.select.all.ids
SELECT ID
FROM Regions;

-- data.regions.select.lastId
SELECT last_insert_rowid();