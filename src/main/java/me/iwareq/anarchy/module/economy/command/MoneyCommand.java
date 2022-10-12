package me.iwareq.anarchy.module.economy.command;

import cn.nukkit.Player;
import me.iwareq.anarchy.AnarchyCore;
import me.iwareq.anarchy.command.BaseCommand;
import me.iwareq.anarchy.player.PlayerData;

public class MoneyCommand extends BaseCommand {

	public MoneyCommand() {
		super("money", "Узнай свой баланс");
	}

	@Override
	public void execute(PlayerData playerData, Player player, String[] args) {
		player.sendMessage(AnarchyCore.PREFIX + "Ваш баланс: " + playerData.getMoney() + " $");
		playerData.addMoney("1.32");
	}
}
