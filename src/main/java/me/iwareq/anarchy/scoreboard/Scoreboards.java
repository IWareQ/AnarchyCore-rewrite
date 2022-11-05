package me.iwareq.anarchy.scoreboard;

import cn.nukkit.Player;
import cn.nukkit.Server;
import me.iwareq.anarchy.player.PlayerData;
import me.iwareq.anarchy.player.PlayerManager;
import me.iwareq.scoreboard.Scoreboard;

public class Scoreboards {

	private static Scoreboard scoreboard;

	public static void init(PlayerManager manager) {
		Scoreboards.scoreboard = new Scoreboard("§3DEATH §fMC", (sb, player) -> {
			PlayerData data = manager.getData(player);
			sb.addLine(data.getGroup().getFormat() + player.getName());
			sb.addLine("§1");
			sb.addLine("§rКлан§7: нету"); // todo clans
			sb.addLine("§rПинг§7: " + player.getPing());
			sb.addLine("§rБаланс§7: §6" + data.getMoney());
			sb.addLine("§rОнлайн§7: §6" + Server.getInstance().getOnlinePlayers().size());
		}, 1);
	}

	public static void showScoreboard(Player player) {
		Scoreboards.scoreboard.show(player);
	}
}
