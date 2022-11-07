package me.iwareq.anarchy.scheme;

import me.iwareq.anarchy.AnarchyCore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class SchemeLoader {

	private static final Map<String, String> SCHEMES = new HashMap<>();

	private static final Path FOLDER = Paths.get(AnarchyCore.getInstance().getDataFolder().toURI()).resolve("schemes");

	public static void init() {
		SchemeLoader.load("players.sql");
		SchemeLoader.load("auction.sql");
		SchemeLoader.load("region.sql");
	}

	private static void load(String scheme) {
		AnarchyCore.getInstance().saveResource("schemes/" + scheme);
		try {
			scheme = new String(Files.readAllBytes(FOLDER.resolve(scheme)));

			String[] keys = scheme.split("-- data.");
			String[] values = scheme.split("-- data.+([A-Za-z0-9]+(\\.[A-Za-z0-9]+)+)");

			for (int i = 0; i < keys.length; i++) {
				String key = keys[i];
				key = key.split("\\n")[0];

				String value = values[i];

				if (!key.isEmpty() && !value.isEmpty()) {
					SCHEMES.put(key.trim(), value.trim());
				}
			}
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	public static String scheme(String key) {
		return SCHEMES.get(key);
	}
}
