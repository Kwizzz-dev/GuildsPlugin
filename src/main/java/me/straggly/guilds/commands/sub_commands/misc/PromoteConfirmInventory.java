package me.straggly.guilds.commands.sub_commands.misc;

import me.straggly.guilds.API;
import me.straggly.guilds.objects.Guild;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class PromoteConfirmInventory
{
    public Inventory getInventory(){
        Inventory inventory = Bukkit.createInventory(null, 27, API.colorize("&aConfirm this action..."));

        ItemStack confirm = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta cMeta = confirm.getItemMeta();
        cMeta.setDisplayName(API.colorize("&aConfirm"));
        cMeta.setLore(Arrays.asList(API.colorize("&fClick to confirm this action.")));
        confirm.setItemMeta(cMeta);

        ItemStack cancel = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta caMeta = confirm.getItemMeta();
        caMeta.setDisplayName(API.colorize("&cCancel"));
        caMeta.setLore(Arrays.asList(API.colorize("&fClick to confirm this action.")));
        cancel.setItemMeta(caMeta);

        inventory.setItem(12, confirm);
        inventory.setItem(14, cancel);

        return inventory;
    }
}
