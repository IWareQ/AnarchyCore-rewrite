package me.iwareq.anarchy.module.economy.command;

import cn.nukkit.Player;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import me.iwareq.anarchy.command.BaseCommand;
import me.iwareq.anarchy.module.economy.EconomyManager;
import me.iwareq.anarchy.player.PlayerData;
import me.iwareq.anarchy.player.PlayerManager;

import java.math.BigDecimal;

public class PayCommand extends BaseCommand {

	private final PlayerManager manager;

	public PayCommand(PlayerManager manager) {
		super("pay", "Перевести монет");
		this.commandParameters.put("pay", new CommandParameter[]{
				CommandParameter.newType("money", CommandParamType.FLOAT),
				CommandParameter.newType("player", CommandParamType.TARGET)
		});

		this.manager = manager;
	}

	@Override
	public void execute(PlayerData playerData, Player player, String[] args) {
		if (args.length < 2) {
			player.sendMessage("Использование - /pay (кол-во монет) (игрок)");
			return;
		}

		BigDecimal money = EconomyManager.parse(args[0]);
		if (money.signum() < 1) {
			player.sendMessage("Кол-во монет должно быть больше 0");
			return;
		}

		if (money.compareTo(playerData.getMoney()) > 0) {
			player.sendMessage("Вам не хватает монет для перевода!");
			player.sendMessage("Ваш баланс: " + playerData.getMoney() + EconomyManager.MONEY_TYPE);
			return;
		}

		this.manager.getOfflineData(args[1], (targetData, targetName) -> {
			if (targetData == null) {
				player.sendMessage("Игрок " + targetName + " не найден!");
				return;
			}

			if (player.getName().equalsIgnoreCase(targetName)) {
				player.sendMessage("Вы не можете перевести себе же!");
				return;
			}

			playerData.reduceMoney(money);
			targetData.addMoney(money);

			player.sendMessage("Вы успешно перевели " + money + EconomyManager.MONEY_TYPE + " игроку " + targetName);
			Player targetPlayer = targetData.getPlayer();
			if (targetPlayer != null) {
				targetPlayer.sendMessage("Игрок " + player.getName() + " перевел Вам " + money + EconomyManager.MONEY_TYPE);
			}
		});
	}
}
