package me.iwareq.anarchy.module.auction.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.item.Item;
import me.iwareq.anarchy.module.auction.AuctionManager;
import me.iwareq.anarchy.module.economy.EconomyManager;

import java.math.BigDecimal;

public class AuctionCommand extends Command {

	private final AuctionManager manager;

	public AuctionCommand(AuctionManager manager) {
		super("auction", "аукцион (продажа — /ah sell <цена>)", "", new String[]{"auc", "ah"});
		this.commandParameters.clear();
		this.commandParameters.put("auction", new CommandParameter[]{
				CommandParameter.newEnum("action", new CommandEnum("AuctionAction", "sell")),
				CommandParameter.newType("money", true, CommandParamType.FLOAT)
		});

		this.manager = manager;
	}

	@Override()
	public boolean execute(CommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			Player player = (Player) sender;
			if (args.length != 2) {
				manager.openAuction(player, 1);
				return true;
			}

			if (this.manager.cannotTrade(player)) {
				player.sendMessage("Вы уже разместили или храните максимальное колличество лотов §7(§6" + AuctionManager.MAX_LOTS + "§7)");
				return false;
			}

			BigDecimal price = EconomyManager.parse(args[1]);

			if (price.compareTo(AuctionManager.MIN_PRICE) < 0) {
				player.sendMessage("Минимальная цена за предмет §7- §6" + AuctionManager.MIN_PRICE + EconomyManager.MONEY_FORMAT);
				return false;
			} else if (price.compareTo(AuctionManager.MAX_PRICE) > 0) {
				player.sendMessage("Максимальная цена за предмет §7- §6" + AuctionManager.MAX_PRICE + EconomyManager.MONEY_FORMAT);
				return false;
			}

			Item item = player.getInventory().getItemInHand().clone();
			if (item.getId() == Item.AIR) {
				player.sendMessage("Чтобы выставить предмет на продажу, возьмите его в руку!");
				return true;
			}

			player.sendMessage("Предмет на продажу успешно выставлен за " + price + EconomyManager.MONEY_FORMAT);
			player.getServer().broadcastMessage("Игрок §6" + player.getName() + " выставил предмет на продажу!");

			player.getInventory().setItemInHand(Item.get(Item.AIR));

			this.manager.addItem(player, price, item);
		}

		return false;
	}
}
