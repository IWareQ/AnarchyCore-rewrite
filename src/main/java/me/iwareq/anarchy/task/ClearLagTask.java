package me.iwareq.anarchy.task;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.scheduler.Task;
import me.iwareq.anarchy.AnarchyCore;
import me.iwareq.anarchy.util.CompletableFutureArray;

public class ClearLagTask extends Task {

	private static final int CLEAR_DELAY = 20 * 60;

	private int left = CLEAR_DELAY;

	@Override
	public void onRun(int currentTick) {
		this.left--;

		if (this.left == 5 * 60) {
			Server.getInstance().broadcastMessage(AnarchyCore.PREFIX + "Очистка произойдет через 5 минут!");
		}

		if (this.left <= 0) {
			this.clearAll();

			for (Player player : Server.getInstance().getOnlinePlayers().values()) {
				player.sendMessage("§l§7(§3Очистка§7) §rОчистка завершена§7!");
				player.sendTip("Очистка завершена!");
			}

			this.left = CLEAR_DELAY;
		}
	}

	private void clearAll() {
		CompletableFutureArray futureArray = new CompletableFutureArray();
		Server.getInstance().getLevels().values().forEach(level -> futureArray.add(() -> {
			for (Entity entity : level.getEntities()) {
				if (!(entity instanceof Player)) {
					entity.close();
				}
			}

			level.doChunkGarbageCollection();
		}));

		futureArray.execute();
	}
}
