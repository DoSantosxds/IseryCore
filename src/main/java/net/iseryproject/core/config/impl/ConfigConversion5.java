package net.iseryproject.core.config.impl;

import java.io.File;
import java.io.IOException;

import net.iseryproject.core.config.ConfigConversion;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigConversion5 implements ConfigConversion {

	@Override
	public void convert(File file, FileConfiguration fileConfiguration) {
		fileConfiguration.set("CONFIG_VERSION", 5);
		fileConfiguration.set("GLOBAL_WHITELIST.KICK_MAINTENANCE", "&6The server is currently in maintenance.\\nCheck our discord for more announcements!");
		fileConfiguration.set("GLOBAL_WHITELIST.KICK_CLOSED_TESTING", "&6You are not whitelisted. To gain early access, you can\\npurchase an eligible rank &7(&6Gold+&7) &6on our store.\\n&fhttps://store.zonix.us");

		try {
			fileConfiguration.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
