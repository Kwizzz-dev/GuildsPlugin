package me.straggly.guilds.commands.sub_commands;

import me.straggly.guilds.API;
import me.straggly.guilds.Guilds;
import me.straggly.guilds.objects.Guild;
import me.straggly.guilds.objects.GuildRank;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SendInfo
{
    public void info(CommandSender sender, String[] args, Guild guild){
        sender.sendMessage(API.colorize("&f&l&m-------&e Guild Info &f&l&m-------"));
        sender.sendMessage(API.colorize("&eGuild Name: &f" + guild.getName()));
        sender.sendMessage(API.colorize("&eLeader: &f" + Bukkit.getOfflinePlayer(guild.getLeader()).getName()));
        sender.sendMessage(API.colorize("&eGuild Level: &f" + guild.getLevel()));
        sender.sendMessage(API.colorize("&eGuild Funds: &f"+ Guilds.getCfg().getString("currency-type") + API.formatNumber(guild.getFunds())));
        sender.sendMessage(API.colorize("&eGuild Members &f[&e" + guild.getMembers().size() + "&f]: " + getGuildMembers(guild)));
    }

    public String getGuildMembers(Guild guild){
        StringBuilder builder = new StringBuilder();

        ArrayList<UUID> list = new ArrayList<>(guild.getMembers().keySet());

        while (!list.isEmpty()){
            for (int i = 1; i < 7; i++){
                List<UUID> remove = new ArrayList<>();
                for (UUID member : list){
                    GuildRank rank = guild.getMembers().get(member);

                    if (rank.getRanking() == i){
                        if (rank.equals(GuildRank.KING)){
                            builder.append(API.colorize("&f&n" + guild.getLeaderTitle() + "&f " +
                                    Bukkit.getOfflinePlayer(member).getName())).append(", ");
                        } else {
                            builder.append(API.colorize("&f&n" + rank.toString() + "&f " +
                                    Bukkit.getOfflinePlayer(member).getName())).append(", ");
                        }
                        remove.add(member);
                    }
                }
                list.removeAll(remove);
            }
        }

        builder.setCharAt(builder.lastIndexOf(", "), '.');

        return builder.toString();
    }
}
