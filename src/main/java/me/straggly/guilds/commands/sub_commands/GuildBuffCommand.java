package me.straggly.guilds.commands.sub_commands;

import me.straggly.guilds.Guilds;
import me.straggly.guilds.Messages;
import me.straggly.guilds.commands.sub_commands.misc.GuildBuffInventory;
import me.straggly.guilds.objects.Guild;
import me.straggly.guilds.objects.GuildAction;
import org.bukkit.entity.Player;

public class GuildBuffCommand
{
    public void buff(Player player, String[] args){
        Guild guild = null;

        if (Guilds.getProfiles().containsKey(player.getUniqueId())){
            guild = Guilds.getProfiles().get(player.getUniqueId());
        }

        if (guild == null){
            player.sendMessage(Messages.NOT_IN_GUILD.toString());
            return;
        }

        if (!guild.getMembers().get(player.getUniqueId()).getInheritance().contains(GuildAction.VIEW_BUFFS.getRequiredRank())){
            player.sendMessage(Messages.WRONG_RANK.toString());
            return;
        }

        player.openInventory(new GuildBuffInventory().getInventory(guild, player));
    }
}
