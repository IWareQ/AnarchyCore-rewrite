package me.iwareq.anarchy.player;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import me.hteppl.data.database.SQLiteDatabase;
import me.iwareq.anarchy.AnarchyCore;
import me.iwareq.anarchy.module.permission.PermissionManager;
import me.iwareq.anarchy.player.task.SavePlayersData;
import me.iwareq.anarchy.scoreboard.Scoreboards;
import org.sql2o.data.Row;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

import static me.iwareq.anarchy.scheme.SchemeLoader.scheme;

public class PlayerManager extends SQLiteDatabase implements Listener {

	private static final int AUTO_SAVE_DELAY = 10 * 60 * 20; // 10 min

	private final Map<String, PlayerData> players = new ConcurrentHashMap<>();

	public PlayerManager(AnarchyCore main) {
		super("players");

		this.executeScheme(scheme("players.init"));

		main.getServer().getPluginManager().registerEvents(this, main);
		main.getServer().getScheduler().scheduleRepeatingTask(new SavePlayersData(this), AUTO_SAVE_DELAY, true);
	}

	public void loadData(Player player) {
		if (!this.isLoaded(player)) {
			PlayerData playerData = new PlayerData(player);
			List<Row> data = this.connection.createQuery(scheme("players.select.all"))
					.addParameter("username", player.getName())
					.executeAndFetchTable()
					.rows();

			if (data.isEmpty()) {
				this.connection.createQuery(scheme("players.insert"))
						.addParameter("username", player.getName())
						.executeUpdate();
			} else {
				data.forEach(row -> {
					playerData.setMoney(row.getString("Money"));
					PermissionManager manager = AnarchyCore.getInstance().getPermissionManager();
					playerData.setGroup(manager.getGroup(row.getString("GroupId")));
				});
			}

			this.players.put(player.getName(), playerData);
		}
	}

	public void saveData(Player player) {
		if (this.isLoaded(player)) {
			PlayerData data = this.getData(player);
			this.connection.createQuery(scheme("players.save"))
					.addParameter("money", data.getMoney())
					.addParameter("group", data.getGroup().getId())
					.addParameter("username", data.getPlayer().getName())
					.executeUpdate();

			this.players.remove(player.getName());
		}
	}

	public PlayerData getData(Player player) {
		return this.players.get(player.getName());
	}

	public void getOfflineData(String name, BiConsumer<PlayerData, String> consumer) {
		Player player = Server.getInstance().getPlayerExact(name);
		if (this.isLoaded(player)) {
			consumer.accept(this.getData(player), player.getName());
			return;
		}

		PlayerData offlineData = new PlayerData(player);
		List<Row> data = this.connection.createQuery(scheme("players.select.all"))
				.addParameter("username", name)
				.executeAndFetchTable()
				.rows();

		if (data.isEmpty()) {
			consumer.accept(null, name);
		} else {
			for (Row row : data) {
				name = row.getString("Username");

				offlineData.setMoney(row.getString("Money"));
				PermissionManager manager = AnarchyCore.getInstance().getPermissionManager();
				offlineData.setGroup(manager.getGroup(row.getString("GroupId")));
			}

			consumer.accept(offlineData, name);

			this.connection.createQuery(scheme("players.save.all"))
					.addParameter("money", offlineData.getMoney())
					.addParameter("group", offlineData.getGroup().getId())
					.addParameter("username", name)
					.executeUpdate();
		}
	}

	public boolean isLoaded(Player player) {
		if (player == null) {
			return false;
		}

		return this.players.containsKey(player.getName());
	}

	public void saveAll() {
		this.players.values().forEach(data -> this.saveData(data.getPlayer()));
	}

	@EventHandler(priority = EventPriority.MONITOR,
	              ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		this.loadData(player);

		Scoreboards.showScoreboard(player);
	}

	@EventHandler(priority = EventPriority.MONITOR,
	              ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent event) {
		this.saveData(event.getPlayer());
	}
}
