package me.straggly.guilds.commands.sub_commands;

import me.straggly.guilds.API;
import me.straggly.guilds.Guilds;
import me.straggly.guilds.Messages;
import me.straggly.guilds.objects.Guild;
import me.straggly.guilds.objects.GuildAction;
import me.straggly.guilds.objects.GuildRank;
import org.bukkit.entity.Player;

public class GuildChatCommand
{
    public void chat(Player player, String[] args){
        Guild guild = null;

        if (Guilds.getProfiles().containsKey(player.getUniqueId())){
            guild = Guilds.getProfiles().get(player.getUniqueId());
        }

        if (guild == null){
            player.sendMessage(Messages.NOT_IN_GUILD.toString());
            return;
        }

        if (guild.getMuteList().contains(player.getUniqueId())){
            player.sendMessage(Messages.YOU_ARE_MUTED.toString());
            return;
        }

        StringBuilder message = new StringBuilder();
        for (int i = 1; i < args.length; i++){
            message.append(args[i]).append(" ");
        }

        GuildRank rank = guild.getMembers().get(player.getUniqueId());

        String format = Guilds.getCfg().getString("guild-chat-format");
        format = format.replaceAll("<guild>", guild.getName());
        format = format.replaceAll("<guild-rank>", API.formatEnum(rank.name()));
        format = format.replaceAll("<player>", player.getName());
        format = format.replaceAll("<message>", message.toString());

        guild.broadcast(format);
    }
}
