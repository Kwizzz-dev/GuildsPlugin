package me.straggly.guilds.commands.sub_commands.misc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.straggly.guilds.API;
import me.straggly.guilds.Guilds;
import me.straggly.guilds.objects.Guild;
import me.straggly.guilds.objects.GuildAward;
import me.straggly.guilds.objects.Leaderboard;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;

import static org.apache.commons.lang.Validate.notNull;

public class LeaderboardInventory
{
    public Inventory getMenuInventory(){
        Inventory inventory = Bukkit.createInventory(null, 27, API.colorize("&e&lPick a category..."));

        int[] slots = { 10, 12, 14, 16 };

        for (Leaderboard leaderboard : Leaderboard.values()){
            ItemStack stack = new ItemStack(Material.EMERALD);
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(API.colorize("&e&l" + API.formatEnum(leaderboard.name())));
            meta.setLore(Arrays.asList(API.colorize("&aClick to view " + API.formatEnum(leaderboard.name()) + " leaderboard.")));

            NamespacedKey key = new NamespacedKey(Guilds.getInstance(), "enum-value");
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, leaderboard.name());

            stack.setItemMeta(meta);

            for (int i = 0; i < slots.length; i++){
                if (inventory.getItem(slots[i]) == null || inventory.getItem(slots[i]).getType().equals(Material.AIR)){
                    inventory.setItem(slots[i], stack);
                    break;
                }
            }
        }

        return inventory;
    }

    public Inventory getLeaderboardInventory(Leaderboard leaderboard){
        Inventory inventory = Bukkit.createInventory(null, 27, API.colorize("&e&l" + API.formatEnum(leaderboard.name())));

        int[] slots = { 11, 13, 15 };
        Guild[] guilds = leaderboard.getAnInterface().calculate(Guilds.getGuilds());

        int pos = 1;
        for (Guild guild : guilds){
            ItemStack stack = itemFromBase64("ewogICJ0aW1lc3RhbXAiIDogMTYwNjcwMzI3MDYxNSwKICAicHJvZmlsZUlkIiA6ICI5ZDEzZjcyMTcxM2E0N2U0OTAwZTMyZGVkNjBjNDY3MyIsCiAgInByb2ZpbGVOYW1lIiA6ICJUYWxvZGFvIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzgwY2JkZDRmNDU0ZjNlODM1OWJjYzJiNGNiODY5NTVmMGQ2ZDZjNThmOWNhNGQzYzBiNmQ5MzY0ZDEwMGE5YzYiCiAgICB9CiAgfQp9");
            SkullMeta meta = (SkullMeta) stack.getItemMeta();
            meta.setDisplayName(API.colorize( (guild != null ? "&e&l#" + pos + ": &f" + guild.getName() : "&c&lN/A")));

            if (guild != null){
                meta.setOwningPlayer(Bukkit.getOfflinePlayer(guild.getLeader()));

                if (leaderboard.getAward().equals(GuildAward.TOP_MONTHLY_KILLS)){
                    meta.setLore(Arrays.asList(API.colorize("&fMonthly Kills: &e" + guild.getMonthKills())));
                } else if (leaderboard.getAward().equals(GuildAward.TOP_MONTHLY_WAR_WINS)){
                    meta.setLore(Arrays.asList(API.colorize("&fMonthly War Wins: &e" + guild.getMonthlyWarWins())));
                } else if (leaderboard.getAward().equals(GuildAward.TOP_TOTAL_FUNDS)){
                    meta.setLore(Arrays.asList(API.colorize("&fTotal Funds: &e"
                            + Guilds.getCfg().getString("currency-type") + API.formatNumber(guild.getFunds()))));
                } else if (leaderboard.getAward().equals(GuildAward.TOP_TOTAL_MEMBERS)){
                    meta.setLore(Arrays.asList(API.colorize("&fTotal Members: &e" + guild.getMembers().size()+1)));
                }
            } else {
                meta.setLore(Arrays.asList(API.colorize("&cNot enough guilds to calculate.")));
            }

            stack.setItemMeta(meta);

            for (int i = 0; i < slots.length; i++){
                if (inventory.getItem(slots[i]) == null || inventory.getItem(slots[i]).getType().equals(Material.AIR)){
                    inventory.setItem(slots[i], stack);
                    break;
                }
            }
        }

        return inventory;
    }

    private static boolean warningPosted = false;
    private static Field blockProfileField;
    private static Method metaSetProfileMethod;
    private static Field metaProfileField;

    private static void checkLegacy() {
        try {
            Material.class.getDeclaredField("PLAYER_HEAD");
            Material.valueOf("SKULL");
            if (!warningPosted) {
                Bukkit.getLogger().warning("SKULLCREATOR API - Using the legacy bukkit API with 1.13+ bukkit versions is not supported!");
                warningPosted = true;
            }
        } catch (IllegalArgumentException | NoSuchFieldException var1) {
        }

    }

    public static ItemStack createSkull() {
        checkLegacy();

        try {
            return new ItemStack(Material.valueOf("PLAYER_HEAD"));
        } catch (IllegalArgumentException var1) {
            return new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (short)3);
        }
    }

    public static ItemStack itemFromBase64(String base64) {
        return itemWithBase64(createSkull(), base64);
    }

    public static ItemStack itemWithBase64(ItemStack item, String base64) {
        notNull(item, "item");
        notNull(base64, "base64");
        if (!(item.getItemMeta() instanceof SkullMeta)) {
            return null;
        } else {
            SkullMeta meta = (SkullMeta)item.getItemMeta();
            mutateItemMeta(meta, base64);
            item.setItemMeta(meta);
            return item;
        }
    }

    private static GameProfile makeProfile(String b64) {
        UUID id = new UUID((long)b64.substring(b64.length() - 20).hashCode(), (long)b64.substring(b64.length() - 10).hashCode());
        GameProfile profile = new GameProfile(id, "aaaaa");
        profile.getProperties().put("textures", new Property("textures", b64));
        return profile;
    }

    private static void mutateItemMeta(SkullMeta meta, String b64) {
        try {
            if (metaSetProfileMethod == null) {
                metaSetProfileMethod = meta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
                metaSetProfileMethod.setAccessible(true);
            }

            metaSetProfileMethod.invoke(meta, makeProfile(b64));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException var5) {
            try {
                if (metaProfileField == null) {
                    metaProfileField = meta.getClass().getDeclaredField("profile");
                    metaProfileField.setAccessible(true);
                }

                metaProfileField.set(meta, makeProfile(b64));
            } catch (IllegalAccessException | NoSuchFieldException var4) {
                var4.printStackTrace();
            }
        }

    }
}
