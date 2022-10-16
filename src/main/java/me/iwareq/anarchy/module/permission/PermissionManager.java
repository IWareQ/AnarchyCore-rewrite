package me.iwareq.anarchy.module.permission;

import cn.nukkit.utils.Config;
import lombok.extern.log4j.Log4j2;
import me.iwareq.anarchy.AnarchyCore;
import me.iwareq.anarchy.module.permission.command.SetGroupCommand;
import me.iwareq.anarchy.module.permission.listener.ChatListener;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Log4j2
public class PermissionManager {

	private final Map<String, Group> groups = new HashMap<>();

	public PermissionManager(AnarchyCore main) {
		main.saveResource("groups.yml");

		File file = new File(main.getDataFolder() + "/groups.yml");
		Config config = new Config(file, Config.YAML);
		config.getAll().forEach((id, data) -> {
			String format = config.getString(id + ".format");
			Set<String> permissions = new HashSet<>(config.getStringList(id + ".permissions"));

			groups.put(id, new Group(id, format, permissions));
		});

		main.getServer().getCommandMap().register(main.getName(), new SetGroupCommand(this));
		main.getServer().getPluginManager().registerEvents(new ChatListener(), main);
	}

	public Group getGroup(String groupId) {
		return this.groups.get(groupId);
	}

	public List<String> getGroupIds() {
		return Arrays.asList(this.groups.keySet().toArray(new String[0]));
	}
}
