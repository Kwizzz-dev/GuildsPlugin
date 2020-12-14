package me.straggly.guilds.commands.sub_commands;

import me.straggly.guilds.API;
import me.straggly.guilds.Guilds;
import me.straggly.guilds.Messages;
import me.straggly.guilds.objects.Guild;
import me.straggly.guilds.objects.GuildRank;
import org.bukkit.entity.Player;

import java.util.List;

public class JoinGuild
{
    public void join(Player player, String[] args){
        String guildName = args[1];

        if (API.getGuildForName(guildName) == null){
            player.sendMessage(Messages.GUILD_DOESNT_EXIST.toString());
            return;
        }

        if (!InviteGuild.getQueue().containsKey(player.getUniqueId())){
            player.sendMessage(Messages.NO_PENDING_INVITES.toString());
            return;
        }

        Guild guild = API.getGuildForName(guildName);
        Guild currentGuild = API.getGuildFromPlayer(player);
        List<Guild> invites = InviteGuild.getQueue().get(player.getUniqueId());

        if (!invites.contains(guild)){
            player.sendMessage(Messages.NOT_INVITED.toString());
            return;
        }

        if (currentGuild != null){
            new QuitGuild().quit(player, args);
        }

        player.sendMessage(Messages.JOINED_GUILD.toString());
        guild.broadcast(Messages.PLAYER_JOINED.toString().replaceAll("<player>", player.getName()));

        guild.addPlayer(player.getUniqueId());
        guild.getForeigners().put(player.getUniqueId(), (int) ((System.currentTimeMillis()/1000) + 259200));

        Guilds.getProfiles().put(player.getUniqueId(), guild);
        guild.save();
    }
}
