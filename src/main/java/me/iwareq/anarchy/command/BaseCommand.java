package me.iwareq.anarchy.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import me.iwareq.anarchy.AnarchyCore;
import me.iwareq.anarchy.player.PlayerData;
import me.iwareq.anarchy.player.PlayerManager;

public abstract class BaseCommand extends Command {

	public BaseCommand(String name, String description) {
		super(name, description);

		this.commandParameters.clear();
	}

	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		String permission = this.getPermission();
		boolean canExecute = sender.hasPermission(permission);
		if (permission == null) {
			canExecute = true;
		}

		if (canExecute && sender.isPlayer()) {
			Player player = (Player) sender;

			PlayerManager playerManager = AnarchyCore.getInstance().getPlayerManager();
			this.execute(playerManager.getData(player), player, args);
		}

		return true;
	}

	public abstract void execute(PlayerData playerData, Player player, String[] args);
}
