package me.iwareq.anarchy.module.blockprotection;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;
import lombok.Getter;
import me.hteppl.data.database.SQLiteDatabase;
import me.iwareq.anarchy.AnarchyCore;
import me.iwareq.anarchy.module.blockprotection.command.RegionCommand;
import me.iwareq.anarchy.module.blockprotection.data.BlockData;
import me.iwareq.anarchy.module.blockprotection.data.RegionData;
import me.iwareq.anarchy.module.blockprotection.listener.ControlRegionListener;
import me.iwareq.anarchy.module.blockprotection.listener.RegionLimitListener;
import me.iwareq.anarchy.module.blockprotection.task.SaveRegionsData;
import me.iwareq.anarchy.module.blockprotection.util.Area3D;
import org.sql2o.Connection;
import org.sql2o.data.Row;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static me.iwareq.anarchy.scheme.SchemeLoader.scheme;

@Getter
public class BlockProtectionManager extends SQLiteDatabase {

	private static final int AUTO_SAVE_DELAY = 10 * 60 * 20; // 10 min

	private static final AtomicInteger ID = new AtomicInteger(1);

	private final Map<Integer, RegionData> regions = new ConcurrentHashMap<>();

	private final Map<Integer, BlockData> blocks = new HashMap<>();

	public BlockProtectionManager(AnarchyCore main) {
		super("region");

		this.executeScheme(scheme("regions.init"));

		int lastId = this.getConnection().createQuery(scheme("regions.select.lastId")).executeScalar(Integer.class);

		ID.set(lastId);

		main.saveResource("regions.yml");

		File file = new File(main.getDataFolder() + "/regions.yml");
		Config config = new Config(file, Config.YAML);
		config.getAll().forEach((id, data) -> {
			String name = config.getString(id + ".name");
			int radius = config.getInt(id + ".radius");
			String image = config.getString(id + ".image");

			int blockId = Integer.parseInt(id);
			blocks.put(blockId, new BlockData(blockId, name, radius, image));
		});

		this.loadAll();

		main.getServer().getCommandMap().register("region", new RegionCommand(this));

		main.getServer().getPluginManager().registerEvents(new ControlRegionListener(this), main);
		main.getServer().getPluginManager().registerEvents(new RegionLimitListener(this), main);

		main.getServer().getScheduler().scheduleRepeatingTask(new SaveRegionsData(this), AUTO_SAVE_DELAY, true);
	}

	public void loadAll() {
		List<Row> regionsData = this.connection.createQuery(scheme("regions.select.all"))
				.executeAndFetchTable()
				.rows();

		for (Row data : regionsData) {
			String ownerName = data.getString("OwnerName");

			int regionBlockX = data.getInteger("MainX");
			int regionBlockY = data.getInteger("MainY");
			int regionBlockZ = data.getInteger("MainZ");
			Position regionBlockPosition = new Position(regionBlockX, regionBlockY, regionBlockZ);

			int minX = data.getInteger("MinX");
			int minY = data.getInteger("MinY");
			int minZ = data.getInteger("MinZ");

			int maxX = data.getInteger("MaxX");
			int maxY = data.getInteger("MaxY");
			int maxZ = data.getInteger("MaxZ");

			Area3D area3D = new Area3D(minX, minY, minZ, maxX, maxY, maxZ);

			int regionId = data.getInteger("ID");

			RegionData regionData = new RegionData(regionId, ownerName, regionBlockPosition, area3D);

			String[] members = data.getString("Members").split(";");

			for (String member : members) {
				regionData.addMember(member);
			}

			this.regions.put(regionId, regionData);
		}
	}

	public synchronized void saveAll() {
		List<Integer> ids = this.connection.createQuery(scheme("regions.select.all.ids"))
				.executeScalarList(Integer.class);

		try (Connection transaction = this.getSql2o().beginTransaction()) {
			this.regions.forEach((id, regionData) -> {
				ids.remove(id);

				Position regionBlock = regionData.getRegionBlock();

				Area3D area3D = regionData.getArea3D();
				Set<String> members = regionData.getMembers();

				StringJoiner joiner = new StringJoiner(";");
				members.forEach(joiner::add);

				transaction.createQuery(scheme("regions.insert"))
						.addParameter("id", id)
						.addParameter("ownerName", regionData.getOwnerName())

						.addParameter("members", joiner)

						.addParameter("mainX", regionBlock.getFloorX())
						.addParameter("mainY", regionBlock.getFloorY())
						.addParameter("mainZ", regionBlock.getFloorZ())

						.addParameter("minX", area3D.getMinX())
						.addParameter("minY", area3D.getMinY())
						.addParameter("minZ", area3D.getMinZ())

						.addParameter("maxX", area3D.getMaxX())
						.addParameter("maxY", area3D.getMaxY())
						.addParameter("maxZ", area3D.getMaxZ())
						.executeUpdate();
			});

			ids.forEach(id ->
					transaction.createQuery(scheme("regions.delete"))
							.addParameter("id", id)
							.executeUpdate());

			transaction.commit();
		}
	}

	public void createRegion(Player player, Block block) {
		BlockData blockData = this.blocks.get(block.getId());
		int radius = blockData.getRadius();

		Position main = block.getLocation();

		Area3D area3D = new Area3D(main, radius);
		if (this.cannotCreateRegion(area3D)) {
			player.sendMessage("Не возможно установить блок §6в выбраном месте §fиз§7-§fза §6пересечения §fрегионов§7!");
			return;
		}

		RegionData regionData = new RegionData(ID.incrementAndGet(), player.getName(), main, area3D);
		regions.put(regionData.getId(), regionData);

		player.sendMessage("Вы успешно создали новый " + blockData.getName() + "§7!");
	}

	private boolean cannotCreateRegion(Area3D area3D) {
		for (RegionData regionData : this.regions.values()) {
			if (regionData.getArea3D().isCollided(area3D)) {
				return true;
			}
		}

		return false;
	}

	public RegionData getRegionData(Position main) {
		for (RegionData regionData : this.regions.values()) {
			Area3D area3D = regionData.getArea3D();
			if (area3D.isCollided(main)) {
				return regionData;
			}
		}

		return null;
	}

	public Set<RegionData> getRegionsData(String playerName) {
		Set<RegionData> result = new HashSet<>();
		for (RegionData regionData : this.regions.values()) {
			if (regionData.isOwner(playerName)) {
				result.add(regionData);
			}
		}

		return result;
	}

	public void deleteRegion(RegionData regionData) {
		this.regions.remove(regionData.getId());
	}

	public boolean cannotInteractHere(Player player, Position position) {
		RegionData regionData = this.getRegionData(position);
		if (regionData != null) {
			return !regionData.contains(player.getName());
		}

		return false;
	}

	public BlockData getBlockData(int blockId) {
		return this.blocks.get(blockId);
	}
}
