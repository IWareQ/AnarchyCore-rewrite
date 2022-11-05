package me.iwareq.anarchy.module.auction.chest;

import cn.nukkit.Player;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import me.iwareq.anarchy.module.auction.AuctionManager;
import me.iwareq.fakeinventories.CustomInventory;

public class AuctionChest extends CustomInventory {

	private final AuctionManager manager;

	public AuctionChest(AuctionManager manager, int currentPage) {
		super(InventoryType.DOUBLE_CHEST, "Торговая площадка");

		this.manager = manager;

		this.setDefaultListener(((item, inventory, event) -> {
			manager.buyItem(event.getTransaction().getSource(), item, currentPage, inventory);

			event.setCancelled();
		}));

		Item storageItem = Item.get(Item.MINECART_WITH_CHEST);
		storageItem.setCustomName("§r§6Хранилище");
		storageItem.setLore("\n§r§l§6• §rНажмите§7, §fчтобы перейти§7!");

		this.setItem(48, storageItem, (item, inventory, event) -> {
			event.setCancelled();
		});

		Item aboutItem = Item.get(Item.SIGN);
		aboutItem.setCustomName("§r§6Справка");
		aboutItem.setLore(
				"\n§rЭто торговая площадка§7, §fкоторая создана",
				"§rдля покупки и продажи предметов§7.",
				"",
				"§r§fТорговая площадка также является",
				"§rотличным способом заработать §6Монет§7, §fпродавая",
				"§rфермерские товары§7, §fкоторые могут",
				"§rзаинтересовать других Игроков§7.",
				"",
				"§rЧтобы выставить предмет на продажу§7,",
				"§r§fвозьмите его в руку и введите",
				"§r§6/auc §7(§6цена§7)"
		);

		this.setItem(50, aboutItem, (item, inventory, event) -> {
			event.setCancelled();
		});

		int countPages = this.manager.getCountPages();
		if (countPages != 1) {
			Item backPageItem = Item.get(Item.PAPER);
			backPageItem.setCustomName("§r§6Листнуть назад");
			backPageItem.setLore("\n§r§l§6• §rНажмите§7, §fчтобы перейти§7!");

			this.setItem(45, backPageItem, (item, inventory, event) -> {
				Player player = event.getTransaction().getSource();
				if (currentPage == 1) {
					this.manager.openAuction(player, countPages);
				} else {
					this.manager.openAuction(player, currentPage - 1);
				}

				player.getLevel().addSound(player, Sound.ITEM_BOOK_PAGE_TURN, 1, 1, player);
				event.setCancelled();
			});

			Item nextPageItem = Item.get(Item.PAPER);
			nextPageItem.setCustomName("§r§6Листнуть вперед");
			nextPageItem.setLore("\n§r§l§6• §rНажмите§7, §fчтобы перейти§7!");

			this.setItem(53, nextPageItem, (item, inventory, event) -> {
				Player player = event.getTransaction().getSource();
				if (currentPage == countPages) {
					this.manager.openAuction(player, 1);
				} else {
					this.manager.openAuction(player, currentPage + 1);
				}

				player.getLevel().addSound(player, Sound.ITEM_BOOK_PAGE_TURN, 1, 1, player);
				event.setCancelled();
			});
		}
	}

	@Override
	public void onClose(Player player) {
		super.onClose(player);

		this.manager.closeAuction(player);
	}
}
