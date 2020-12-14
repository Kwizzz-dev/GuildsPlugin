package me.straggly.guilds;

import com.sun.istack.internal.Nullable;
import me.straggly.guilds.objects.Guild;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class API
{
    public static String colorize(String msg){
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static String formatEnum(String msg){
        StringBuilder builder = new StringBuilder();
        String temp = msg.replaceAll("_", " ");

        boolean up = true;
        for (Character c : temp.toCharArray()){
            if (up){
                builder.append(c.toString().toUpperCase());
                up = false;
            } else builder.append(c.toString().toLowerCase());

            if (c.toString().equalsIgnoreCase(" ")){
                up = true;
            }
        }

        return builder.toString();
    }

    @Nullable
    public static Guild getGuildFromPlayer(Player player){
        Guild guild = null;
        for (Guild g : Guilds.getGuilds()){
            if (g.getMembers().containsKey(player.getUniqueId())) guild = g;
        }
        return guild;
    }

    public static int getMembersLevel(Guild guild){
        HashMap<Integer, Integer> levels = new HashMap<>();
        for (String s : Guilds.getLevelRequirements().getConfig().getStringList("members")){
            int level = Integer.parseInt(s.split(":")[0]);
            int requirement = Integer.parseInt(s.split(":")[1]);

            levels.put(level, requirement);
        }

        int level = 0;
        for (Map.Entry<Integer, Integer> map : levels.entrySet()){
            if (guild.getMembers().size() >= map.getValue()){
                if (map.getKey() > level){
                    level = map.getKey();
                }
            }
        }

        return level > 0 ? level : guild.getLevel();
    }

    public static int getKillsLevel(Guild guild){
        HashMap<Integer, Integer> kills = new HashMap<>();
        for (String s : Guilds.getLevelRequirements().getConfig().getStringList("kills")){
            int level = Integer.parseInt(s.split(":")[0]);
            int requirement = Integer.parseInt(s.split(":")[1]);

            kills.put(level, requirement);
        }

        int level = 0;
        for (Map.Entry<Integer, Integer> map : kills.entrySet()){
            if (guild.getMonthKills() >= map.getValue()){
                if (map.getKey() > level){
                    level = map.getKey();
                }
            }
        }

        return level > 0 ? level : guild.getLevel();
    }

    public static String formatNumber(int num){
        StringBuilder builder = new StringBuilder();
        int comma = 0;

        char[] split = String.valueOf(num).toCharArray();
        for (int i = split.length-1; i > -1; i--){
            if (comma == 3){
                builder.append(",");
                comma = 0;
            }
            builder.append(split[i]);
            comma++;
        }
        return builder.reverse().toString();
    }

    @Nullable
    public static String enumValueFromStack(ItemStack stack){
        if (!stack.hasItemMeta()) return null;

        ItemMeta meta = stack.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        NamespacedKey key = new NamespacedKey(Guilds.getInstance(), "enum-value");

        if (!container.has(key, PersistentDataType.STRING)) return null;

        return container.get(key, PersistentDataType.STRING);
    }

    @Nullable
    public static String guildNameFromStack(ItemStack stack){
        if (!stack.hasItemMeta()) return null;

        ItemMeta meta = stack.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        NamespacedKey key = new NamespacedKey(Guilds.getInstance(), "guild-name");

        if (!container.has(key, PersistentDataType.STRING)) return null;

        return container.get(key, PersistentDataType.STRING);
    }

    /**
     * Return a guild instance
     *
     * @param name
     * @return
     */
    @Nullable
    public static Guild getGuildForName(String name){
        for (Guild guild : Guilds.getGuilds()){
            if (guild.getName().equalsIgnoreCase(name)) return guild;
        }
        return null;
    }
}
