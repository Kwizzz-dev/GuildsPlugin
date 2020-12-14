package me.straggly.guilds.commands.sub_commands;

import me.straggly.guilds.Guilds;
import me.straggly.guilds.Messages;
import me.straggly.guilds.objects.Guild;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class QuitGuild
{
    public void quit(Player player, String[] args){
        Guild guild = null;

        if (Guilds.getProfiles().containsKey(player.getUniqueId())){
            guild = Guilds.getProfiles().get(player.getUniqueId());
        }

        if (guild == null){
            player.sendMessage(Messages.NOT_IN_GUILD.toString());
            return;
        }

        if (guild.getLeader().equals(player.getUniqueId())){
            player.sendMessage(Messages.DISBAND_TO_QUIT.toString());
            return;
        }

        guild.getMembers().remove(player.getUniqueId());
        guild.save();

        Guilds.getProfiles().remove(player.getUniqueId());

        for (PotionEffect effect : player.getActivePotionEffects()){
            player.removePotionEffect(effect.getType());
        }

        player.sendMessage(Messages.QUIT_GUILD.toString());
        guild.broadcast(Messages.PLAYER_LEFT.toString().replaceAll("<player>", player.getName()));
    }
}
