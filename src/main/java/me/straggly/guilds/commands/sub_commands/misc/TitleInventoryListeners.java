package me.straggly.guilds.commands.sub_commands.misc;

import me.straggly.guilds.API;
import me.straggly.guilds.Guilds;
import me.straggly.guilds.Messages;
import me.straggly.guilds.objects.Guild;
import me.straggly.guilds.objects.GuildAction;
import me.straggly.guilds.objects.GuildAward;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class TitleInventoryListeners implements Listener
{
    @EventHandler
    public void onMove(InventoryMoveItemEvent event){
        Player player = (Player) event.getSource().getViewers().get(0);

        Guild guild = null;

        if (Guilds.getProfiles().containsKey(player.getUniqueId())){
            guild = Guilds.getProfiles().get(player.getUniqueId());
        }

        if (guild == null) return;

        if (Arrays.equals(event.getDestination().getContents(), new TitleInventory().getInventory(guild).getContents())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event){
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!event.getView().getTitle().equalsIgnoreCase(API.colorize("&eSelect a title..."))) return;

        Player player = (Player) event.getWhoClicked();

        Guild guild = null;

        if (Guilds.getProfiles().containsKey(player.getUniqueId())){
            guild = Guilds.getProfiles().get(player.getUniqueId());
        }

        if (guild == null) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event){
        if (event.getClickedInventory() == null) return;
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!event.getView().getTitle().equalsIgnoreCase(API.colorize("&eSelect a title..."))) return;
        if (event.getCurrentItem() == null) return;

        Player player = (Player) event.getWhoClicked();

        Guild guild = null;

        if (Guilds.getProfiles().containsKey(player.getUniqueId())){
            guild = Guilds.getProfiles().get(player.getUniqueId());
        }

        if (guild == null){
            player.sendMessage(Messages.NOT_IN_GUILD.toString());
            return;
        }

        if (!guild.getMembers().get(player.getUniqueId()).getInheritance().contains(GuildAction.CHANGE_LEADER_TITLE.getRequiredRank())){
            player.sendMessage(Messages.WRONG_RANK.toString());
            return;
        }

        event.setCancelled(true);
        player.closeInventory();

        ItemStack stack = event.getCurrentItem();

        if (!stack.hasItemMeta()) return;

        String title = stack.getItemMeta().getDisplayName();
        title = title.replaceAll(" Title", "");
        title = ChatColor.stripColor(title);

        if (title.equalsIgnoreCase("No Awards")) return;

        GuildAward award = GuildAward.getAwardFromName(title);

        if (award == null) return;

        guild.setLeaderTitle(award.getLeaderTitle());
        guild.save();

        guild.broadcast(Messages.GUILD_AWARD_UPDATED.toString().replaceAll("<title>", title));
    }
}
