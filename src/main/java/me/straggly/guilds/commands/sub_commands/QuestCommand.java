package me.straggly.guilds.commands.sub_commands;

import me.straggly.guilds.Guilds;
import me.straggly.guilds.Messages;
import me.straggly.guilds.commands.sub_commands.misc.GuildQuestInventory;
import me.straggly.guilds.objects.Guild;
import org.bukkit.entity.Player;

public class QuestCommand
{
    public void quest(Player player, String[] args){
        Guild guild = null;

        if (Guilds.getProfiles().containsKey(player.getUniqueId())){
            guild = Guilds.getProfiles().get(player.getUniqueId());
        }

        if (guild == null){
            player.sendMessage(Messages.NOT_IN_GUILD.toString());
            return;
        }

        player.openInventory(new GuildQuestInventory().getInventory(guild));
    }
}
