package me.straggly.guilds.commands.sub_commands.misc;

import me.straggly.guilds.API;
import me.straggly.guilds.Guilds;
import me.straggly.guilds.Messages;
import me.straggly.guilds.objects.Guild;
import me.straggly.guilds.objects.GuildAction;
import me.straggly.guilds.objects.GuildBuff;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

import java.util.Arrays;
import java.util.UUID;

public class GuildBuffInventoryListeners implements Listener
{
    @EventHandler
    public void onMove(InventoryMoveItemEvent event){
        Player player = (Player) event.getSource().getViewers().get(0);

        Guild guild = null;

        if (Guilds.getProfiles().containsKey(player.getUniqueId())){
            guild = Guilds.getProfiles().get(player.getUniqueId());
        }

        if (guild == null) return;

        if (Arrays.equals(event.getDestination().getContents(),
                new GuildBuffInventory().getInventory(guild, player).getContents())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event){
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!event.getView().getTitle().equalsIgnoreCase(API.colorize("&e&lGuild Buffs"))) return;

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
        if (!event.getView().getTitle().equalsIgnoreCase(API.colorize("&e&lGuild Buffs"))) return;
        if (event.getCurrentItem() == null) return;

        Player player = (Player) event.getWhoClicked();

        Guild guild = null;

        event.setCancelled(true);
        player.closeInventory();

        if (Guilds.getProfiles().containsKey(player.getUniqueId())) {
            guild = Guilds.getProfiles().get(player.getUniqueId());
        }

        if (guild == null) {
            player.sendMessage(Messages.NOT_IN_GUILD.toString());
            return;
        }

        if (!guild.getMembers().get(player.getUniqueId()).getInheritance().contains(GuildAction.CHANGE_BUFFS.getRequiredRank())) {
            return;
        }

        String enumName = API.enumValueFromStack(event.getCurrentItem());
        if (enumName == null){
            return;
        }

        boolean add = event.getClick().isLeftClick();

        GuildBuff buff = GuildBuff.valueOf(enumName);
        int currentValue = guild.getBuffs().getOrDefault(buff, 1);

        if (add){
            if (guild.getLevelPoints() <= 0){
                player.sendMessage(Messages.NO_LEVEL_POINTS.toString());
                return;
            }

            if (buff.equals(GuildBuff.INTEREST_RATE)){
                if (Guilds.getCfg().getConfig().getInt("max-level-points") < ((guild.getInterestModifier()/0.5)-2)+1){
                    player.sendMessage(Messages.MAX_UPGRADES.toString());
                    return;
                }

                if (guild.getFunds() < buff.getPrice()){
                    player.sendMessage(Messages.NOT_ENOUGH_MONEY.toString());
                    return;
                }

                guild.setInterestModifier(guild.getInterestModifier() + 0.5);
            } else {
                if (Guilds.getCfg().getConfig().getInt("max-level-points") < currentValue+1){
                    player.sendMessage(Messages.MAX_UPGRADES.toString());
                    return;
                }

                if (guild.getFunds() < buff.getPrice()){
                    player.sendMessage(Messages.NOT_ENOUGH_MONEY.toString());
                    return;
                }

                guild.getBuffs().put(buff, currentValue+1);
            }
            guild.setFunds(guild.getFunds()-buff.getPrice());
            guild.setLevelPoints(guild.getLevelPoints() - 1);

            for (UUID uuid : guild.getMembers().keySet()){
                Player target = Bukkit.getPlayer(uuid);
                if (target != null){
                    guild.applyBuff(target);
                }
            }

            guild.save();
            guild.broadcast(Messages.GUILD_BUFF_UPGRADED.toString()
            .replaceAll("<buff>", API.formatEnum(buff.name())));
        } else {
            if (buff.equals(GuildBuff.INTEREST_RATE)){
                if (((guild.getInterestModifier()/0.5)-2)-1 < 0){
                    player.sendMessage(Messages.MIN_UPGRADES.toString());
                    return;
                }

                guild.setInterestModifier(guild.getInterestModifier() - 0.5);
            } else {
                if (currentValue-1 < 1){
                    player.sendMessage(Messages.MIN_UPGRADES.toString());
                    return;
                }

                guild.getBuffs().put(buff, currentValue-1);
            }
            guild.setLevelPoints(guild.getLevelPoints() + 1);

            for (UUID uuid : guild.getMembers().keySet()){
                Player target = Bukkit.getPlayer(uuid);
                if (target != null){
                    guild.applyBuff(target);
                }
            }

            guild.save();
            guild.broadcast(Messages.GUILD_BUFF_DOWNGRADED.toString()
                    .replaceAll("<buff>", API.formatEnum(buff.name())));
        }
    }
}
