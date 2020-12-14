package me.straggly.guilds.commands.sub_commands.misc;

import me.straggly.guilds.API;
import me.straggly.guilds.Guilds;
import me.straggly.guilds.Messages;
import me.straggly.guilds.objects.Guild;
import me.straggly.guilds.objects.GuildRank;
import me.straggly.guilds.objects.GuildWar;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

import java.time.LocalDateTime;
import java.util.Arrays;

public class GuildWarInventoryListeners implements Listener
{
    @EventHandler
    public void onMove(InventoryMoveItemEvent event){
        Player player = (Player) event.getSource().getViewers().get(0);

        Guild guild = null;

        if (Guilds.getProfiles().containsKey(player.getUniqueId())){
            guild = Guilds.getProfiles().get(player.getUniqueId());
        }

        if (guild == null) return;

        if (Arrays.equals(event.getDestination().getContents(),
                new GuildWarInventory().getInventory(guild).getContents())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event){
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!event.getView().getTitle().equalsIgnoreCase(API.colorize("&e&lChoose your opponent..."))) return;

        Player player = (Player) event.getWhoClicked();

        Guild guild = null;

        if (Guilds.getProfiles().containsKey(player.getUniqueId())){
            guild = Guilds.getProfiles().get(player.getUniqueId());
        }

        if (guild == null) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!event.getView().getTitle().equalsIgnoreCase(API.colorize("&e&lChoose your opponent..."))) return;
        if (event.getCurrentItem() == null) return;

        Player player = (Player) event.getWhoClicked();

        event.setCancelled(true);
        player.closeInventory();

        Guild guild = null;

        if (Guilds.getProfiles().containsKey(player.getUniqueId())) {
            guild = Guilds.getProfiles().get(player.getUniqueId());
        }

        if (guild == null) {
            player.sendMessage(Messages.NOT_IN_GUILD.toString());
            return;
        }

        GuildRank rank = guild.getMembers().get(player.getUniqueId());
        if (rank.getRanking() > 2){
            player.sendMessage(Messages.WRONG_RANK.toString());
            return;
        }

        Guild opponent = API.getGuildForName(API.guildNameFromStack(event.getCurrentItem()));

        if (opponent == null){
            return;
        }

        if (opponent.getGuildWar() != null){
            player.sendMessage(Messages.OPPONENT_AT_WAR.toString());
            return;
        }

        if (guild.getGuildWar() != null){
            player.sendMessage(Messages.GUILD_AT_WAR.toString());
            return;
        }

        guild.setGuildWar(new GuildWar(opponent, guild, 0, true, LocalDateTime.now().plusMinutes(1)));
        opponent.setGuildWar(new GuildWar(guild, opponent, 0, true, LocalDateTime.now().plusMinutes(1)));

        guild.playSound(Sound.ENTITY_ENDER_DRAGON_HURT);
        opponent.playSound(Sound.ENTITY_ENDER_DRAGON_HURT);

        guild.save();
        opponent.save();
    }
}
