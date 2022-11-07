-- data.regions.init
CREATE TABLE IF NOT EXISTS Regions
(
    ID        INTEGER PRIMARY KEY AUTOINCREMENT,
    OwnerName VARCHAR(32) NOT NULL COLLATE NOCASE,

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

-- data.members.init
CREATE TABLE IF NOT EXISTS Members
(
    RegionID INT         NOT NULL,
    Name     VARCHAR(32) NOT NULL COLLATE NOCASE
);

-- data.regions.insert
INSERT INTO Regions(OwnerName, MainX, MainY, MainZ, MinX, MinY, MinZ, MaxX, MaxY, MaxZ)
VALUES (:ownerName, :mainX, :mainY, :mainZ, :minX, :minY, :minZ, :maxX, :maxY, :maxZ);

-- data.members.insert
INSERT INTO Members(RegionID, Name)
VALUES (:regionId, :name);

-- noinspection SqlWithoutWhere
-- data.regions.delete.all
DELETE
FROM Regions;

-- noinspection SqlWithoutWhere
-- data.members.delete.all
DELETE
FROM Members;

-- data.regions.select.all
SELECT *
FROM Regions;

-- data.members.select
SELECT Name
FROM Members
WHERE RegionID = :regionId;

-- data.regions.select.lastId
SELECT last_insert_rowid()
FROM Regions;