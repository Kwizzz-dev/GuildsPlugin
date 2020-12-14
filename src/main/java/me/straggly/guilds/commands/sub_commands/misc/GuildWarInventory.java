package me.straggly.guilds.commands.sub_commands.misc;

import me.straggly.guilds.API;
import me.straggly.guilds.Guilds;
import me.straggly.guilds.objects.Guild;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

public class GuildWarInventory
{
    public Inventory getInventory(Guild guild){
        int[] topBar = { 0, 1, 2, 3, 4, 5, 6, 7, 8 };

        Inventory inventory = Bukkit.createInventory(null, 36, API.colorize("&e&lChoose your opponent..."));

        ItemStack info = new ItemStack(Material.BOOK);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.setDisplayName(API.colorize("&e&nGuild Wars"));
        infoMeta.setLore(Arrays.asList(API.colorize("&fWelcome warrior! Here is the"),
                API.colorize("&fhub to start your &eguild war&f!"),
                API.colorize(" "),
                API.colorize("&e&nWhy aren't all guilds here?"),
                API.colorize("&fThe guilds shown are those of a similar"),
                API.colorize("&elevel&f and &eELO &fto ensure fair play!"),
                API.colorize("&fThey also won't appear if they are currently"),
                API.colorize("&fat &cwar&f.")));
        info.setItemMeta(infoMeta);

        ItemStack sStack = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta sMeta = sStack.getItemMeta();
        sMeta.setDisplayName(API.colorize(" "));
        sStack.setItemMeta(sMeta);

        for (int i : topBar){
            inventory.setItem(i, sStack);
        }

        inventory.setItem(4, info);

        for (Guild guild1 : Guilds.getGuilds()){
            if(guild.getName().equals(guild1.getName())){
                continue;
            }

            if (Math.max(guild1.getEloPoints(), guild.getEloPoints())
            - Math.min(guild1.getEloPoints(), guild.getEloPoints()) < -100) {
                continue;
            }

            if (Math.max(guild1.getEloPoints(), guild.getEloPoints())
                    - Math.min(guild1.getEloPoints(), guild.getEloPoints()) > 100){
                continue;
            }

            ItemStack stack = new ItemStack(Material.IRON_SWORD);
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(API.colorize("&eGuild: &f" + guild1.getName()));
            meta.setLore(Arrays.asList(
                    API.colorize("&fLeader: &e" + Bukkit.getOfflinePlayer(guild1.getLeader()).getName()),
                    API.colorize("&fElo: &e" + guild1.getEloPoints()),
                    API.colorize("&fMonthly Wins: &e" + guild1.getMonthlyWarWins()),
                    API.colorize("&fMonthly Kills: &e" + guild1.getMonthKills())));

            NamespacedKey key = new NamespacedKey(Guilds.getInstance(), "guild-name");
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, guild1.getName());

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
