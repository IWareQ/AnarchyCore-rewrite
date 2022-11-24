package me.iwareq.anarchy.module.auction.chest;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.inventory.InventoryTransactionEvent;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.AllArgsConstructor;
import me.iwareq.anarchy.AnarchyCore;
import me.iwareq.anarchy.module.auction.AuctionManager;
import me.iwareq.anarchy.module.economy.EconomyManager;
import me.iwareq.anarchy.player.PlayerData;
import me.iwareq.anarchy.player.PlayerManager;
import me.iwareq.fakeinventories.util.ItemHandler;

import java.math.BigDecimal;

@AllArgsConstructor
public class ItemBuyHandler implements ItemHandler {

	private final AuctionChest inventory;
	private final AuctionManager manager;
	private int page;

	@Override
	public void handle(Item item, InventoryTransactionEvent event) {
		event.setCancelled(true);

		CompoundTag data = item.getNamedTag();
		if (data == null) {
			return;
		}

		CompoundTag auctionData = data.getCompound("AuctionData");

		Player player = event.getTransaction().getSource();

		BigDecimal price = EconomyManager.parse(auctionData.getString("Price"));

		PlayerManager playerManager = AnarchyCore.getInstance().getPlayerManager();
		PlayerData playerData = playerManager.getData(player);
		if (playerData.getMoney().compareTo(price) < 0) {
			player.sendMessage("Недостаточно §6монет §fдля совершения покупки§7!");
			this.playSound(Sound.NOTE_BASS, player);
			return;
		}

		int id = auctionData.getInt("ID");
		if (!manager.removeItem(id)) {
			player.sendMessage("Предмет был снят с продажи§7!");
			this.playSound(Sound.NOTE_BASS, player);
			return;
		}

		PlayerInventory buyerInventory = player.getInventory();
		if (buyerInventory.canAddItem(item)) {
			inventory.removeItem(item);

			data.remove("AuctionData");
			data.remove("display");

			buyerInventory.addItem(item.setNamedTag(data));

			String sellerName = auctionData.getString("SellerName");
			if (sellerName.equals(player.getName())) {
				player.sendMessage("Предмет был §6снят с продажи §fи отправлен Вам в Инвентарь");
				this.playSound(Sound.RANDOM_LEVELUP, player);
			} else {
				playerManager.getOfflineData(sellerName, (targetData, targetName) -> {
					playerData.reduceMoney(price);
					targetData.addMoney(price);

					Player targetPlayer = targetData.getPlayer();
					if (targetPlayer != null) {
						targetPlayer.sendMessage("Игрок §6" + player.getName() + " §fкупил Ваш товар за §6" + price + EconomyManager.MONEY_FORMAT);
					}
				});

				player.sendMessage("Предмет успешно куплен за §6" + price + EconomyManager.MONEY_FORMAT);
				this.playSound(Sound.RANDOM_LEVELUP, player);
			}

			Server.getInstance().getScheduler().scheduleTask(() -> {
				while (this.page > this.manager.getCountPages()) {
					this.page--;
				}

				this.manager.openAuction(player, page);
			}, true);
		} else {
			player.sendMessage("Недостаточно §6места §fв §6инвентаре§7!");
			this.playSound(Sound.NOTE_BASS, player);
		}
	}

	private void playSound(Sound sound, Player player) {
		player.getLevel().addSound(player, sound, 1, 1, player);
	}
}
