package me.iwareq.anarchy.module.auction.task;

import cn.nukkit.scheduler.Task;
import lombok.AllArgsConstructor;
import me.iwareq.anarchy.module.auction.AuctionManager;

@AllArgsConstructor
public class SaveAuctionItems extends Task {

	private final AuctionManager manager;

	@Override
	public void onRun(int currentTick) {
		this.manager.saveItems();
	}
}
