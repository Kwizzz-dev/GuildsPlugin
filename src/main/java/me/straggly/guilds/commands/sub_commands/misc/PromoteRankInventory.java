package me.straggly.guilds.commands.sub_commands.misc;

import me.straggly.guilds.API;
import me.straggly.guilds.objects.Guild;
import me.straggly.guilds.objects.GuildRank;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class PromoteRankInventory
{
    public Inventory getInventory(Guild guild, Player player, Player target){
        int[] slots = { 19, 20, 21, 22, 23, 24, 25 };

        Inventory inventory = Bukkit.createInventory(null, 36, API.colorize("&e&l&nChoose a rank..."));

        ItemStack info = new ItemStack(Material.BOOK);
        ItemMeta iMeta = info.getItemMeta();
        iMeta.setDisplayName(API.colorize("&e&nGuild Promotions"));
        iMeta.setLore(Arrays.asList(API.colorize("&fClick the rank you'd like to promote/demote")
        , API.colorize("&e" + target.getName() + " &fto, then &aconfirm &for")
        , API.colorize("&ccancel &fthe promotion!")));
        info.setItemMeta(iMeta);

        inventory.setItem(13, info);

        GuildRank targetRank = guild.getMembers().get(target.getUniqueId());
        for (GuildRank rank : GuildRank.values()){
            ItemStack stack = new ItemStack(Material.NAME_TAG);
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(API.colorize((targetRank.getInheritance().contains(rank)
                    ? "&cDemote to " : "&aPromote to ")
                    + API.formatEnum(rank.name())));

            if (rank.equals(targetRank)){
                meta.setDisplayName(API.colorize("&7Current Rank: &f" + API.formatEnum(rank.name())));
            }

            stack.setItemMeta(meta);

            for (int i = 0; i < slots.length; i++){
                if (inventory.getItem(slots[i]) == null || inventory.getItem(slots[i]).equals(Material.AIR)){
                    inventory.setItem(slots[i], stack);
                    break;
                }
            }
        }

        return inventory;
    }
}
