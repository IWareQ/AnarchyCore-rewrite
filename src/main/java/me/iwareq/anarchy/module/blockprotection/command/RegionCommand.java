package me.iwareq.anarchy.module.blockprotection.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import me.iwareq.anarchy.module.blockprotection.BlockProtectionManager;
import me.iwareq.anarchy.module.blockprotection.form.MenuForm;

public class RegionCommand extends Command {

	private final BlockProtectionManager manager;

	public RegionCommand(BlockProtectionManager manager) {
		super("region", "§rСистема регионов", "", new String[]{"rg"});
		this.commandParameters.clear();
		this.commandParameters.put("region", new CommandParameter[]{
				CommandParameter.newEnum("action", new CommandEnum("RegionAction", "add", "del")),
				CommandParameter.newType("player", CommandParamType.TARGET)
		});

		this.manager = manager;
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			Player player = (Player) sender;
			if (args.length == 0) {
				new MenuForm(this.manager).sendAsync(player);
				return true;
			}

			switch (args[0]) {
				case "add": {
					if (args.length != 2) {
						player.sendMessage("§l§6• §rИспользование §7- /§6rg add §7(§6игрок§7)");
						return true;
					}

					// todo
				}
				break;

				case "del": {
					if (args.length != 2) {
						player.sendMessage("§l§6• §r§fИспользование §7- /§6rg del §7(§6игрок§7)");
						return true;
					}

					// todo
				}
				break;

				default: {
					// todo
				}
				break;

			}
		}

		return false;
	}
}
