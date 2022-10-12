package me.iwareq.anarchy.player;

import cn.nukkit.Player;
import lombok.Getter;
import lombok.Setter;
import me.iwareq.anarchy.module.economy.EconomyManager;

import java.math.BigDecimal;

@Getter
public class PlayerData {

	private final Player player;

	@Setter
	private BigDecimal money = new BigDecimal(EconomyManager.MONEY_FORMAT);

	public PlayerData(Player player) {
		this.player = player;
	}

	public void addMoney(String value) {
		this.money = this.money.add(new BigDecimal(value));
	}
}
