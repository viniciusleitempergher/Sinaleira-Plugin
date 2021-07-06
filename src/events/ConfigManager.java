package events;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigManager {
	private File file;
	private FileConfiguration config;

	public ConfigManager(String name) {
		this.file = new File(Main.m.getDataFolder(), String.valueOf(name) + ".yml");
		this.config = (FileConfiguration) YamlConfiguration.loadConfiguration(this.file);
	}

	public FileConfiguration getConfig() {
		if (this.config == null) {
			reloadConfig();
		}
		return this.config;
	}

	public File getFile() {
		return this.file;
	}

	public void reloadConfig() {
		this.config = (FileConfiguration) YamlConfiguration.loadConfiguration(this.file);
		InputStream imputStream = Main.m.getResource(this.file.getName());
		if (imputStream != null) {
			YamlConfiguration imputConfig = YamlConfiguration.loadConfiguration(imputStream);
			getConfig().setDefaults((Configuration) imputConfig);
		}
	}

	public void saveConfig() {
		try {
			getConfig().save(this.file);
		} catch (IOException iOException) {
		}
	}

	public void saveDefault() {
		getConfig().options().copyDefaults(true);
		saveConfig();
	}

	public void saveDefaultConfig() {
		Main.m.saveResource(this.file.getName(), true);
	}

	public double getDouble(String path) {
		return this.config.getDouble(path);
	}
}