package me.iwareq.anarchy.module.blockprotection.form;

import cn.nukkit.Player;
import cn.nukkit.Server;
import me.iwareq.anarchy.module.blockprotection.data.RegionData;
import ru.contentforge.formconstructor.form.SimpleForm;

public class RemovePlayerForm extends SimpleForm {

	public RemovePlayerForm(RegionData data) {
		super("Удаление участника");

		this.addContent("Выберите §6участника§7, §fкоторого хотите §6удалить §fиз региона§7:");
		for (String memberName : data.getMembers()) {
			this.addButton("§6" + memberName + "\n§fНажмите§7, §fчтобы удалить§7!", (player, button) -> {
				data.removeMember(memberName);

				Player target = Server.getInstance().getPlayerExact(memberName);
				if (target != null) {
					target.sendMessage("Игрок §6" + player.getName() + " §fудалил вас из своего региона§7!");
				}

				player.sendMessage("Игрок §6" + memberName + " §fудален из региона§7!");
			});
		}
	}
}
