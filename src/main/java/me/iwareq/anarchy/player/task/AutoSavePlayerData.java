package me.iwareq.anarchy.player.task;

import cn.nukkit.scheduler.Task;
import lombok.AllArgsConstructor;
import me.iwareq.anarchy.player.PlayerManager;

@AllArgsConstructor
public class AutoSavePlayerData extends Task {

	private final PlayerManager manager;

	@Override
	public void onRun(int currentTick) {
		this.manager.saveAll();
	}
}
