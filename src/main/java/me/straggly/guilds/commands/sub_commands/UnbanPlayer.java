package me.straggly.guilds.commands.sub_commands;

import me.straggly.guilds.Guilds;
import me.straggly.guilds.Messages;
import me.straggly.guilds.objects.Guild;
import me.straggly.guilds.objects.GuildAction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class UnbanPlayer
{
    public void unban(Player player, String[] args){
        Player target = Bukkit.getPlayer(args[1]);

        if (target == null){
            player.sendMessage(Messages.PLAYER_OFFLINE.toString());
            return;
        }

        Guild guild = null;

        if (Guilds.getProfiles().containsKey(player.getUniqueId())){
            guild = Guilds.getProfiles().get(player.getUniqueId());
        }

        if (guild == null){
            player.sendMessage(Messages.NOT_IN_GUILD.toString());
            return;
        }

        if (!guild.getMembers().get(player.getUniqueId()).getInheritance().contains(GuildAction.BAN.getRequiredRank())){
            player.sendMessage(Messages.WRONG_RANK.toString());
            return;
        }

        if (!guild.getBanList().contains(target.getUniqueId())){
            player.sendMessage(Messages.PLAYER_NOT_BANNED.toString());
            return;
        }

        guild.getBanList().remove(target.getUniqueId());
        guild.save();

        guild.broadcast(Messages.PLAYER_UNBANNED.toString().replaceAll("<player>", target.getName()));
        target.sendMessage(Messages.YOU_HAVE_BEEN_UNBANNED.toString().replaceAll("<guild>", guild.getName()));
    }
}
