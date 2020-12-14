package me.straggly.guilds.commands.sub_commands.misc;

import me.straggly.guilds.API;
import me.straggly.guilds.Guilds;
import me.straggly.guilds.Messages;
import me.straggly.guilds.objects.Guild;
import me.straggly.guilds.objects.GuildAction;
import me.straggly.guilds.objects.GuildRank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class PromoteInventoryListeners implements Listener
{
    public static HashMap<UUID, UUID> queue = new HashMap<>();

    @EventHandler
    public void onMove(InventoryMoveItemEvent event){
        Player player = (Player) event.getSource().getViewers().get(0);

        Guild guild = null;

        if (!queue.containsKey(player.getUniqueId())) return;

        if (Guilds.getProfiles().containsKey(player.getUniqueId())){
            guild = Guilds.getProfiles().get(player.getUniqueId());
        }

        if (guild == null) return;

        Player target = Bukkit.getPlayer(queue.get(player.getUniqueId()));
        if (Arrays.equals(event.getDestination().getContents(),
                new PromoteRankInventory().getInventory(guild, player, target).getContents())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event){
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!event.getView().getTitle().equalsIgnoreCase(API.colorize("&e&l&nChoose a rank..."))) return;

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
        if (!event.getView().getTitle().equalsIgnoreCase(API.colorize("&e&l&nChoose a rank..."))) return;
        if (event.getCurrentItem() == null) return;

        Player player = (Player) event.getWhoClicked();

        Guild guild = null;

        if (Guilds.getProfiles().containsKey(player.getUniqueId())) {
            guild = Guilds.getProfiles().get(player.getUniqueId());
        }

        if (guild == null) {
            player.sendMessage(Messages.NOT_IN_GUILD.toString());
            return;
        }

        if (!guild.getMembers().get(player.getUniqueId()).getInheritance().contains(GuildAction.PROMOTE_TO_BARON.getRequiredRank())) {
            player.sendMessage(Messages.WRONG_RANK.toString());
            return;
        }

        event.setCancelled(true);
        player.closeInventory();

        Player target = Bukkit.getPlayer(queue.get(player.getUniqueId()));

        if (target == null){
            player.sendMessage(Messages.PLAYER_OFFLINE.toString());
            return;
        }

        ItemStack clicked = event.getCurrentItem();

        if (!clicked.hasItemMeta()) return;

        String name = clicked.getItemMeta().getDisplayName();
        String rankName = ChatColor.stripColor(name).replaceAll("Promote to ", "")
                .replaceAll("Demote to ", "");

        boolean promote = ChatColor.stripColor(name).contains("Promote to");
        GuildRank rank;
        GuildRank playerRank = guild.getMembers().get(player.getUniqueId());

        try {
            rank = GuildRank.valueOf(rankName.toUpperCase());
        } catch (Exception e){
            return;
        }

        boolean permissionGranted;
        switch (rankName.toUpperCase()){
            case "KING":
            case "DUKE":
                permissionGranted = playerRank.getRanking() <= 1;
                break;
            case "VISCOUNT":
                permissionGranted = playerRank.getRanking() <= 2;
                break;
            default:
                permissionGranted = playerRank.getRanking() <= 3;
        }

        if (!permissionGranted){
            player.sendMessage(Messages.WRONG_RANK.toString());
            return;
        }

        HashMap<UUID, GuildRank> carry = new HashMap<>();
        carry.put(target.getUniqueId(), rank);
        PromoteConfirmListeners.queue.put(player.getUniqueId(), carry);

        if (promote){
            PromoteConfirmListeners.promote.add(player.getUniqueId());
        } else {
            PromoteConfirmListeners.demote.add(player.getUniqueId());
        }

        player.openInventory(new PromoteConfirmInventory().getInventory());
    }
}
