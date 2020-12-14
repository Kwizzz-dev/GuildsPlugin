package me.straggly.guilds.commands.sub_commands;

import me.straggly.guilds.Guilds;
import me.straggly.guilds.Messages;
import me.straggly.guilds.commands.sub_commands.misc.GuildPollInventory;
import me.straggly.guilds.objects.Guild;
import me.straggly.guilds.objects.GuildAction;
import org.bukkit.entity.Player;

public class ViewGuildPoll
{
    public void poll(Player player){
        Guild guild = null;

        if (Guilds.getProfiles().containsKey(player.getUniqueId())){
            guild = Guilds.getProfiles().get(player.getUniqueId());
        }

        if (guild == null){
            player.sendMessage(Messages.NOT_IN_GUILD.toString());
            return;
        }

        if (!guild.getMembers().get(player.getUniqueId()).getInheritance().contains(GuildAction.VOTE.getRequiredRank())){
            player.sendMessage(Messages.WRONG_RANK.toString());
            return;
        }

        if (guild.getGuildPoll() == null){
            player.sendMessage(Messages.POLL_NOT_RUNNING.toString());
            return;
        }

        player.openInventory(new GuildPollInventory().getInventory(guild));
    }
}
