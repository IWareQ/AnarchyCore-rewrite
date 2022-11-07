package me.iwareq.anarchy.module.blockprotection.task;

import cn.nukkit.scheduler.Task;
import lombok.AllArgsConstructor;
import me.iwareq.anarchy.module.blockprotection.BlockProtectionManager;

@AllArgsConstructor
public class SaveRegionsData extends Task {

	private final BlockProtectionManager manager;

	@Override
	public void onRun(int currentTick) {
		this.manager.saveAll();
	}
}
