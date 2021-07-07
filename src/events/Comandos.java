package events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class Comandos implements CommandExecutor, Listener {
	public static ItemStack itID;
	public static Map<Integer, Location> blocosSinal = new HashMap<>();
	static ArrayList<Player> pSinal = new ArrayList<>();
	public static Map<Integer, Player> participantes = new HashMap<>();
	public static int numPar = 0;
	public static int numSin = 0;

	public static boolean ocurr = false;
	public static Location camarote;
	public static Location inicio;
	public static Location spawn;

	public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("§4Apenas jogadores podem usar.");
			return true;
		}

		Player p = (Player) sender;
		if (cmd.getName().equalsIgnoreCase("sinaleira")) {
			if (args.length == 0) {
				p.sendMessage("§4[Sinaleira]§c Comando não existe, /sinaleira help para ajuda sobre comandos.");
				return true;
			}
			if (args[0].equalsIgnoreCase("entrar")) {
				if (!ocurr) {
					p.sendMessage("§4Evento Sinaleira não está acontecendo no momento.");
					return true;
				}
				byte b;
				int i;
				ItemStack[] arrayOfItemStack;
				for (i = (arrayOfItemStack = p.getInventory().getContents()).length, b = 0; b < i;) {
					ItemStack item = arrayOfItemStack[b];
					if (item != null) {
						p.sendMessage("§4[Sinaleira] §cEsvazie o inventário para participar do evento.");
						return true;
					}
					b++;
				}

				for (i = (arrayOfItemStack = p.getInventory().getArmorContents()).length, b = 0; b < i;) {
					ItemStack item = arrayOfItemStack[b];
					ItemStack armorTemp = new ItemStack(Material.AIR, 0);
					if (item.getTypeId() != armorTemp.getTypeId()) {
						p.sendMessage("§4[Sinaleira] §cEsvazie o inventário para participar do evento.");
						return true;
					}
					b++;
				}

				numPar++;
				participantes.put(Integer.valueOf(numPar), p);
				p.sendMessage("§aEntrando no evento sinaleira... ");
				p.setCanPickupItems(false);
				p.teleport(camarote);
				return true;
			}
			if (args[0].equalsIgnoreCase("sair")) {
				numPar--;
				int pSaiuKey = ((Integer) Eventos.<Integer, Player>getKey(participantes, p)).intValue();
				participantes.remove(Integer.valueOf(pSaiuKey));
				p.sendMessage("§cSaindo do evento sinaleira... ");
				p.setCanPickupItems(true);
				p.teleport(spawn);
				return true;
			}
			if (args[0].equalsIgnoreCase("camarote")) {
				if (participantes.size() == 0) {
					p.sendMessage("§4Evento Sinaleira não está acontecendo no momento.");
					return true;
				}
				p.teleport(camarote);
				p.sendMessage("§2[Sinaleira] §cEntrando no camarote do evento sinaleira...");
				return true;
			}

			if (args[0].equalsIgnoreCase("help")) {
				List<String> msg = Main.cf.getStringList("Help");
				for (int f = 0; f < msg.size(); f++) {
					String s = msg.get(f);
					s = s.replace('&', '§');
					p.sendMessage(s);
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("credits")) {
				p.sendMessage("§2[Sinaleira] §aPlugin by: §fvini_extreme1212 §axD");
				return true;
			}

			if (!p.hasPermission("sinaleira.set")) {
				p.sendMessage("§4[Sinaleira]§c Comando não existe, /sinaleira help para ajuda sobre comandos.");
				return true;
			}
			if (args[0].equalsIgnoreCase("admin")) {
				List<String> msg = Main.cf.getStringList("Admin");
				for (int f = 0; f < msg.size(); f++) {
					String s = msg.get(f);
					s = s.replace('&', '§');
					p.sendMessage(s);
				}
				return true;
			}

			if (args[0].equalsIgnoreCase("setPremio")) {
				int itemID = Integer.valueOf(Integer.parseInt(args[1])).intValue();
				int quant = Integer.valueOf(Integer.parseInt(args[2])).intValue();
				ItemStack item = new ItemStack(Material.getMaterial(itemID), 2);

				Main.configs.getConfig().set("premio", Integer.valueOf(itemID));
				Main.configs.getConfig().set("quantidadedopremio", Integer.valueOf(quant));
				Main.configs.saveConfig();

				p.sendMessage("§f Premio setado: " + Main.configs.getConfig().getInt("Premio") + ", "
						+ Main.configs.getConfig().getInt("QuantidadeDoPremio") + "\n " + item);
				return true;
			}

			if (args[0].equalsIgnoreCase("reset") && args[1].equalsIgnoreCase("sinal")) {
				p.sendMessage("§2[Sinaleira] §aResetando todas as configs de sinal!");
				Eventos.resetConfigSinal();
				return true;
			}

			if (args[0].equalsIgnoreCase("set")) {
				if (args[1].equalsIgnoreCase("block")) {
					Integer itemID = Integer.valueOf(Integer.parseInt(args[2]));
					ItemStack item = new ItemStack(Material.getMaterial(itemID.intValue()));
					itID = item;
					Main.configs.getConfig().set("blocoChegada", itID);
					Main.configs.saveConfig();
					p.sendMessage("§aBloco: §f" + item.getType() + " §asetado.");
					return true;
				}
				if (args[1].equalsIgnoreCase("camarote")) {
					camarote = p.getLocation();
					p.sendMessage("§2[Sinaleira] §aLocal de camarote setado!");

					Main.configs.getConfig().set("camarote.world", camarote.getWorld().getName());
					Main.configs.getConfig().set("camarote.x", Double.valueOf(camarote.getX()));
					Main.configs.getConfig().set("camarote.y", Double.valueOf(camarote.getY()));
					Main.configs.getConfig().set("camarote.z", Double.valueOf(camarote.getZ()));
					Main.configs.saveConfig();
					return true;
				}
				if (args[1].equalsIgnoreCase("inicio")) {
					inicio = p.getLocation();

					p.sendMessage("§2[Sinaleira] §aLocal de inicio setado!");

					Main.configs.getConfig().set("inicio.world", inicio.getWorld().getName());
					Main.configs.getConfig().set("inicio.x", Double.valueOf(inicio.getX()));
					Main.configs.getConfig().set("inicio.y", Double.valueOf(inicio.getY()));
					Main.configs.getConfig().set("inicio.z", Double.valueOf(inicio.getZ()));
					Main.configs.saveConfig();
					return true;
				}
				if (args[1].equalsIgnoreCase("spawn")) {
					spawn = p.getLocation();

					p.sendMessage("§2[Sinaleira] §aLocal de spawn do server setado!");

					Main.configs.getConfig().set("spawndoserver.world", spawn.getWorld().getName());
					Main.configs.getConfig().set("spawndoserver.x", Double.valueOf(spawn.getX()));
					Main.configs.getConfig().set("spawndoserver.y", Double.valueOf(spawn.getY()));
					Main.configs.getConfig().set("spawndoserver.z", Double.valueOf(spawn.getZ()));
					Main.configs.saveConfig();
					return true;
				}
			}
			if (args[0].equalsIgnoreCase("sinal")) {
				if (!pSinal.contains(p)) {
					pSinal.add(p);
				}
				p.sendMessage("\n§aClique com o direito no bloco em que deseja usar como sinaleira.");
				return true;
			}
			if (args[0].equalsIgnoreCase("lista")) {
				p.sendMessage("Lista: " + pSinal);
				return true;
			}

			if (args[0].equalsIgnoreCase("iniciar")) {
				World w = Bukkit.getServer().getWorld(Main.configs.getConfig().getString("camarote.world"));

				double x = Main.configs.getConfig().getLong("camarote.x");
				double y = Main.configs.getConfig().getLong("camarote.y");
				double z = Main.configs.getConfig().getLong("camarote.z");

				if (w != null) {
					Location l = new Location(w, x, y, z);
					camarote = l;
				} else {
					Bukkit.getConsoleSender().sendMessage("§4Mundo do camarote §6null");
				}

				w = Bukkit.getServer().getWorld(Main.configs.getConfig().getString("inicio.world"));
				x = Main.configs.getConfig().getDouble("inicio.x");
				y = Main.configs.getConfig().getDouble("inicio.y");
				z = Main.configs.getConfig().getDouble("inicio.z");

				if (w != null) {
					Location l = new Location(w, x, y, z);
					inicio = l;
				} else {
					Bukkit.getConsoleSender().sendMessage("§4Mundo do inicio §6null");
				}

				w = Bukkit.getServer().getWorld(Main.configs.getConfig().getString("spawndoserver.world"));
				x = Main.configs.getConfig().getDouble("spawndoserver.x");
				y = Main.configs.getConfig().getDouble("spawndoserver.y");
				z = Main.configs.getConfig().getDouble("spawndoserver.z");

				if (w != null) {
					Location l = new Location(w, x, y, z);
					spawn = l;
				} else {
					Bukkit.getConsoleSender().sendMessage("§4Mundo do spawn §6null");
				}

				int totalBlocos = Main.configs.getConfig().getInt("sinaleira.numBlocos");
				numSin = Main.configs.getConfig().getInt("sinaleira.numBlocos");
				for (int i = 0; i <= totalBlocos; i++) {
					x = Main.configs.getConfig().getInt("sinaleira" + i + ".x");
					y = Main.configs.getConfig().getLong("sinaleira" + i + ".y");
					z = Main.configs.getConfig().getLong("sinaleira" + i + ".z");
					Location lb = new Location(w, x, y, z);
					blocosSinal.put(Integer.valueOf(i), lb);
				}

				itID = Main.configs.getConfig().getItemStack("blocoChegada");
				Eventos.premio = Main.cf.getItemStack("Premio");

				if (camarote == null) {
					p.sendMessage("§4[Sinaleira] §cNão foi possível iniciar sinaleira, camarote não setado.");
					return true;
				}
				if (inicio == null) {
					p.sendMessage("§4[Sinaleira] §cNão foi possível iniciar sinaleira, inicio não setado.");
					return true;
				}
				if (itID == null) {
					p.sendMessage("§4[Sinaleira] §cNão foi possível iniciar sinaleira, bloco de chegada não setado.");
					return true;
				}
				ocurr = true;
				comecarSinaleira();
				Eventos.acabou = false;
				return true;
			}
			p.sendMessage("§4[Sinaleira]§c Comando não existe, /sinaleira help para ajuda sobre comandos.");
			return true;
		}

		return false;
	}

	public long tempo = 10L;
	public int intervalo = 2;
	public int tier = 0;
	public static int i = 3;

	public static void comecarSinaleira() {
		(new Thread() {
			public void run() {
				try {
					SinaleiraMsg.sinalStart();
				} catch (InterruptedException e) {
					System.out.println("\nErro no thread...\n!\n!\n!");
				}
			}
		}).start();
	}

	public static Map<Integer, Location> getBlocosSinal() {
		return blocosSinal;
	}

	public static void setBlocosSinal(int k, Location l) {
		blocosSinal.put(Integer.valueOf(k), l);
	}

	public static ArrayList<Player> getpSinal() {
		return pSinal;
	}

	public static void delpSinal(int i) {
		pSinal.remove(i);
	}

	public void setpSinal(ArrayList<Player> pSinal) {
		Comandos.pSinal = pSinal;
	}

	public static Map<Integer, Player> getParticipantes() {
		return participantes;
	}

	public static void putParticipantes(int k, Player p) {
		participantes.put(Integer.valueOf(k), p);
	}

	public static void setParticipantes(Map<Integer, Player> m) {
		participantes = m;
	}

	public static void clearSinalP() {
		pSinal.clear();
	}
}
