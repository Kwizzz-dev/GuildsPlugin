package me.straggly.guilds.events;

import me.straggly.guilds.API;
import me.straggly.guilds.Guilds;
import me.straggly.guilds.Messages;
import me.straggly.guilds.objects.Guild;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;

public class ConnectionEvents implements Listener
{
    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();

        Guild guild = API.getGuildFromPlayer(player);

        if (guild != null){
            for (PotionEffect effect : player.getActivePotionEffects()){
                player.removePotionEffect(effect.getType());
            }

            if (guild.getMembers().get(player.getUniqueId()).getRanking() < 6){
                guild.applyBuff(player);
            }

            Guilds.getProfiles().put(player.getUniqueId(), guild);

            if (guild.getGuildPoll() != null){
                player.sendMessage(Messages.POLL_REMINDER.toString());
            }

            if (guild.getGuildWar() != null){
                player.sendMessage(Messages.WAR_STARTED.toString().replaceAll("<guild>",
                        guild.getGuildWar().getOpponent().getName()));
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();

        Guilds.getProfiles().remove(player.getUniqueId());

        for (PotionEffect effect : player.getActivePotionEffects()){
            player.removePotionEffect(effect.getType());
        }
    }
}
