package me.iwareq.anarchy.module.blockprotection.form;

import me.iwareq.anarchy.module.blockprotection.BlockProtectionManager;
import ru.contentforge.formconstructor.form.SimpleForm;
import ru.contentforge.formconstructor.form.element.ImageType;

public class MenuForm extends SimpleForm {

	public MenuForm(BlockProtectionManager manager) {
		super("Регионы");

		this.addContent("Вы находитесь в главном меню взаимодействия с регионами§7.");
		// this.addContent("\n\n§fМаксимальное кол§7-§fво §6Регионов §fдля Вас§7: §6" + PermissionAPI.getPlayerGroup(player.getName()).getMaxRegions());
		this.addContent("\n\n§fВыберите §6нужный §fВам пункт§7:");

		this.addButton("Мои регионы", ImageType.PATH, "textures/ui/absorption_effect", (player, button) -> {
			new MyRegionsForm(manager, player).sendAsync(player);
		});

		this.addButton("Членство в регионах", ImageType.PATH, "textures/ui/dressing_room_skins", (player, button) -> {
			// this.openMembershipInRegionsForm(player);
		});

		this.addButton("Гайд по регионам", ImageType.PATH, "textures/ui/how_to_play_button_default_light", (player, button) -> {
			// this.openGuidForm(player);
		});
	}
}
