package me.straggly.guilds.commands.sub_commands;

import me.straggly.guilds.API;
import me.straggly.guilds.Guilds;
import me.straggly.guilds.Messages;
import me.straggly.guilds.commands.sub_commands.misc.CreateGuildPollListeners;
import me.straggly.guilds.objects.Guild;
import me.straggly.guilds.objects.GuildAction;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;

public class CreateGuildPoll
{
    public void poll(Player player, String[] args){
        Guild guild = null;

        if (Guilds.getProfiles().containsKey(player.getUniqueId())){
            guild = Guilds.getProfiles().get(player.getUniqueId());
        }

        if (guild == null){
            player.sendMessage(Messages.NOT_IN_GUILD.toString());
            return;
        }

        if (!guild.getMembers().get(player.getUniqueId()).getInheritance().contains(GuildAction.CREATE_POLL.getRequiredRank())){
            player.sendMessage(Messages.WRONG_RANK.toString());
            return;
        }

        if (guild.getGuildPoll() != null){
            player.sendMessage(Messages.POLL_RUNNING.toString());
            return;
        }

        if (!args[1].toUpperCase().contains("D") && !args[1].toUpperCase().contains("M") && !args[1].toUpperCase().contains("S")){
            player.sendMessage(Messages.WRONG_VALUE.toString());
            player.sendMessage(API.colorize("&c&oTimeframe, use the likes of D, M, S! For example, 7d is 7 days."));
            return;
        }

        LocalDateTime time;
        try {
            time = getTime(args[1]);
        } catch (Exception e){
            player.sendMessage(Messages.WRONG_VALUE.toString());
            player.sendMessage(API.colorize("&c&oTimeframe, use the likes of D, M, S! For example, 7d is 7 days/"));
            return;
        }

        if (time == null){
            player.sendMessage(Messages.WRONG_VALUE.toString());
            player.sendMessage(API.colorize("&c&oTimeframe, use the likes of D, M, S! For example, 7d is 7 days/"));
            return;
        }

        StringBuilder builder = new StringBuilder();
        builder.append(time.toString()).append(";");
        builder.append(player.getUniqueId().toString()).append(";");

        CreateGuildPollListeners.questionQueue.put(player.getUniqueId(), builder.toString());
        player.sendMessage(Messages.TYPE_QUESTION.toString());
    }

    public LocalDateTime getTime(String args){
        StringBuilder val = new StringBuilder();
        String timetype = null;
        for (Character character : args.toCharArray()){
            try {
                val.append(Integer.parseInt(String.valueOf(character)));
            } catch (Exception e){
                timetype = character.toString();
            }
        }

        int value = Integer.parseInt(val.toString());

        try {
            switch (timetype.toUpperCase()){
                case "D":
                    return LocalDateTime.now().plusDays(value);
                case "M":
                    return LocalDateTime.now().plusMinutes(value);
                case "S":
                    return LocalDateTime.now().plusSeconds(value);
                default:
                    return null;
            }
        } catch (Exception e){
            return null;
        }
    }
}
