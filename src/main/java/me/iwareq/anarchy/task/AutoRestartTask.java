package me.iwareq.anarchy.task;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.scheduler.Task;

public class AutoRestartTask extends Task {

	private static final int RESTART_DELAY = 3 * 60 * 60;

	private int left = RESTART_DELAY;

	@Override
	public void onRun(int currentTick) {
		this.left--;

		if (this.left == 5 * 60) {
			Server.getInstance().broadcastMessage("§l§7(§3Перезагрузка§7) §rСервер перезагрузится через §65 §fминут!");
		}

		if (this.left == 10) {
			Server.getInstance().broadcastMessage("§l§7(§3Перезагрузка§7) §rСервер перезагрузится через §610 §fсекунд!");
		}

		if (this.left <= 10) {
			for (Player player : Server.getInstance().getOnlinePlayers().values()) {
				player.sendTitle("§lПерезагрузка", "§lСервер перезагрузится через §6" + this.left + " §fсек.", 0, 20, 0);
			}
		}

		if (this.left <= 0) {
			Server server = Server.getInstance();
			server.getOnlinePlayers().values().forEach(player ->
					player.close(player.getLeaveMessage(), "§l§6Перезагрузка"));

			server.doAutoSave();
			server.shutdown();
			this.left = RESTART_DELAY;
		}
	}
}
