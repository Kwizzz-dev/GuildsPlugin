package me.straggly.guilds.commands.sub_commands.misc;

import me.straggly.guilds.Guilds;
import me.straggly.guilds.Messages;
import me.straggly.guilds.objects.Guild;
import me.straggly.guilds.objects.GuildAction;
import me.straggly.guilds.objects.GuildPoll;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class CreateGuildPollListeners implements Listener
{
    public static HashMap<UUID, String> questionQueue = new HashMap<>();
    public static HashMap<UUID, String> optionsQueue = new HashMap<>();

    @EventHandler (priority = EventPriority.LOW)
    public void onChat(AsyncPlayerChatEvent event){
        if (event.isCancelled()) return;

        Player player = event.getPlayer();

        if (!questionQueue.containsKey(player.getUniqueId()) && !optionsQueue.containsKey(player.getUniqueId())) return;

        Guild guild = null;

        if (Guilds.getProfiles().containsKey(player.getUniqueId())){
            guild = Guilds.getProfiles().get(player.getUniqueId());
        }

        if (guild == null){
            player.sendMessage(Messages.NOT_IN_GUILD.toString());
            questionQueue.remove(player.getUniqueId());
            optionsQueue.remove(player.getUniqueId());
            return;
        }

        event.setCancelled(true);

        if (questionQueue.containsKey(player.getUniqueId())){
            StringBuilder builder = new StringBuilder(questionQueue.get(player.getUniqueId()));
            builder.append(event.getMessage()).append(";");

            optionsQueue.put(player.getUniqueId(), builder.toString());
            questionQueue.remove(player.getUniqueId());

            player.sendMessage(Messages.TYPE_OPTIONS.toString());
            return;
        }

        StringBuilder builder = new StringBuilder(optionsQueue.get(player.getUniqueId()));

        if (!event.getMessage().equalsIgnoreCase("END")){
            builder.append(event.getMessage()).append(";");
            optionsQueue.put(player.getUniqueId(), builder.toString());
        }

        if (builder.toString().split(";").length == 6 || event.getMessage().equalsIgnoreCase("END")){
            player.sendMessage(Messages.POLL_STARTED.toString());

            optionsQueue.remove(player.getUniqueId());

            guild.getFile().getConfig().set("poll", builder.toString());
            guild.getFile().saveConfig();
            guild.setGuildPoll(new GuildPoll(builder.toString()));
            guild.save();

            guild.broadcast("&f&l&m-------&e Guild Poll &f&l&m-------");
            guild.broadcast("&eStarted by: &f" + Bukkit.getOfflinePlayer(guild.getGuildPoll().getStartedBy()).getName());
            guild.broadcast("&eQuestion: &f" + guild.getGuildPoll().getQuestion());
            guild.broadcast("&e&oType &e&n/guild poll&e to participate!");
        } else {
            player.sendMessage(Messages.CONTINUE_TYPE_OPTION.toString());
        }
    }
}