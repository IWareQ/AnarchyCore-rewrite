package me.iwareq.anarchy.module.economy.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import me.iwareq.anarchy.module.economy.EconomyManager;
import me.iwareq.anarchy.player.PlayerManager;

import java.math.BigDecimal;

public class AddMoneyCommand extends Command {

	private final PlayerManager manager;

	public AddMoneyCommand(PlayerManager manager) {
		super("addmoney", "Выдать монет");
		this.setPermission("command.addmoney");

		this.commandParameters.clear();
		this.commandParameters.put("addmoney", new CommandParameter[]{
				CommandParameter.newType("money", CommandParamType.FLOAT),
				CommandParameter.newType("player", CommandParamType.TARGET)
		});

		this.manager = manager;
	}

	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		if (sender.hasPermission(this.getPermission())) {
			if (args.length < 2) {
				sender.sendMessage("Использование - /addmoney (кол-во монет) (игрок)");
				return true;
			}

			BigDecimal money = EconomyManager.parse(args[0]);
			if (money.signum() < 1) {
				sender.sendMessage("Кол-во монет должно быть больше 0");
				return true;
			}

			this.manager.getOfflineData(args[1], (targetData, targetName) -> {
				if (targetData == null) {
					sender.sendMessage("Игрок " + targetName + " не зарегистрирован!");
					return;
				}

				targetData.addMoney(money);

				sender.sendMessage("Баланс " + targetName + " пополнен на " + money + EconomyManager.MONEY_TYPE);

				Player targetPlayer = targetData.getPlayer();
				if (targetPlayer != null) {
					targetPlayer.sendMessage("Ваш баланс пополнен на " + money + EconomyManager.MONEY_TYPE);
					targetPlayer.sendMessage("Ваш баланс: " + targetData.getMoney() + EconomyManager.MONEY_TYPE);
				}
			});
		}

		return true;
	}
}
