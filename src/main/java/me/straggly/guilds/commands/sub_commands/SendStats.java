package me.straggly.guilds.commands.sub_commands;

import me.straggly.guilds.API;
import me.straggly.guilds.objects.Guild;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class SendStats
{
    public void stats(CommandSender sender, String[] args, Guild guild){
        sender.sendMessage(API.colorize("&f&l&m-------&e Guild Statistics &f&l&m-------"));
        sender.sendMessage(API.colorize("&eGuild Name: &f" + guild.getName()));
        sender.sendMessage(API.colorize("&e "));
        sender.sendMessage(API.colorize("&e&lStatistics"));
        sender.sendMessage(API.colorize("&eTotal Kills: &f" + guild.getKills()));
        sender.sendMessage(API.colorize("&eTotal War Wins: &f" + guild.getWarWins()));
        sender.sendMessage(API.colorize("&eMonthly Kills: &f" + guild.getMonthKills()));
        sender.sendMessage(API.colorize("&eMonthly War Wins: &f" + guild.getMonthlyWarWins()));
    }
}
