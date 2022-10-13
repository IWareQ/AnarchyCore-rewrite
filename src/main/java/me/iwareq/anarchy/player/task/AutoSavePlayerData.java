package me.iwareq.anarchy.player.task;

import cn.nukkit.scheduler.Task;
import me.iwareq.anarchy.player.PlayerManager;

public class AutoSavePlayerData extends Task {

	private static final int SAVE_DELAY = 5;

	private final PlayerManager manager;

	private int left = SAVE_DELAY;

	public AutoSavePlayerData(PlayerManager manager) {
		this.manager = manager;
	}

	@Override
	public void onRun(int currentTick) {
		this.left--;

		if (this.left <= 0) {
			this.manager.saveAll();

			this.left = SAVE_DELAY;
		}
	}
}
