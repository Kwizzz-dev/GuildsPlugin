package me.straggly.guilds.commands.sub_commands.misc;

import me.straggly.guilds.API;
import me.straggly.guilds.Guilds;
import me.straggly.guilds.objects.Guild;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class GuildQuestInventory
{
    public Inventory getInventory(Guild guild){
        Inventory inventory = Bukkit.createInventory(null, 36, API.colorize("&6&lCurrent Quests"));

        int[] topBar = { 0, 1, 2, 3, 4, 5, 6, 7, 8 };

        ItemStack info = new ItemStack(Material.BOOK);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.setDisplayName(API.colorize("&e&nGuild Quests"));
        infoMeta.setLore(Arrays.asList(
                API.colorize("&fWelcome warrior! Here is the"),
                API.colorize("&fhub to view your &eguild quests&f!"),
                API.colorize("&f "),
                API.colorize("&fQuests are &ealways &factive and so your"),
                API.colorize("&fprogress will always be tracked.")));
        info.setItemMeta(infoMeta);

        ItemStack sStack = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta sMeta = sStack.getItemMeta();
        sMeta.setDisplayName(API.colorize(" "));
        sStack.setItemMeta(sMeta);

        for (int i : topBar){
            inventory.setItem(i, sStack);
        }

        inventory.setItem(4, info);

        for (String s : guild.getQuests().keySet()){
            ItemStack stack = new ItemStack(Material.MAP);
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(API.colorize("&eQuest: &f" + API.formatEnum(s)));
            meta.setLore(Arrays.asList(
                    API.colorize("&eReward: &f" + Guilds.getCfg().getString("currency-type")
                    + API.formatNumber(Guilds.getQuestsFile().getConfig().getInt("quests." + s + ".reward"))),
                    API.colorize("&f "),
                    API.colorize("&eStatus: " +
                            (
                                    guild.getQuests().get(s)
                                    ? "&aCompleted!"
                                            : "&cIn progress..."
                                    ))));
            stack.setItemMeta(meta);

            for (int i = 0; i < inventory.getSize(); i++){
                if (inventory.getItem(i) == null || inventory.getItem(i).getType().equals(Material.AIR)){
                    inventory.setItem(i, stack);
                    break;
                }
            }
        }

        return inventory;
    }
}
