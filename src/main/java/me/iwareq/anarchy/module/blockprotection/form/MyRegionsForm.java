package me.iwareq.anarchy.module.blockprotection.form;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import me.iwareq.anarchy.module.blockprotection.BlockProtectionManager;
import me.iwareq.anarchy.module.blockprotection.data.BlockData;
import me.iwareq.anarchy.module.blockprotection.data.RegionData;
import ru.contentforge.formconstructor.form.SimpleForm;
import ru.contentforge.formconstructor.form.element.ImageType;

import java.util.Set;

public class MyRegionsForm extends SimpleForm {

	public MyRegionsForm(BlockProtectionManager manager, Player player) {
		super("Мои регионы");

		this.addContent("Выберите один из регионов§7, §fс которым хотите §6взаимодействовать§7.");
		this.addContent("\n\n§fСписок §6Ваших §fрегионов§7:");

		Set<RegionData> regionsData = manager.getRegionsData(player.getName());
		if (regionsData.isEmpty()) {
			this.addContent("\n\n§fВы не имеете регионов§7!");
		}

		regionsData.forEach(regionData -> {
			Level world = Server.getInstance().getLevelByName("world");
			Position mainPosition = regionData.getRegionBlock();
			BlockData blockData = manager.getBlockData(world.getBlock(mainPosition).getId());
			this.addButton(blockData.getName() + "\n§f" + mainPosition.getFloorX() + "§7, §f" + mainPosition.getFloorY() + "§7, §f" + mainPosition.getFloorZ(), ImageType.PATH, blockData.getImage(), (p, button) -> {
				new RegionEditForm(manager, player).sendAsync(player);
			});
		});

		this.addButton("Назад", ImageType.PATH, "textures/ui/back_button_default", (p, button) -> {
			new MenuForm(manager).sendAsync(player);
		});
	}
}
