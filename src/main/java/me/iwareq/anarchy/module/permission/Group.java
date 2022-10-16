package me.iwareq.anarchy.module.permission;

import cn.nukkit.Player;
import cn.nukkit.permission.PermissionAttachment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.iwareq.anarchy.AnarchyCore;

import java.util.Set;

@AllArgsConstructor
public class Group {

	@Getter
	private final String id;
	@Getter
	private final String format;
	private final Set<String> permissionAttachment;

	public void init(Player player) {
		player.setNameTag(this.format + " " + player.getName());

		PermissionAttachment attachment = player.addAttachment(AnarchyCore.getInstance());
		attachment.clearPermissions();
		this.permissionAttachment.forEach(permission -> attachment.setPermission(permission, true));
	}
}
