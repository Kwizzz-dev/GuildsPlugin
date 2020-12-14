package me.straggly.guilds.commands.sub_commands.misc;

import me.straggly.guilds.API;
import me.straggly.guilds.Guilds;
import me.straggly.guilds.Messages;
import me.straggly.guilds.objects.Guild;
import me.straggly.guilds.objects.GuildRank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PromoteConfirmListeners implements Listener
{
    public static HashMap<UUID, HashMap<UUID, GuildRank>> queue = new HashMap<>();
    public static ArrayList<UUID> promote = new ArrayList<>();
    public static ArrayList<UUID> demote = new ArrayList<>();
    private HashMap<UUID, UUID> promoteToKing = new HashMap<>();

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){
        if (event.isCancelled()) return;

        Player player = event.getPlayer();

        if (!promoteToKing.containsKey(player.getUniqueId())) return;

        Guild guild = null;

        if (Guilds.getProfiles().containsKey(player.getUniqueId())){
            guild = Guilds.getProfiles().get(player.getUniqueId());
        }

        if (guild == null){
            player.sendMessage(Messages.NOT_IN_GUILD.toString());
            promoteToKing.remove(player.getUniqueId());
            return;
        }

        event.setCancelled(true);

        Player target = Bukkit.getPlayer(promoteToKing.get(player.getUniqueId()));

        if (target == null){
            player.sendMessage(Messages.PLAYER_OFFLINE.toString());
            return;
        }

        if (!event.getMessage().equalsIgnoreCase(guild.getName())){
            player.sendMessage(Messages.CONFIRMATION_WRONG.toString());
            return;
        }

        promoteToKing.remove(player.getUniqueId());

        guild.getMembers().put(player.getUniqueId(), GuildRank.DUKE);
        guild.getMembers().put(target.getUniqueId(), GuildRank.KING);
        guild.setLeader(target.getUniqueId());
        guild.save();

        guild.broadcast(Messages.KING_CHANGE.toString().replaceAll("<player>", target.getName()));
    }

    @EventHandler
    public void onMove(InventoryMoveItemEvent event){
        Player player = (Player) event.getSource().getViewers().get(0);

        Guild guild = null;

        if (!queue.containsKey(player.getUniqueId())) return;

        if (Guilds.getProfiles().containsKey(player.getUniqueId())){
            guild = Guilds.getProfiles().get(player.getUniqueId());
        }

        if (guild == null) return;

        if (Arrays.equals(event.getDestination().getContents(),
                new PromoteConfirmInventory().getInventory().getContents())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event){
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!event.getView().getTitle().equalsIgnoreCase(API.colorize("&aConfirm this action..."))) return;

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
        if (!event.getView().getTitle().equalsIgnoreCase(API.colorize("&aConfirm this action..."))) return;
        if (event.getCurrentItem() == null) return;

        Player player = (Player) event.getWhoClicked();

        Guild guild = null;

        if (Guilds.getProfiles().containsKey(player.getUniqueId())) {
            guild = Guilds.getProfiles().get(player.getUniqueId());
        }

        if (guild == null) {
            player.sendMessage(Messages.NOT_IN_GUILD.toString());
            return;
        }

        event.setCancelled(true);
        player.closeInventory();

        Player target = null;
        GuildRank targetRank = null;

        for (Map.Entry<UUID, GuildRank> map : queue.get(player.getUniqueId()).entrySet()){
            target = Bukkit.getPlayer(map.getKey());
            targetRank = map.getValue();
        }

        if (target == null){
            player.sendMessage(Messages.PLAYER_OFFLINE.toString());
            return;
        }

        if (targetRank == null){
            player.sendMessage(Messages.RANK_DOESNT_EXIST.toString());
            return;
        }

        ItemStack stack = event.getCurrentItem();
        if (!stack.hasItemMeta()) return;

        boolean confirm = ChatColor.stripColor(stack.getItemMeta().getDisplayName()).contains("Confirm");

        if (!confirm) return;

        if (promote.contains(player.getUniqueId())){
            /*

            PROMOTION SECTION

             */

            promote.remove(player.getUniqueId());

            if (targetRank.equals(GuildRank.KING) && guild.getMembers().get(target.getUniqueId()).equals(GuildRank.DUKE)){
                if (!guild.getMembers().get(player.getUniqueId()).equals(GuildRank.KING)){
                    player.sendMessage(Messages.PROMOTE_WRONG_RANK.toString()
                            .replaceAll("<rank>", API.formatEnum(guild.getMembers().get(player.getUniqueId()).name())));
                    return;
                }

                player.sendMessage(Messages.TYPE_TO_CONFIRM.toString());
                promoteToKing.put(player.getUniqueId(), target.getUniqueId());
            } else if (targetRank.equals(GuildRank.KING) && !guild.getMembers().get(target.getUniqueId()).equals(GuildRank.DUKE)){
                player.sendMessage(Messages.PROMOTE_WRONG_RANK.toString()
                        .replaceAll("<rank>", API.formatEnum(guild.getMembers().get(player.getUniqueId()).name())));
                return;
            }

            if (targetRank.equals(GuildRank.DUKE)){
                if (guild.getMembers().get(player.getUniqueId()).getRanking() > 2){
                    player.sendMessage(Messages.PROMOTE_WRONG_RANK.toString()
                            .replaceAll("<rank>", API.formatEnum(guild.getMembers().get(player.getUniqueId()).name())));
                    return;
                }

                int count = guild.getPromotionVotes().containsKey(target.getUniqueId()) ? guild.getPromotionVotes().get(target.getUniqueId())+1 : 1;
                int votesRequired = ((guild.getDukeCount() > 0 ? guild.getDukeCount() : 1)/3) *2;

                if (count >= votesRequired || guild.getMembers().get(player.getUniqueId()).equals(GuildRank.KING)){
                    guild.getPromotionVotes().remove(target.getUniqueId());
                    guild.getMembers().put(target.getUniqueId(), targetRank);
                    guild.save();

                    player.sendMessage(Messages.PLAYER_PROMOTED.toString());
                    target.sendMessage(Messages.YOU_HAVE_BEEN_PROMOTED.toString()
                    .replaceAll("<rank>", API.formatEnum(targetRank.name())));
                } else {
                    guild.getPromotionVotes().put(target.getUniqueId(), count);
                    guild.save();

                    player.sendMessage(Messages.PROMOTE_MORE_VOTES.toString());
                }
                return;
            }

            if (targetRank.equals(GuildRank.VISCOUNT)){
                if (guild.getMembers().get(player.getUniqueId()).getRanking() > 2){
                    player.sendMessage(Messages.PROMOTE_WRONG_RANK.toString()
                            .replaceAll("<rank>", API.formatEnum(guild.getMembers().get(player.getUniqueId()).name())));
                    return;
                }

                guild.getPromotionVotes().remove(target.getUniqueId());
                guild.getMembers().put(target.getUniqueId(), targetRank);
                guild.save();

                player.sendMessage(Messages.PLAYER_PROMOTED.toString());
                target.sendMessage(Messages.YOU_HAVE_BEEN_PROMOTED.toString()
                        .replaceAll("<rank>", API.formatEnum(targetRank.name())));
            }

            if (targetRank.getRanking() >= 4){
                if (guild.getMembers().get(player.getUniqueId()).getRanking() > 3){
                    player.sendMessage(Messages.PROMOTE_WRONG_RANK.toString()
                    .replaceAll("<rank>", API.formatEnum(guild.getMembers().get(player.getUniqueId()).name())));
                    return;
                }

                guild.getMembers().put(target.getUniqueId(), targetRank);
                guild.save();

                player.sendMessage(Messages.PLAYER_PROMOTED.toString());
                target.sendMessage(Messages.YOU_HAVE_BEEN_PROMOTED.toString()
                        .replaceAll("<rank>", API.formatEnum(targetRank.name())));
                return;
            }
        } else {
            /*

            DEMOTION SECTION

             */

            if (targetRank.equals(GuildRank.DUKE)){
                if (guild.getMembers().get(player.getUniqueId()).getRanking() > 2){
                    player.sendMessage(Messages.DEMOTE_WRONG_RANK.toString()
                    .replaceAll("<rank>", API.formatEnum(guild.getMembers().get(player.getUniqueId()).name())));
                    return;
                }

                guild.getPromotionVotes().remove(target.getUniqueId());
                guild.getMembers().put(target.getUniqueId(), targetRank);
                guild.save();

                player.sendMessage(Messages.PLAYER_DEMOTED.toString());
                target.sendMessage(Messages.YOU_HAVE_BEEN_DEMOTED.toString()
                        .replaceAll("<rank>", API.formatEnum(targetRank.name())));
                return;
            }

            if (targetRank.equals(GuildRank.VISCOUNT)){
                if (guild.getMembers().get(player.getUniqueId()).getRanking() > 1){
                    player.sendMessage(Messages.DEMOTE_WRONG_RANK.toString()
                            .replaceAll("<rank>", API.formatEnum(guild.getMembers().get(player.getUniqueId()).name())));
                    return;
                }

                guild.getPromotionVotes().remove(target.getUniqueId());
                guild.getMembers().put(target.getUniqueId(), targetRank);
                guild.save();

                player.sendMessage(Messages.PLAYER_DEMOTED.toString());
                target.sendMessage(Messages.YOU_HAVE_BEEN_DEMOTED.toString()
                        .replaceAll("<rank>", API.formatEnum(targetRank.name())));
                return;
            }

            if (targetRank.getRanking() >= 4){
                if (guild.getMembers().get(player.getUniqueId()).getRanking() > 3){
                    player.sendMessage(Messages.DEMOTE_WRONG_RANK.toString()
                    .replaceAll("<rank>", API.formatEnum(guild.getMembers().get(player.getUniqueId()).name())));
                    return;
                }

                guild.getMembers().put(target.getUniqueId(), targetRank);
                guild.save();

                player.sendMessage(Messages.PLAYER_DEMOTED.toString());
                target.sendMessage(Messages.YOU_HAVE_BEEN_DEMOTED.toString()
                        .replaceAll("<rank>", API.formatEnum(targetRank.name())));
                return;
            }
        }
    }
}
