package events;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Eventos implements Listener {
	public static boolean acabou = false;
	public static ItemStack premio;

	@EventHandler
	public void sinal(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		Map<Integer, Player> participantes = SinaleiraMsg.participantesI;

		if (participantes.size() == 0 || !participantes.containsValue(player)) {
			return;
		}
		Location pl = player.getLocation();
		pl.setY(pl.getY() - 1.0D);
		if (participantes.size() == 1) {
			for (int i = 1; i <= participantes.size(); i++) {
				((Player) participantes.get(Integer.valueOf(i))).teleport(Comandos.spawn);
			}
			Player winner = participantes.get(getKey(participantes, player));
			SinaleiraMsg.participantesI.clear();
			Comandos.participantes.clear();
			Comandos.numPar = 0;
			List<String> mensagem = new ArrayList<>();
			mensagem = Main.cf.getStringList("EventoEncerrado");

			for (int j = 0; j < mensagem.size(); j++) {
				String s = mensagem.get(j);
				s = s.replace('&', '§');
				s = s.replaceAll("@ganhador", (new StringBuilder(String.valueOf(player.getName()))).toString());
				Bukkit.broadcastMessage(s);
			}
			acabou = true;
			darPremio(player);
			player.teleport(Comandos.spawn);
			return;
		}
		if (!SinaleiraMsg.podeCorrer && participantes.containsValue(player)) {
			player.sendMessage("§4[Sinaleira]§c Você perdeu ;c");
			Comandos.numPar--;
			participantes.remove(getKey(participantes, player));
			player.setCanPickupItems(true);
			player.teleport(Comandos.spawn);
			SinaleiraMsg.participantesI = participantes;
		}
	}

	@EventHandler
	public void chegou(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		Map<Integer, Player> participantes = SinaleiraMsg.participantesI;
		if (!participantes.containsValue(player)) {
			return;
		}
		Location playerLocation = player.getLocation();
		playerLocation.setY(playerLocation.getY() - 2.0D);
		Block pBlock = playerLocation.getBlock();
		if (pBlock.getTypeId() == Comandos.itID.getTypeId()) {
			for (int i = 1; i <= participantes.size(); i++) {
				((Player) participantes.get(Integer.valueOf(i))).teleport(Comandos.spawn);
			}
			SinaleiraMsg.participantesI.clear();
			Comandos.participantes.clear();
			Comandos.numPar = 0;
			player.setCanPickupItems(true);
			List<String> mensagem = new ArrayList<>();
			mensagem = Main.cf.getStringList("EventoEncerrado");

			for (int j = 0; j < mensagem.size(); j++) {
				String s = mensagem.get(j);
				s = s.replace('&', '§');
				s = s.replaceAll("@ganhador", (new StringBuilder(String.valueOf(player.getName()))).toString());
				Bukkit.broadcastMessage(s);
			}
			player.teleport(Comandos.spawn);
			darPremio(player);
			acabou = true;
		}
	}

	@EventHandler
	public void ouvir(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		for (int i = 0; i < Comandos.getpSinal().size(); i++) {

			if (player == Comandos.pSinal.get(i)) {
				Action action = event.getAction();
				if (action == Action.RIGHT_CLICK_BLOCK) {
					Location blocoClicado = player.getTargetBlock((HashSet<Byte>) null, 100).getLocation();
					Comandos.setBlocosSinal(Comandos.numSin, blocoClicado);
					Main.configs.getConfig().set("sinaleira" + Comandos.numSin + ".world",
							blocoClicado.getWorld().getName());
					Main.configs.getConfig().set("sinaleira" + Comandos.numSin + ".x",
							Double.valueOf(blocoClicado.getX()));
					Main.configs.getConfig().set("sinaleira" + Comandos.numSin + ".y",
							Double.valueOf(blocoClicado.getY()));
					Main.configs.getConfig().set("sinaleira" + Comandos.numSin + ".z",
							Double.valueOf(blocoClicado.getZ()));
					Main.configs.getConfig().set("sinaleira.numBlocos", Integer.valueOf(Comandos.numSin));
					Main.configs.saveConfig();
					Comandos.delpSinal(i);
					player.sendMessage("§a[§f!§a] Sinaleira criada.");
					Comandos.numSin++;
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPreprocess(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		String command = event.getMessage();

		if (!command.trim().equalsIgnoreCase("/sinaleira sair") && Comandos.getParticipantes().containsValue(player)) {
			event.setCancelled(true);
			player.sendMessage(
					"§4[Sinaleira] §cComandos bloqueados durante o evento, para sair use: §f/Sinaleira Sair.");
		}
	}

	public static void resetConfigSinal() {
		int totalBlocos = Main.configs.getConfig().getInt("sinaleira.numBlocos");

		for (int i = 0; i <= totalBlocos; i++) {
			Main.configs.getConfig().set("sinaleira" + i + ".x", Integer.valueOf(0));
			Main.configs.getConfig().set("sinaleira" + i + ".y", Integer.valueOf(0));
			Main.configs.getConfig().set("sinaleira" + i + ".z", Integer.valueOf(0));
		}
		Main.configs.getConfig().set("sinaleira.numBlocos", Integer.valueOf(0));
		Main.configs.saveConfig();
		Comandos.numSin = 0;
	}

	public static void darPremio(Player p) {
		p.setCanPickupItems(true);
		int premioID = Main.configs.getConfig().getInt("premio");
		int quantPremio = Main.configs.getConfig().getInt("quantidadedopremio");
		premio = new ItemStack(premioID, quantPremio);
		PlayerInventory playerInventory = p.getInventory();

		playerInventory.addItem(new ItemStack[] { premio });
	}

	public static <K, V> K getKey(Map<K, V> map, V value) {
		for (Map.Entry<K, V> entry : map.entrySet()) {
			if (entry.getValue().equals(value)) {
				return entry.getKey();
			}
		}
		return null;
	}
}
