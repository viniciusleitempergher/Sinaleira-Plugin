package events;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Main extends JavaPlugin {
	public static Main m;
	public static FileConfiguration cf;
	public static BukkitScheduler sh;
	public static PluginManager pm;
	public static ConfigManager configs;

	public void onEnable() {
		Bukkit.getConsoleSender().sendMessage("§2-=§aSinaleiraPL habilitado.§2=-");
		getCommand("sinaleira").setExecutor(new Comandos());
		pm.registerEvents(new Eventos(), (Plugin) this);
		configs = new ConfigManager("configs");
	}

	public void onLoad() {
		m = this;
		cf = getConfig();
		sh = Bukkit.getScheduler();
		pm = Bukkit.getPluginManager();
	}

	public void onDisable() {
		Bukkit.getConsoleSender().sendMessage("§4-=§cSinaleiraPL desabilitado.§4=-");
		HandlerList.unregisterAll(new Eventos());
	}

	public static Plugin getPlugin() {
		return (Plugin) m;
	}
}
