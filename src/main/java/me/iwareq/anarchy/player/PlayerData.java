package me.iwareq.anarchy.player;

import cn.nukkit.Player;
import lombok.Getter;
import me.iwareq.anarchy.AnarchyCore;
import me.iwareq.anarchy.module.economy.EconomyManager;
import me.iwareq.anarchy.module.permission.Group;
import me.iwareq.anarchy.module.permission.PermissionManager;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
public class PlayerData {

	private final Player player;

	private BigDecimal money = new BigDecimal("0.0");
	private Group group;

	public PlayerData(Player player) {
		this.player = player;

		PermissionManager permissionManager = AnarchyCore.getInstance().getPermissionManager();
		this.setGroup(permissionManager.getGroup("default"));
	}

	@Deprecated
	public void addMoney(String value) {
		this.money = this.money.add(new BigDecimal(value));
	}

	public void addMoney(BigDecimal value) {
		this.money = this.money.add(value);
	}

	@Deprecated
	public void reduceMoney(String value) {
		this.money = this.money.subtract(new BigDecimal(value));
	}

	public void reduceMoney(BigDecimal value) {
		this.money = this.money.subtract(value);
	}

	public BigDecimal getMoney() {
		return this.money.setScale(EconomyManager.MONEY_SCALE, RoundingMode.DOWN);
	}

	public void setMoney(String value) {
		this.money = new BigDecimal(value);
	}

	public void setGroup(Group group) {
		this.group = group;
		this.group.init(this.player);
	}
}
