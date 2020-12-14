package me.straggly.guilds.commands.sub_commands;

import me.straggly.guilds.Guilds;
import me.straggly.guilds.Messages;
import me.straggly.guilds.objects.Guild;
import me.straggly.guilds.objects.GuildAction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class UnmutePlayer
{
    public void unmute(Player player, String[] args){
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

        if (!guild.getMembers().get(player.getUniqueId()).getInheritance().contains(GuildAction.MUTE.getRequiredRank())){
            player.sendMessage(Messages.WRONG_RANK.toString());
            return;
        }

        Guild targetGuild = null;
        if (Guilds.getProfiles().containsKey(target.getUniqueId())){
            targetGuild = Guilds.getProfiles().get(target.getUniqueId());
        }

        if (targetGuild == null || !targetGuild.equals(guild)){
            player.sendMessage(Messages.NOT_IN_YOUR_GUILD.toString());
            return;
        }

        if (guild.getMembers().get(target.getUniqueId()).getRanking() <= guild.getMembers().get(player.getUniqueId()).getRanking()){
            player.sendMessage(Messages.TARGET_HIGHER_RANK.toString());
            return;
        }

        if (!guild.getMuteList().contains(target.getUniqueId())){
            player.sendMessage(Messages.PLAYER_NOT_MUTED.toString());
            return;
        }

        guild.getMuteList().add(target.getUniqueId());
        guild.save();

        guild.broadcast(Messages.PLAYER_UNMUTED.toString().replaceAll("<player>", target.getName()));
        target.sendMessage(Messages.YOU_HAVE_BEEN_UNMUTED.toString());
    }
}
