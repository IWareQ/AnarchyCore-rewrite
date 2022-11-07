package me.iwareq.anarchy.module.blockprotection.util;

import cn.nukkit.level.Position;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Area3D {

	private final int minX, minY, minZ;
	private final int maxX, maxY, maxZ;

	public Area3D(Position main, int radius) {
		Position min = main.subtract(radius, radius, radius);

		this.minX = min.getFloorX();
		this.minY = min.getFloorY();
		this.minZ = min.getFloorZ();

		Position max = main.add(radius, radius, radius);

		this.maxX = max.getFloorX();
		this.maxY = max.getFloorY();
		this.maxZ = max.getFloorZ();
	}

	public boolean isCollided(Area3D area3D) {
		return this.maxX >= area3D.getMinX() && this.minX <= area3D.getMaxX() &&
				this.maxY >= area3D.getMinY() && this.minY <= area3D.getMaxY() &&
				this.maxZ >= area3D.getMinZ() && this.minZ <= area3D.getMaxZ();
	}

	public boolean isCollided(Position position) {
		int x = position.getFloorX();
		int y = position.getFloorY();
		int z = position.getFloorZ();

		return this.maxX >= x && this.minX <= x &&
				this.maxY >= y && this.minY <= y &&
				this.maxZ >= z && this.minZ <= z;
	}
}
