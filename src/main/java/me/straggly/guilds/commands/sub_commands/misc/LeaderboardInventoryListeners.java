package me.straggly.guilds.commands.sub_commands.misc;

import me.straggly.guilds.API;
import me.straggly.guilds.Guilds;
import me.straggly.guilds.objects.Guild;
import me.straggly.guilds.objects.Leaderboard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class LeaderboardInventoryListeners implements Listener
{
    @EventHandler
    public void onMove(InventoryMoveItemEvent event){
        if (Arrays.equals(event.getDestination().getContents(),
                new LeaderboardInventory().getMenuInventory().getContents())){
            event.setCancelled(true);
        }

        for (Leaderboard leaderboard : Leaderboard.values()){
            if (Arrays.equals(event.getDestination().getContents(),
                    new LeaderboardInventory().getLeaderboardInventory(leaderboard).getContents())){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event){
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!event.getView().getTitle().equalsIgnoreCase(API.colorize("&e&lPick a category..."))
        && !event.getView().getTitle().startsWith(API.colorize("&e&lMonthly"))
        && !event.getView().getTitle().startsWith(API.colorize("&e&lTotal"))) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!event.getView().getTitle().equalsIgnoreCase(API.colorize("&e&lPick a category..."))) return;
        if (event.getCurrentItem() == null) return;

        event.setCancelled(true);

        ItemStack stack = event.getCurrentItem();

        Player player = (Player) event.getWhoClicked();

        if (!event.getCurrentItem().hasItemMeta()) return;

        Leaderboard leaderboard = Leaderboard.valueOf(API.enumValueFromStack(stack));

        player.closeInventory();
        player.openInventory(new LeaderboardInventory().getLeaderboardInventory(leaderboard));
    }

    @EventHandler
    public void onClick2(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!event.getView().getTitle().startsWith(API.colorize("&e&lMonthly"))
        && !event.getView().getTitle().startsWith(API.colorize("&e&lTotal"))) return;
        if (event.getCurrentItem() == null) return;

        event.setCancelled(true);
    }
}
