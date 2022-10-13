package me.iwareq.anarchy.module.economy;

import cn.nukkit.Player;
import cn.nukkit.command.SimpleCommandMap;
import me.iwareq.anarchy.module.economy.command.MoneyCommand;
import me.iwareq.anarchy.player.PlayerManager;

import java.math.BigDecimal;

public class EconomyManager {

	public static final int MONEY_SCALE = 1;

	private final PlayerManager manager;

	public EconomyManager(PlayerManager manager, SimpleCommandMap commandMap) {
		this.manager = manager;

		commandMap.register("economy", new MoneyCommand());
	}

	public BigDecimal getMoney(Player player) {
		return this.manager.getData(player).getMoney();
	}
}
