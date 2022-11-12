package me.iwareq.anarchy.module.blockprotection.data;

import cn.nukkit.level.Position;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.iwareq.anarchy.module.blockprotection.util.Area3D;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Getter
@AllArgsConstructor
public class RegionData {

	private final int id;

	private final String ownerName;

	private final Set<String> members = new HashSet<>();

	private final Position regionBlock;

	private final Area3D area3D;

	public void addMember(String playerName) {
		if (!playerName.isEmpty()) {
			this.members.add(playerName);
		}
	}

	public void removeMember(String playerName) {
		this.members.remove(playerName);
	}

	public boolean contains(String playerName) {
		return this.ownerName.equals(playerName) || this.members.contains(playerName);
	}

	public boolean isOwner(String playerName) {
		return this.ownerName.equals(playerName);
	}

	public void addMembers(Collection<String> members) {
		this.members.addAll(members);
	}
}
