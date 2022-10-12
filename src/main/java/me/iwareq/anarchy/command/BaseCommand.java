package me.iwareq.anarchy.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import me.iwareq.anarchy.AnarchyCore;
import me.iwareq.anarchy.player.PlayerData;
import me.iwareq.anarchy.player.PlayerManager;

public abstract class BaseCommand extends Command {

	public BaseCommand(String name) {
		super(name);
	}

	public BaseCommand(String name, String description) {
		super(name, description);
	}

	public BaseCommand(String name, String description, String usageMessage) {
		super(name, description, usageMessage);
	}

	public BaseCommand(String name, String description, String usageMessage, String[] aliases) {
		super(name, description, usageMessage, aliases);
	}

	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;

			if (player.hasPermission(this.getPermission())) {
				PlayerManager playerManager = AnarchyCore.getInstance().getPlayerManager();
				this.execute(playerManager.getData(player), player, args);
			}
		}

		return true;
	}

	public abstract void execute(PlayerData playerData, Player player, String[] args);
}
