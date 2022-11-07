package me.iwareq.anarchy.module.blockprotection.listener;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.level.Position;
import lombok.AllArgsConstructor;
import me.iwareq.anarchy.module.blockprotection.BlockProtectionManager;
import me.iwareq.anarchy.module.blockprotection.data.RegionData;

@AllArgsConstructor
public class ControlRegionListener implements Listener {

	private final BlockProtectionManager manager;

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();

		if (this.manager.getBlocks().containsKey(block.getId())) {
			this.manager.createRegion(player, block);
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();

		Position regionBlock = block.getLocation();

		RegionData regionData = this.manager.getRegionData(regionBlock);
		if (regionData != null) {
			if (regionData.getRegionBlock().equals(regionBlock)) {
				if (regionData.isOwner(player.getName())) {
					this.manager.deleteRegion(regionData);

					player.sendMessage("Регион успешно удален!");
				} else {
					player.sendMessage("Вы не можете удалить чужой регион!");
					event.setCancelled(true);
				}
			}
		}
	}
}
