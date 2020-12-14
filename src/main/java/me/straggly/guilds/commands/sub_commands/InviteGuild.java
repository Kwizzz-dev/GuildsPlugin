package me.straggly.guilds.commands.sub_commands;

import me.straggly.guilds.Guilds;
import me.straggly.guilds.Messages;
import me.straggly.guilds.objects.Guild;
import me.straggly.guilds.objects.GuildAction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class InviteGuild
{
    private static HashMap<UUID, List<Guild>> queue = new HashMap<>();
    public static HashMap<UUID, List<Guild>> getQueue() {
        return queue;
    }

    public void invite(Player player, String[] args){
        Player target = Bukkit.getPlayer(args[1]);

        if (target == null){
            player.sendMessage(Messages.PLAYER_OFFLINE.toString());
            return;
        }

        Guild guild = null;

        if (Guilds.getProfiles().containsKey(player.getUniqueId())){
            guild = Guilds.getProfiles().get(player.getUniqueId());
        }

        if (guild == null){
            player.sendMessage(Messages.NOT_IN_GUILD.toString());
            return;
        }

        if (!guild.getMembers().get(player.getUniqueId()).getInheritance().contains(GuildAction.INVITE.getRequiredRank())){
            player.sendMessage(Messages.WRONG_RANK.toString());
            return;
        }

        Guild targetGuild = null;
        if (Guilds.getProfiles().containsKey(target.getUniqueId())){
            targetGuild = Guilds.getProfiles().get(target.getUniqueId());
        }

        if (guild.getBanList().contains(target.getUniqueId())){
            player.sendMessage(Messages.PLAYER_IS_BANNED.toString());
            return;
        }

        if (targetGuild != null &&
                targetGuild.getName().equals(guild.getName())){
            player.sendMessage(Messages.IN_THIS_GUILD.toString());
            return;
        }

        List<Guild> queuePush;
        if (queue.containsKey(target.getUniqueId())){
            queuePush = queue.get(target.getUniqueId());
        } else {
            queuePush = new ArrayList<>();
        }
        queuePush.add(guild);

        queue.put(target.getUniqueId(), queuePush);

        player.sendMessage(Messages.PLAYER_INVITED.toString().replaceAll("<player>", target.getName()));
        target.sendMessage(Messages.TARGET_INVITED.toString().replaceAll("<guild>", guild.getName()));
    }
}
