package me.iwareq.anarchy;

import cn.nukkit.command.Command;
import cn.nukkit.command.SimpleCommandMap;
import cn.nukkit.plugin.PluginBase;
import lombok.Getter;
import me.iwareq.anarchy.module.economy.EconomyManager;
import me.iwareq.anarchy.player.PlayerManager;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

@Getter
public class AnarchyCore extends PluginBase {

	public static final String PREFIX = "| ";

	@Getter
	private static AnarchyCore instance;

	private PlayerManager playerManager;
	private EconomyManager economyManager;

	@Override
	public void onLoad() {
		instance = this;
	}

	@Override()
	public void onEnable() {
		this.playerManager = new PlayerManager(this);

		SimpleCommandMap commandMap = this.getServer().getCommandMap();
		this.economyManager = new EconomyManager(this.playerManager, commandMap);

		this.unregisterCommands();
		this.registerCommands(commandMap);
	}

	@Override
	public void onDisable() {
		this.playerManager.saveAll();
	}

	private void unregisterCommands() {
		Map<String, Command> commandMap = this.getServer().getCommandMap().getCommands();
		Stream.of("version", "seed", "help", "?").forEach(commandMap::remove);
	}

	// TODO defaults commands
	private void registerCommands(SimpleCommandMap commandMap) {
		commandMap.registerAll(this.getName(), Collections.emptyList());
	}
}
