package me.straggly.guilds.commands.sub_commands;

import me.straggly.guilds.API;
import me.straggly.guilds.Messages;
import me.straggly.guilds.commands.sub_commands.misc.GuildWarInventory;
import me.straggly.guilds.objects.Guild;
import me.straggly.guilds.objects.GuildRank;
import me.straggly.guilds.objects.GuildWar;
import org.bukkit.entity.Player;

import java.time.format.DateTimeFormatter;

public class GuildWarCommand
{
    public void war(Player player, String[] args){
        Guild guild = null;

        if (API.getGuildFromPlayer(player) != null){
            guild = API.getGuildFromPlayer(player);
        }

        if (guild == null){
            player.sendMessage(Messages.NOT_IN_GUILD.toString());
            return;
        }

        GuildRank rank = guild.getMembers().get(player.getUniqueId());
        if (rank.getRanking() > 2){
            player.sendMessage(Messages.WRONG_RANK.toString());
            return;
        }

        if (guild.getGuildWar() != null){
            StringBuilder builder = new StringBuilder();

            GuildWar war = guild.getGuildWar().getOpponent().getGuildWar();

            builder.append(API.colorize("&f&l&m-------&e Guild War Info &f&l&m-------")).append("\n");
            builder.append(API.colorize("&fKills: &e" + war.getKills())).append("\n");
            builder.append(API.colorize("&fOpponent: &e" + guild.getGuildWar().getOpponent().getName())).append("\n");
            builder.append(API.colorize("&fEnd Date: &e" + war.getEnd().format(DateTimeFormatter.RFC_1123_DATE_TIME))).append("\n");

            player.sendMessage(builder.toString());

            return;
        }

        player.openInventory(new GuildWarInventory().getInventory(guild));
    }
}
