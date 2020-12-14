package me.straggly.guilds.commands.sub_commands;

import me.straggly.guilds.Guilds;
import me.straggly.guilds.Messages;
import me.straggly.guilds.commands.sub_commands.misc.LeaderboardInventory;
import me.straggly.guilds.objects.Guild;
import org.bukkit.entity.Player;

public class LeaderboardCommand
{
    public void leaderboard(Player player, String[] args){
        player.openInventory(new LeaderboardInventory().getMenuInventory());
    }
}
