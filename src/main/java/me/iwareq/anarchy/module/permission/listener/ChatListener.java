package me.iwareq.anarchy.module.permission.listener;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;
import lombok.AllArgsConstructor;
import me.iwareq.anarchy.AnarchyCore;
import me.iwareq.anarchy.player.PlayerData;
import me.iwareq.anarchy.player.PlayerManager;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
public class ChatListener implements Listener {

	private static final int LOCAL_CHAT_RADIUS = 70;

	private final PlayerManager manager = AnarchyCore.getInstance().getPlayerManager();

	private final Server server = Server.getInstance();

	@EventHandler
	public void onPlayerChat(PlayerChatEvent event) {
		Player player = event.getPlayer();
		String message = event.getMessage();

		PlayerData data = this.manager.getData(player);
		String displayName = data.getGroup().getFormat() + " " + player.getName();

		Set<CommandSender> recipients = new HashSet<>();
		recipients.add(this.server.getConsoleSender());
		if (String.valueOf(message.charAt(0)).equals("!")) {
			recipients.addAll(this.server.getOnlinePlayers().values());

			event.setRecipients(recipients);

			event.setFormat("§7(§aG§7) " + displayName + " §8» §7" + this.formatMessage(message.substring(1)));
		} else {
			for (Player target : this.server.getOnlinePlayers().values()) {
				if (target.getLevel() == player.getLevel() && player.distance(target) <= LOCAL_CHAT_RADIUS) {
					recipients.add(target);
				}
			}

			event.setRecipients(recipients);

			event.setFormat("§7(§6L§7) " + displayName + " §8» §f" + this.formatMessage(message));

			if (recipients.size() == 2) {
				player.sendTitle("Никто §cне увидел §rсообшение", "Вы использовали локальный чат\n" + "Поставьте перед сообщением знак !\n" + "чтобы написать в глобальный чат");
			}
		}
	}

	private String formatMessage(String message) {
		return message.replaceAll("§", "");
	}
}
