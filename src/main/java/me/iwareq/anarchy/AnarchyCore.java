package me.iwareq.anarchy;

import cn.nukkit.command.Command;
import cn.nukkit.command.SimpleCommandMap;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.ServerScheduler;
import lombok.Getter;
import me.iwareq.anarchy.command.TestCommand;
import me.iwareq.anarchy.module.auction.AuctionManager;
import me.iwareq.anarchy.module.blockprotection.BlockProtectionManager;
import me.iwareq.anarchy.module.economy.EconomyManager;
import me.iwareq.anarchy.module.permission.PermissionManager;
import me.iwareq.anarchy.player.PlayerManager;
import me.iwareq.anarchy.scheme.SchemeLoader;
import me.iwareq.anarchy.scoreboard.Scoreboards;
import me.iwareq.anarchy.task.ClearLagTask;
import me.iwareq.anarchy.task.RestartTask;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

@Getter
public class AnarchyCore extends PluginBase {

	public static final String PREFIX = "| ";

	@Getter
	private static AnarchyCore instance;

	private PlayerManager playerManager;
	private PermissionManager permissionManager;
	private AuctionManager auctionManager;
	private BlockProtectionManager blockProtectionManager;

	@Override
	public void onLoad() {
		instance = this;

		SchemeLoader.init();
	}

	@Override()
	public void onEnable() {
		this.playerManager = new PlayerManager(this);

		SimpleCommandMap commandMap = this.getServer().getCommandMap();
		EconomyManager.init(this.playerManager, commandMap);

		this.permissionManager = new PermissionManager(this);

		this.auctionManager = new AuctionManager(this, commandMap);

		this.blockProtectionManager = new BlockProtectionManager(this);

		Scoreboards.init(this.playerManager);

		this.registerTasks();

		this.unregisterCommands();
		this.registerCommands(commandMap);
	}

	@Override
	public void onDisable() {
		this.playerManager.saveAll();
		this.auctionManager.saveItems();
		this.blockProtectionManager.saveAll();
	}

	private void unregisterCommands() {
		Map<String, Command> commandMap = this.getServer().getCommandMap().getCommands();
		Stream.of("version", "seed", "help", "?").forEach(commandMap::remove);
	}

	private void registerTasks() {
		ServerScheduler scheduler = this.getServer().getScheduler();
		scheduler.scheduleRepeatingTask(new ClearLagTask(), 20, true);
		scheduler.scheduleRepeatingTask(new RestartTask(), 20, true);
	}

	// TODO defaults commands
	private void registerCommands(SimpleCommandMap commandMap) {
		commandMap.registerAll(this.getName(), Collections.singletonList(new TestCommand()));
	}
}
