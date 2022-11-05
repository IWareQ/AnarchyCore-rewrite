package me.iwareq.anarchy.module.auction;

import cn.nukkit.Player;
import cn.nukkit.command.SimpleCommandMap;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.nbt.tag.CompoundTag;
import me.hteppl.data.database.SQLiteDatabase;
import me.iwareq.anarchy.AnarchyCore;
import me.iwareq.anarchy.module.auction.chest.AuctionChest;
import me.iwareq.anarchy.module.auction.command.AuctionCommand;
import me.iwareq.anarchy.module.economy.EconomyManager;
import me.iwareq.anarchy.player.PlayerData;
import me.iwareq.anarchy.player.PlayerManager;
import me.iwareq.anarchy.util.NbtConverter;
import me.iwareq.fakeinventories.CustomInventory;
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
	private static final int CHEST_SIZE = 36;

	private static final AtomicInteger ID = new AtomicInteger();

	private final Map<Player, AuctionChest> auctions = new ConcurrentHashMap<>();
	private final Map<Integer, Item> items = new ConcurrentHashMap<>();

	public AuctionManager(SimpleCommandMap commandMap) {
		super("auction");

		this.executeScheme(scheme("items.init"));

		this.loadItems();

		int lastId = this.getConnection()
				.createQuery(scheme("items.select.lastId"))
				.executeScalar(Integer.class);

		ID.set(lastId);

		commandMap.register("auction", new AuctionCommand(this));
	}

	private void loadItems() {
		List<Row> data = this.getConnection()
				.createQuery(scheme("items.select.all"))
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

	public void saveItems() {
		this.getConnection().createQuery(scheme("items.delete.all")).executeUpdate();

		this.items.forEach((id, item) -> {
			CompoundTag data = item.getNamedTag();

			String sellerName = data.getString("SellerName");
			String price = data.getString("Price");

			this.getConnection().createQuery(scheme("items.insert"))
					.addParameter("sellerName", sellerName)
					.addParameter("price", price)
					.addParameter("itemId", item.getId())
					.addParameter("itemDamage", item.getDamage())
					.addParameter("itemCount", item.getCount())
					.addParameter("nbtHex", NbtConverter.toHex(data))
					.executeUpdate();
		});
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
		auctionChest.addItem(itemsByPage.toArray(new Item[0]));

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

		data.putInt("ID", id);
		data.putString("SellerName", seller.getName());
		data.putString("Price", price.toString());

		item.setNamedTag(data);

		item = this.setItemRole(item, seller.getName(), price);

		this.items.put(id, item);
	}

	public void buyItem(Player buyer, Item item, int page, CustomInventory inventory) {
		CompoundTag data = item.getNamedTag();
		BigDecimal price = EconomyManager.parse(data.getString("Price"));

		PlayerManager playerManager = AnarchyCore.getInstance().getPlayerManager();
		PlayerData playerData = playerManager.getData(buyer);
		if (playerData.getMoney().compareTo(price) < 0) {
			buyer.sendMessage("Недостаточно §6монет §fдля совершения покупки§7!");
			buyer.getLevel().addSound(buyer, Sound.NOTE_BASS, 1, 1, buyer);
			return;
		}

		PlayerInventory buyerInventory = buyer.getInventory();
		if (buyerInventory.canAddItem(item)) {
			inventory.removeItem(item);

			int id = data.getInt("ID");

			String sellerName = data.getString("SellerName");
			if (sellerName.equals(buyer.getName())) {
				data.remove("ID");
				data.remove("display");

				buyerInventory.addItem(item.setNamedTag(data));

				buyer.sendMessage("Предмет был §6снят с продажи §fи отправлен Вам в Инвентарь");
				buyer.getLevel().addSound(buyer, Sound.RANDOM_LEVELUP, 1, 1, buyer);
			} else {
				data.remove("ID");
				data.remove("display");

				buyerInventory.addItem(item.setNamedTag(data));

				playerManager.getOfflineData(sellerName, (targetData, targetName) -> {
					playerData.reduceMoney(price);
					targetData.addMoney(price);

					Player targetPlayer = targetData.getPlayer();
					if (targetPlayer != null) {
						targetPlayer.sendMessage("Игрок §6" + buyer.getName() + " §fкупил Ваш товар за §6" + price + EconomyManager.MONEY_FORMAT);
					}
				});

				buyer.sendMessage("Предмет успешно куплен за §6" + price + EconomyManager.MONEY_FORMAT);
				buyer.getLevel().addSound(buyer, Sound.RANDOM_LEVELUP, 1, 1, buyer);
			}

			this.items.remove(id);

			while (page > this.getCountPages()) {
				page--;
			}

			this.openAuction(buyer, page);
		} else {
			buyer.sendMessage("Недостаточно §6места §fв §6инвентаре§7!");
			buyer.getLevel().addSound(buyer, Sound.NOTE_BASS, 1, 1, buyer);
		}
	}

	private Item setItemRole(Item item, String sellerName, BigDecimal price) {
		return item.setLore(
				"\n§rПродавец§7: §6" + sellerName,
				"\n§rСтоимость§7: §6" + price + EconomyManager.MONEY_FORMAT,
				"\n",
				"\n§l§6• §rНажмите§7, §fчтобы купить предмет§7!"
		);
	}

	public int getCountPages() {
		return this.items.isEmpty() ? 1 : this.calculateSize(this.items.size());
	}

	private int calculateSize(int data) {
		return (int) Math.ceil((double) data / CHEST_SIZE);
	}

	public boolean notTrade(Player player) {
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
}
