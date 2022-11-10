-- data.items.init
CREATE TABLE IF NOT EXISTS Items
(
    ID         INTEGER PRIMARY KEY AUTOINCREMENT,
    SellerName VARCHAR(32) NOT NULL COLLATE NOCASE,
    Price      VARCHAR(32) NOT NULL,
    ItemID     INT         NOT NULL,
    ItemDamage INT         NOT NULL,
    ItemCount  INT         NOT NULL,
    NbtHex     VARCHAR(32) NOT NULL
);

-- data.items.insert
INSERT INTO Items(ID, SellerName, Price, ItemID, ItemDamage, ItemCount, NbtHex)
VALUES (:id, :sellerName, :price, :itemId, :itemDamage, :itemCount, :nbtHex)
ON CONFLICT (ID) DO NOTHING;

-- noinspection SqlWithoutWhere
-- data.items.delete
DELETE
FROM Items
WHERE ID = :id;

-- data.items.select.all
SELECT *
FROM Items;

-- data.items.select.all.ids
SELECT ID
FROM Items;

-- data.items.select.lastId
SELECT last_insert_rowid();