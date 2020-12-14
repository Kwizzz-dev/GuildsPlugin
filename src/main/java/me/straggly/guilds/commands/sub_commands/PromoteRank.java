package me.straggly.guilds.commands.sub_commands;

import me.straggly.guilds.Guilds;
import me.straggly.guilds.Messages;
import me.straggly.guilds.commands.sub_commands.misc.PromoteInventoryListeners;
import me.straggly.guilds.commands.sub_commands.misc.PromoteRankInventory;
import me.straggly.guilds.objects.Guild;
import me.straggly.guilds.objects.GuildAction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PromoteRank
{
    public void promote(Player player, String[] args){
        Guild guild = null;

        if (Guilds.getProfiles().containsKey(player.getUniqueId())){
            guild = Guilds.getProfiles().get(player.getUniqueId());
        }

        if (guild == null){
            player.sendMessage(Messages.NOT_IN_GUILD.toString());
            return;
        }

        if (!guild.getMembers().get(player.getUniqueId()).getInheritance().contains(GuildAction.PROMOTE_TO_BARON.getRequiredRank())){
            player.sendMessage(Messages.WRONG_RANK.toString());
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null){
            player.sendMessage(Messages.PLAYER_OFFLINE.toString());
            return;
        }

        if (!Guilds.getProfiles().containsKey(player.getUniqueId())
        || !Guilds.getProfiles().get(player.getUniqueId()).equals(guild)){
            player.sendMessage(Messages.NOT_IN_YOUR_GUILD.toString());
            return;
        }

        PromoteInventoryListeners.queue.put(player.getUniqueId(), target.getUniqueId());
        player.openInventory(new PromoteRankInventory().getInventory(guild, player, target));
    }
}
