package me.straggly.guilds.commands.sub_commands.misc;

import me.straggly.guilds.API;
import me.straggly.guilds.objects.Guild;
import me.straggly.guilds.objects.GuildAward;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class TitleInventory
{
    public Inventory getInventory(Guild guild){
        int[] slots = { 10, 11, 12, 13, 14, 15, 16 };

        Inventory inventory = Bukkit.createInventory(null, 27, API.colorize("&eSelect a title..."));

        if (guild.getAwards().size() > 0){
            for (GuildAward award : guild.getAwards()){
                ItemStack stack = new ItemStack(Material.NAME_TAG);
                ItemMeta meta = stack.getItemMeta();
                meta.setDisplayName(API.colorize("&c" + award.getLeaderTitle() + " Title"));
                meta.setLore(Arrays.asList(API.colorize("&7Click to change title."),
                        API.colorize("&fAward: &e" + API.formatEnum(award.name()))));
                stack.setItemMeta(meta);

                for (int i = 0; i < slots.length; i++){
                    if (inventory.getItem(i) == null || inventory.getItem(i).getType().equals(Material.AIR)){
                        inventory.setItem(i, stack);
                        break;
                    }
                }
            }
        } else {
            ItemStack stack = new ItemStack(Material.BARRIER);
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(API.colorize("&cNo Awards"));
            meta.setLore(Arrays.asList(API.colorize("&cYou don't seem to have any awards."),
                    API.colorize("&cYou can earn awards by winning"),
                    API.colorize("&cevents such as the monthly most kills.")));
            stack.setItemMeta(meta);

            inventory.setItem(13, stack);
        }

        return inventory;
    }
}
