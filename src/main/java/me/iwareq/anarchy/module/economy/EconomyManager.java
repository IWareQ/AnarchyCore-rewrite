package me.iwareq.anarchy.module.economy;

import cn.nukkit.command.SimpleCommandMap;
import me.iwareq.anarchy.module.economy.command.AddMoneyCommand;
import me.iwareq.anarchy.module.economy.command.MoneyCommand;
import me.iwareq.anarchy.module.economy.command.PayCommand;
import me.iwareq.anarchy.player.PlayerManager;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class EconomyManager {

	public static final String MONEY_FORMAT = "$";

	public static final int MONEY_SCALE = 1;

	public static void init(PlayerManager manager, SimpleCommandMap commandMap) {
		commandMap.register("economy", new MoneyCommand());
		commandMap.register("economy", new AddMoneyCommand(manager));
		commandMap.register("economy", new PayCommand(manager));
	}

	public static BigDecimal parse(String value) {
		return new BigDecimal(value).setScale(EconomyManager.MONEY_SCALE, RoundingMode.DOWN);
	}
}
