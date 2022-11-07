package me.iwareq.anarchy.module.blockprotection.listener;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.Event;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.block.ItemFrameDropItemEvent;
import cn.nukkit.event.entity.EntityExplodeEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.level.Position;
import lombok.AllArgsConstructor;
import me.iwareq.anarchy.module.blockprotection.BlockProtectionManager;
import me.iwareq.anarchy.module.blockprotection.data.RegionData;

import java.util.List;

@AllArgsConstructor
public class RegionLimitListener implements Listener {

	private final BlockProtectionManager manager;

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();

		this.cannotInteractHere(player, block.getLocation(), event);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();

		this.cannotInteractHere(player, block.getLocation(), event);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityExplode(EntityExplodeEvent event) {
		List<Block> blocks = event.getBlockList();
		blocks.removeIf(block -> {
			Position regionBlock = block.getLocation();

			RegionData regionData = this.manager.getRegionData(regionBlock);
			if (regionData != null) {
				return regionData.getRegionBlock().equals(regionBlock);
			}

			return false;
		});

		event.setBlockList(blocks);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onItemFrameDropItem(ItemFrameDropItemEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();

		if (player != null) {
			this.cannotInteractHere(player, block.getLocation(), event);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();

		this.cannotInteractHere(player, block.getLocation(), event);
	}

	private void cannotInteractHere(Player player, Position position, Event event) {
		if (this.manager.cannotInteractHere(player, position)) {
			player.sendTip("Территория §6не доступна §fдля взаимодействия");
			event.setCancelled(true);
		}
	}
}
