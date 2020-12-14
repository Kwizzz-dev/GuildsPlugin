package me.straggly.guilds.commands.sub_commands.misc;

import me.straggly.guilds.API;
import me.straggly.guilds.Guilds;
import me.straggly.guilds.objects.Guild;
import me.straggly.guilds.objects.GuildBuff;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.Map;

public class GuildBuffInventory
{
    public Inventory getInventory(Guild guild, Player player){
        Inventory inventory = Bukkit.createInventory(null, 36, API.colorize("&e&lGuild Buffs"));

        int[] slots = { 10, 11, 12, 13, 14, 15, 16, 20, 21, 22, 23, 24 };

        ItemStack info = new ItemStack(Material.BOOK);
        ItemMeta iMeta = info.getItemMeta();
        iMeta.setDisplayName(API.colorize("&e&l&nGuild Buff Info"));
        iMeta.setLore(Arrays.asList(API.colorize("&fHere you can view your buffs"),
                API.colorize("&fand their levels, if you're ranked"),
                API.colorize("&eDuke &for higher, you can upgrade these"),
                API.colorize("&fbuffs for the guild!"),
                API.colorize(" "),
                API.colorize("&eFunds: &f" + Guilds.getCfg().getString("currency-type") + API.formatNumber(guild.getFunds())),
                API.colorize("&eLevel Points: &f" + guild.getLevelPoints())));
        info.setItemMeta(iMeta);
        inventory.setItem(4, info);

        for (Map.Entry<GuildBuff, Integer> map : guild.getBuffs().entrySet()){
            ItemStack stack = new ItemStack(Material.DRAGON_BREATH);
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(API.colorize("&eBuff: &f" + API.formatEnum(map.getKey().name())));

            if (guild.getMembers().get(player.getUniqueId()).getRanking() > 2){
                meta.setLore(Arrays.asList(API.colorize("&eCurrent Level: &f" + map.getValue())));
            } else {
                meta.setLore(Arrays.asList(API.colorize("&eCurrent Level: &f" + map.getValue()),
                        API.colorize("&f "),
                        API.colorize("&aLeft-click to upgrade."),
                        API.colorize("&cRight-click to downgrade.")));
            }

            NamespacedKey key = new NamespacedKey(Guilds.getInstance(), "enum-value");
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, map.getKey().name());

            stack.setItemMeta(meta);

            for (int i = 0; i < slots.length; i++){
                if (inventory.getItem(slots[i]) == null || inventory.getItem(slots[i]).getType().equals(Material.AIR)){
                    inventory.setItem(slots[i], stack);
                    break;
                }
            }
        }

        ItemStack stack = new ItemStack(Material.EMERALD);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(API.colorize("&eBuff: &fInterest Rate"));

        if (guild.getMembers().get(player.getUniqueId()).getRanking() > 2){
            meta.setLore(Arrays.asList(API.colorize("&eCurrent Value: &f" + guild.getInterestModifier())));
        } else {
            meta.setLore(Arrays.asList(API.colorize("&eCurrent Value: &f" + guild.getInterestModifier()),
                    API.colorize("&f "),
                    API.colorize("&aLeft-click to upgrade."),
                    API.colorize("&cRight-click to downgrade.")));
        }

        NamespacedKey key = new NamespacedKey(Guilds.getInstance(), "enum-value");
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, GuildBuff.INTEREST_RATE.name());

        stack.setItemMeta(meta);

        for (int i = 0; i < slots.length; i++){
            if (inventory.getItem(slots[i]) == null || inventory.getItem(slots[i]).getType().equals(Material.AIR)){
                inventory.setItem(slots[i], stack);
                break;
            }
        }

        return inventory;
    }
}
