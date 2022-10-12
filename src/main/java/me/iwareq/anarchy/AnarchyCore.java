package me.iwareq.anarchy;

import cn.nukkit.plugin.PluginBase;
import lombok.Getter;

public class AnarchyCore extends PluginBase {

	@Getter
	private static AnarchyCore instance;

	@Override
	public void onLoad() {
		instance = this;
	}

	@Override()
	public void onEnable() {

	}
}
