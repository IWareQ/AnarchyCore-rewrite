package me.iwareq.anarchy.module.blockprotection.form;

import cn.nukkit.Player;
import cn.nukkit.level.Position;
import me.iwareq.anarchy.module.blockprotection.BlockProtectionManager;
import me.iwareq.anarchy.module.blockprotection.data.RegionData;
import ru.contentforge.formconstructor.form.SimpleForm;
import ru.contentforge.formconstructor.form.element.ImageType;

public class RegionEditForm extends SimpleForm {

	public RegionEditForm(BlockProtectionManager manager, Player player) {
		super("Мои регионы");

		Position playerPosition = player.getPosition();
		RegionData regionData = manager.getRegionData(playerPosition);
		if (regionData == null || !regionData.getArea3D().isCollided(playerPosition)) {
			this.addContent("Вы должны находиться внутри того региона, который хотите просмотреть§7!");
			return;
		}

		this.addContent("§l§6• §rКол§7-§fво участников§7: §6" + regionData.getMembers().size());
		this.addContent("\n\n§6Выберите §6нужное §fВам действие§7, §fкоторое хотите применить к данному §6Региону§7:");
		this.addButton("Удалить участника", (p, button) -> {
			new RemovePlayerForm(regionData).sendAsync(player);
		});

		this.addButton("Назад", ImageType.PATH, "textures/ui/back_button_default", (p, button) -> {
			new MyRegionsForm(manager, player).sendAsync(player);
		});
	}
}
