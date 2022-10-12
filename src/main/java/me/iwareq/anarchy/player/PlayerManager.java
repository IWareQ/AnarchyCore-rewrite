package me.iwareq.anarchy.player;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import me.hteppl.data.database.SQLiteDatabase;
import me.iwareq.anarchy.AnarchyCore;
import me.iwareq.anarchy.module.economy.EconomyManager;
import org.sql2o.data.Row;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerManager extends SQLiteDatabase implements Listener {

	private final Map<String, PlayerData> players = new HashMap<>();

	public PlayerManager(AnarchyCore main) {
		super("players");

		this.executeScheme("CREATE TABLE IF NOT EXISTS Players\n" +
				"(\n" +
				"    ID       INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
				"    Username VARCHAR(32) NOT NULL COLLATE NOCASE,\n" +
				"    Money    INT         NOT NULL DEFAULT '" + EconomyManager.MONEY_FORMAT + "'\n" +
				");");

		main.getServer().getPluginManager().registerEvents(this, main);
	}

	public void loadData(Player player) {
		if (!this.isLoaded(player)) {
			PlayerData playerData = new PlayerData(player);
			List<Row> data = this.getConnection()
					.createQuery("SELECT Money FROM Players WHERE Username = :username;")
					.addParameter("username", player.getName())
					.executeAndFetchTable()
					.rows();

			if (data.isEmpty()) {
				this.getConnection()
						.createQuery("INSERT INTO Players (Username) VALUES (:username);")
						.addParameter("username", player.getName())
						.executeUpdate();
			} else {
				data.forEach(row -> {
					// playerData.setMoney(row.getBigDecimal("Money"));
					playerData.setMoney(new BigDecimal(row.getString("Money")));
				});
			}

			this.players.put(player.getName(), playerData);
		}
	}

	public void saveData(Player player) {
		if (this.isLoaded(player)) {
			PlayerData data = this.getData(player);
			this.getConnection()
					.createQuery("UPDATE Players SET Money = :money WHERE Username = :username;")
					.addParameter("money", data.getMoney())
					.addParameter("username", data.getPlayer().getName())
					.executeUpdate();

			this.players.remove(player.getName());
		}
	}

	public PlayerData getData(Player player) {
		return this.players.get(player.getName());
	}

	public boolean isLoaded(Player player) {
		return this.players.containsKey(player.getName());
	}

	public void saveAll() {
		this.players.values().forEach(data -> this.saveData(data.getPlayer()));
	}

	@EventHandler(priority = EventPriority.MONITOR,
	              ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event) {
		this.loadData(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR,
	              ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent event) {
		this.saveData(event.getPlayer());
	}
}
