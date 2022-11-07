package me.iwareq.anarchy.module.blockprotection.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BlockData {

	private final int id;
	private final String name;
	private final int radius;
	private final String image;
}
