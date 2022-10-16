package me.iwareq.anarchy.module.permission.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import me.iwareq.anarchy.AnarchyCore;
import me.iwareq.anarchy.module.permission.Group;
import me.iwareq.anarchy.module.permission.PermissionManager;
import me.iwareq.anarchy.player.PlayerManager;

public class SetGroupCommand extends Command {

	private final PermissionManager manager;

	public SetGroupCommand(PermissionManager manager) {
		super("setgroup", "Выдать привилегию");
		this.setPermission("command.setgroup");

		this.commandParameters.clear();
		this.commandParameters.put("group", new CommandParameter[]{
				CommandParameter.newEnum("groupId", new CommandEnum("groups", manager.getGroupIds())),
				CommandParameter.newType("player", CommandParamType.TARGET)
		});

		this.manager = manager;
	}

	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		if (sender.hasPermission(this.getPermission())) {
			if (args.length < 2) {
				sender.sendMessage("Использование - /setgroup (groupId) (игрок)");
				return true;
			}

			Group group = this.manager.getGroup(args[0]);
			if (group == null) {
				sender.sendMessage("Привилегия " + args[0] + " не найдена!");
				return true;
			}

			PlayerManager playerManager = AnarchyCore.getInstance().getPlayerManager();
			playerManager.getOfflineData(args[1], (targetData, targetName) -> {
				if (targetData == null) {
					sender.sendMessage("Игрок " + targetName + " не зарегистрирован!");
					return;
				}

				targetData.setGroup(group);

				sender.sendMessage("Игрок " + targetName + " получил привилегию " + group.getFormat());

				Player targetPlayer = targetData.getPlayer();
				if (targetPlayer != null) {
					targetPlayer.sendMessage("Вы получили привилегию " + group.getFormat());
					targetPlayer.sendMessage("Подробнее со списком возможностей можно познакомиться с помощью команды /donate");
				}
			});
		}

		return true;
	}
}
