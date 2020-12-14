package me.straggly.guilds.commands.sub_commands.misc;

import me.straggly.guilds.API;
import me.straggly.guilds.objects.Guild;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class GuildPollInventory
{
    public Inventory getInventory(Guild guild){
        Inventory inventory = Bukkit.createInventory(null, 36, API.colorize("&e&l&nGuild Poll"));

        ItemStack question = new ItemStack(Material.BOOK);
        ItemMeta qMeta = question.getItemMeta();
        qMeta.setDisplayName(API.colorize("&e&lPoll Information:"));
        qMeta.setLore(Arrays.asList(API.colorize("&eQuestion: &f" + guild.getGuildPoll().getQuestion()),
                API.colorize("&eSelect below to vote!")));
        question.setItemMeta(qMeta);

        inventory.setItem(13, question);

        int i = 1;
        for (String string : guild.getGuildPoll().getOptions()){
            ItemStack option = new ItemStack(Material.NAME_TAG);
            ItemMeta meta = option.getItemMeta();
            meta.setDisplayName(API.colorize("&eOption #" + (i)));
            meta.setLore(Arrays.asList(API.colorize("&f" + string), " ",
                    API.colorize("&aClick me to vote for this option.")));
            option.setItemMeta(meta);

            for (int j = 21; j < 24; j++){
                if (inventory.getItem(j) == null || inventory.getItem(j).getType().equals(Material.AIR)){
                    inventory.setItem(j, option);
                    break;
                }
            }
            i++;
        }

        return inventory;
    }
}
