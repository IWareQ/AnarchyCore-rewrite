package me.iwareq.anarchy.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

public class TestCommand extends Command {

	public TestCommand() {
		super("test");
	}

	@Override
	public boolean execute(CommandSender commandSender, String s, String[] strings) {
		return false;
	}
}
