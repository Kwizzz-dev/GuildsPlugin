package me.straggly.guilds.events;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import me.straggly.guilds.API;
import me.straggly.guilds.Guilds;
import me.straggly.guilds.Messages;
import me.straggly.guilds.data.DataFile;
import me.straggly.guilds.objects.Guild;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class QuestEvent implements Listener {

	public static Map<Guild, HashMap<Material, Integer>> blocksMined = new HashMap<>();
	public static Map<Guild, Integer> kills = new HashMap<>();

	DataFile conf = Guilds.getQuestsFile();
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {

		Player player = e.getPlayer();
		Block b = e.getBlock();

		Guild guild = null;

		if (API.getGuildFromPlayer(player) != null){
			guild = API.getGuildFromPlayer(player);
		}

		if (guild == null) return;
				
		for (Map.Entry<String, Boolean> map : guild.getQuests().entrySet()){
			String s = map.getKey();
			boolean complete = map.getValue();

			if (complete) continue;

			if (conf != null) {
				String[] split = conf.getConfig().getString("quests." + s + ".requirements").split(":");

				if (split[0].equalsIgnoreCase("MINE_BLOCKS")) {
					if (b.getType() == Material.matchMaterial(split[1].toUpperCase())) {
						setBlocksMined(guild, b.getType(), getBlocksMined(guild, b.getType()) + 1);
					}

					if (getBlocksMined(guild, b.getType()) >= conf.getConfig().getInt("quests." + s + ".value")) {
						guild.broadcast(Messages.QUEST_COMPLETE.toString()
								.replaceAll("<quest>", API.formatEnum(s)));

						guild.getQuests().put(s, true);
						guild.setFunds(guild.getFunds() + conf.getConfig().getInt("quests." + s + ".reward"));
					}

					Optional<Material> type = Optional.of(b.getType());

					guild.saveQuestProgress(s, type, getBlocksMined(guild, b.getType()));
					guild.save();
				}
			}
		}
	}
	
	
	@EventHandler
	public void onPlayerKill(PlayerDeathEvent e) {

		if (e.getEntity().getKiller() == null) return;

		Player player = e.getEntity().getKiller();

		Guild guild = null;

		if (API.getGuildFromPlayer(player) != null){
			guild = API.getGuildFromPlayer(player);
		}

		if (guild == null) return;

		Guild opponent = null;

		if (API.getGuildFromPlayer(e.getEntity()) != null){
			opponent = API.getGuildFromPlayer(e.getEntity());
		}

		if (opponent == null) return;

		for (Map.Entry<String, Boolean> map : guild.getQuests().entrySet()){
			String s = map.getKey();
			boolean complete = map.getValue();

			if (complete) continue;

			String split = conf.getConfig().getString("quests." + s + ".requirements");

			if (conf != null) {
				if (split.equalsIgnoreCase("KILL_PLAYERS")) {
					setKills(guild, getKills(guild) + 1);
				}

				if (getKills(guild) >= conf.getConfig().getInt("quests." + s + ".value")) {
					guild.broadcast(Messages.QUEST_COMPLETE.toString()
							.replaceAll("<quest>", API.formatEnum(s)));

					guild.getQuests().put(s, true);
					guild.setFunds(guild.getFunds() + conf.getConfig().getInt("quests." + s + ".reward"));
				}
			}
			guild.saveQuestProgress(s, Optional.empty(), getKills(guild));
			guild.save();
		}
	}
	
	public static Map<Guild, HashMap<Material, Integer>> blocks() {
		return blocksMined;
	}
	
	public static int getBlocksMined(Guild p, Material material) {
		if (blocksMined.containsKey(p)){
			if (blocksMined.get(p).containsKey(material) && blocksMined.get(p).get(material) > 0) {
				return blocksMined.get(p).get(material);
			} else {
				return 0;
			}
		}
		return 0;
	}
	
	public static void setBlocksMined(Guild p, Material material, int i) {
		if (blocksMined.containsKey(p)) {
			if (blocksMined.get(p).containsKey(material)){
				HashMap<Material, Integer> temp = new HashMap<>();
				temp.put(material, i);

				HashMap<Material, Integer> old = new HashMap<>();
				old.put(material, getBlocksMined(p, material));

				blocksMined.replace(p, old, temp);
			}
		}
		else {
			HashMap<Material, Integer> temp = new HashMap<>();
			temp.put(material, i);

			blocksMined.put(p, temp);
		}
	}


	public static Map<Guild, Integer> kills() {
		return kills;
	}
	
	public static int getKills(Guild p) {
		if (kills.containsKey(p) && kills.get(p) > 0) {
			return kills.get(p);
		} else {
			return 0;
		}
	}
	
	public static void setKills(Guild p, int i) {
		if (kills.containsKey(p)) {
			kills.replace(p, getKills(p), i);
		}
		else {
			kills.put(p, i);
		}
	}
}