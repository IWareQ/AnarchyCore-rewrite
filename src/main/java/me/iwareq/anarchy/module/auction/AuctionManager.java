package me.iwareq.anarchy.module.auction;

import cn.nukkit.Player;
import cn.nukkit.command.SimpleCommandMap;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import me.hteppl.data.database.SQLiteDatabase;
import me.iwareq.anarchy.AnarchyCore;
import me.iwareq.anarchy.module.auction.chest.AuctionChest;
import me.iwareq.anarchy.module.auction.chest.ItemBuyHandler;
import me.iwareq.anarchy.module.auction.command.AuctionCommand;
import me.iwareq.anarchy.module.auction.task.SaveAuctionItems;
import me.iwareq.anarchy.module.economy.EconomyManager;
import me.iwareq.anarchy.util.NbtConverter;
import org.sql2o.Connection;
import org.sql2o.data.Row;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static me.iwareq.anarchy.scheme.SchemeLoader.scheme;

public class AuctionManager extends SQLiteDatabase {

	public static final BigDecimal MAX_PRICE = new BigDecimal("50000");
	public static final BigDecimal MIN_PRICE = new BigDecimal("0.1");

	public static final int MAX_LOTS = 5;

	private static final int AUTO_SAVE_DELAY = 10 * 60 * 20; // 10 min

	private static final int CHEST_SIZE = 36;

	private static final AtomicInteger ID = new AtomicInteger();

	private final Map<Player, AuctionChest> auctions = new ConcurrentHashMap<>();
	private final Map<Integer, Item> items = new ConcurrentHashMap<>();

	public AuctionManager(AnarchyCore main, SimpleCommandMap commandMap) {
		super("auction");

		this.executeScheme(scheme("items.init"));

		this.loadItems();

		int lastId = this.getConnection()
				.createQuery(scheme("items.select.lastId"))
				.executeScalar(Integer.class);

		ID.set(lastId);

		commandMap.register("auction", new AuctionCommand(this));

		main.getServer().getScheduler().scheduleRepeatingTask(new SaveAuctionItems(this), AUTO_SAVE_DELAY, true);
	}

	private void loadItems() {
		List<Row> data = this.connection.createQuery(scheme("items.select.all"))
				.executeAndFetchTable()
				.rows();

		if (!data.isEmpty()) {
			for (int page = 0; page < this.calculateSize(data.size()); page++) {
				int start = page * CHEST_SIZE;
				int stop = Math.min(data.size(), start + CHEST_SIZE);

				for (int index = start; index < stop; index++) {
					Row row = data.get(index);
					String seller = row.getString("SellerName");
					String price = row.getString("price");

					int itemId = row.getInteger("ItemId");
					int damage = row.getInteger("ItemDamage");
					int count = row.getInteger("ItemCount");

					Item item = Item.get(itemId, damage, count);

					CompoundTag namedTag = null;
					String hex = row.getString("NbtHex");
					if (hex != null) {
						namedTag = NbtConverter.toNbt(hex);
					}

					if (namedTag == null) {
						namedTag = new CompoundTag();
					}

					int id = row.getInteger("ID");
					namedTag.putInt("ID", id);
					namedTag.putString("SellerName", seller);
					namedTag.putString("Price", price);
					item = item.setNamedTag(namedTag);

					item = this.setItemRole(item, seller, EconomyManager.parse(price));

					items.put(id, item);
				}
			}
		}
	}

	public synchronized void saveItems() {
		List<Integer> ids = this.connection.createQuery(scheme("items.select.all.ids"))
				.executeScalarList(Integer.class);

		try (Connection transaction = this.getSql2o().beginTransaction()) {
			this.items.forEach((id, item) -> {
				CompoundTag data = item.getNamedTag();

				ids.remove(id);

				String sellerName = data.getString("SellerName");
				String price = data.getString("Price");

				transaction.createQuery(scheme("items.insert"))
						.addParameter("id", id)
						.addParameter("sellerName", sellerName)
						.addParameter("price", price)
						.addParameter("itemId", item.getId())
						.addParameter("itemDamage", item.getDamage())
						.addParameter("itemCount", item.getCount())
						.addParameter("nbtHex", NbtConverter.toHex(data))
						.executeUpdate();
			});

			ids.forEach(id ->
					transaction.createQuery(scheme("items.delete"))
							.addParameter("id", id)
							.executeUpdate());

			transaction.commit();
		}
	}

	private List<Item> getItemsByPage(int page) {
		int start = (page - 1) * CHEST_SIZE;
		int stop = Math.min(this.items.size(), start + CHEST_SIZE);

		Item[] items = this.items.values().toArray(new Item[0]);
		return Arrays.asList(items).subList(start, stop);
	}

	public void openAuction(Player player, int page) {
		AuctionChest auctionChest = new AuctionChest(this, page);

		for (int i = 0; i < CHEST_SIZE; i++) {
			auctionChest.clear(i);
		}

		List<Item> itemsByPage = this.getItemsByPage(page);
		auctionChest.addItem(new ItemBuyHandler(auctionChest, this, page), itemsByPage.toArray(new Item[0]));

		AuctionChest old = this.auctions.get(player);
		if (old == null) {
			player.addWindow(auctionChest);
		} else {
			old.setContents(auctionChest.getContents());

			old.sendContents(player);
		}
	}

	public void closeAuction(Player player) {
		this.auctions.remove(player);
	}

	public void addItem(Player seller, BigDecimal price, Item item) {
		int id = ID.getAndIncrement();

		CompoundTag data = item.getNamedTag();

		if (data == null) {
			data = new CompoundTag();
		}

		CompoundTag auctionData = new CompoundTag();

		auctionData.putInt("ID", id);
		auctionData.putString("SellerName", seller.getName());
		auctionData.putString("Price", price.toString());

		data.putCompound("AuctionData", auctionData);

		item.setNamedTag(data);

		item = this.setItemRole(item, seller.getName(), price);

		this.items.put(id, item);
	}

	private Item setItemRole(Item item, String sellerName, BigDecimal price) {
		return item.setLore(
				"",
				"§rПродавец§7: §6" + sellerName,
				"§rСтоимость§7: §6" + price + EconomyManager.MONEY_FORMAT,
				"",
				"§r§6• §rНажмите§7, §fчтобы купить предмет§7!"
		);
	}

	public int getCountPages() {
		return this.items.isEmpty() ? 1 : this.calculateSize(this.items.size());
	}

	private int calculateSize(int data) {
		return (int) Math.ceil((double) data / CHEST_SIZE);
	}

	public boolean cannotTrade(Player player) {
		int countLots = 0;
		for (Item item : this.items.values()) {
			CompoundTag data = item.getNamedTag();

			String sellerName = data.getString("SellerName");
			if (sellerName.equals(player.getName())) {
				countLots++;
			}
		}

		return countLots >= MAX_LOTS;
	}

	public boolean removeItem(int id) {
		return this.items.remove(id) != null;
	}
}
