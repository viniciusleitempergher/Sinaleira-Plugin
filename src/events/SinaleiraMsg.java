package events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class SinaleiraMsg {
	public static double tempoInicial = Integer.parseInt(Main.cf.getString("TempoParaEntrar"));
	public static boolean podeCorrer = true;
	public static Map<Integer, Player> participantesI = new HashMap<>();
	
	public static void sinalStart() throws InterruptedException {
		Comandos.ocurr = true;
		for (double tempo = tempoInicial; tempo > 0.0D; tempo -= tempoInicial * 0.25D) {
			List<String> mensagem = new ArrayList<>();
			mensagem = Main.cf.getStringList("Iniciando");

			for (int i = 0; i < mensagem.size(); i++) {
				String s = mensagem.get(i);
				s = s.replaceAll("@tempo", (new StringBuilder(String.valueOf(tempo))).toString());
				s = s.replaceAll("@players",
						(new StringBuilder(String.valueOf(Comandos.participantes.size()))).toString());
				Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', s));
			}

			Thread.sleep((long) (tempoInicial * 0.25D * 1000.0D));
		}
		instrucoes();
	}

	public static double instructions = Integer.parseInt(Main.cf.getString("TempoInstrucoes"));

	public static void instrucoes() throws InterruptedException {
		Comandos.ocurr = false;
		if (Comandos.numPar == 0) {
			List<String> mensagem = new ArrayList<>();
			mensagem = Main.cf.getStringList("Cancelado");
			for (int f = 0; f < mensagem.size(); f++) {
				String s = mensagem.get(f);
				Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', s));
			}
			return;
		}
		for (int i = 1; i <= Comandos.getParticipantes().size(); i++) {
			Player p = Comandos.getParticipantes().get(Integer.valueOf(i));
			List<String> msg = new ArrayList<>();
			msg = Main.cf.getStringList("Instrucoes");
			int f;
			for (f = 0; f < msg.size(); f++) {
				String s = msg.get(f);
				s = s.replaceAll("@tempo", (new StringBuilder(String.valueOf(instructions))).toString());
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
			}

			msg = Main.cf.getStringList("EventoFechado");
			podeCorrer = true;

			for (f = 0; f < msg.size(); f++) {
				String s = msg.get(f);
				Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', s));
			}
		}
		Thread.sleep((long) (instructions * 1000.0D));
		for (int a = 1; a <= Comandos.participantes.size(); a++) {
			((Player) Comandos.getParticipantes().get(Integer.valueOf(a))).teleport(Comandos.inicio);
		}
		sinalVerde();
	}

	public static double tempoCorrendo = Integer.parseInt(Main.cf.getString("TempoCorrendo"));

	public static void sinalVerde() throws InterruptedException {
		if (Eventos.acabou) {
			return;
		}

		for (int i = 1; i <= Comandos.participantes.size(); i++) {
			participantesI.put(Integer.valueOf(i), Comandos.participantes.get(Integer.valueOf(i)));
		}

		List<String> msg = new ArrayList<>();
		msg = Main.cf.getStringList("Verde");
		for (int f = 0; f < msg.size(); f++) {
			String s = msg.get(f);
			for (int w = 1; w <= Comandos.participantes.size(); w++) {
				((Player) Comandos.participantes.get(Integer.valueOf(w))).sendMessage(ChatColor.translateAlternateColorCodes('&', s));
			}
		}

		for (int j = 0; j < Comandos.blocosSinal.size(); j++) {
			((Location) Comandos.blocosSinal.get(Integer.valueOf(j))).getBlock().setType(Material.EMERALD_BLOCK);
		}

		for (double tempo = tempoCorrendo; tempo > 0.0D; tempo -= tempoCorrendo * 0.25D) {
			if (Eventos.acabou) {
				return;
			}

			msg = Main.cf.getStringList("Correndo");
			int k;
			for (k = 0; k < msg.size(); k++) {
				String s = msg.get(k);
				s = s.replaceAll("@tempo", (new StringBuilder(String.valueOf(tempo))).toString());
				for (int w = 1; w <= Comandos.participantes.size(); w++) {
					((Player) Comandos.participantes.get(Integer.valueOf(w))).sendMessage(ChatColor.translateAlternateColorCodes('&', s));
				}
			}
			if (tempo <= 3.0D) {

				msg = Main.cf.getStringList("Amarelo");
				for (k = 0; k < msg.size(); k++) {
					String s = msg.get(k);
					for (int w = 1; w <= Comandos.participantes.size(); w++) {
						((Player) Comandos.participantes.get(Integer.valueOf(w))).sendMessage(ChatColor.translateAlternateColorCodes('&', s));
					}
				}
				for (int m = 0; m < Comandos.blocosSinal.size(); m++) {
					((Location) Comandos.blocosSinal.get(Integer.valueOf(m))).getBlock().setType(Material.GOLD_BLOCK);
				}
			}
			Thread.sleep((long) (tempoCorrendo * 0.25D * 1000.0D));
		}
		stopCorrer();
	}

	public static double tempoParado = Integer.parseInt(Main.cf.getString("TempoParado"));

	public static void stopCorrer() throws InterruptedException {
		if (Eventos.acabou) {
			return;
		}
		List<String> mensagem = new ArrayList<>();
		mensagem = Main.cf.getStringList("Vermelho");
		for (int f = 0; f < mensagem.size(); f++) {
			String s = mensagem.get(f);
			for (int w = 1; w <= Comandos.participantes.size(); w++) {
				((Player) Comandos.participantes.get(Integer.valueOf(w))).sendMessage(ChatColor.translateAlternateColorCodes('&', s));
			}
		}
		podeCorrer = false;
		for (int i = 0; i < Comandos.blocosSinal.size(); i++) {
			((Location) Comandos.blocosSinal.get(Integer.valueOf(i))).getBlock().setType(Material.REDSTONE_BLOCK);
		}

		for (double tempo = tempoParado; tempo > 0.0D; tempo -= tempoParado * 0.25D) {
			if (Eventos.acabou) {
				return;
			}

			mensagem = Main.cf.getStringList("Parado");
			for (int j = 0; j < mensagem.size(); j++) {
				String s = mensagem.get(j);
				s = s.replaceAll("@tempo", (new StringBuilder(String.valueOf(tempo))).toString());
				for (int w = 1; w <= Comandos.participantes.size(); w++) {
					((Player) Comandos.participantes.get(Integer.valueOf(w))).sendMessage(ChatColor.translateAlternateColorCodes('&', s));
				}
			}
			Thread.sleep((long) (tempoParado * 0.25D * 1000.0D));
		}
		podeCorrer = true;
		sinalVerde();
	}
}
