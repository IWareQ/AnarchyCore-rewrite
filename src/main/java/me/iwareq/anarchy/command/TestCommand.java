package me.iwareq.anarchy.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.inventory.InventoryType;
import me.iwareq.fakeinventories.CustomInventory;

public class TestCommand extends Command {

	public TestCommand() {
		super("test");
	}

	@Override
	public boolean execute(CommandSender commandSender, String s, String[] strings) {
		CustomInventory inventory = new CustomInventory(InventoryType.DOUBLE_CHEST);
		Player player = (Player) commandSender;
		player.addWindow(inventory);
		return false;
	}
}
