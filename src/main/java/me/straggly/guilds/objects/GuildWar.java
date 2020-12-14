package me.straggly.guilds.objects;

import me.straggly.guilds.API;
import me.straggly.guilds.Guilds;
import me.straggly.guilds.Messages;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
import java.util.Random;

public class GuildWar
{
    private final Guild opponent, guild;
    private int kills;
    private LocalDateTime end;

    public GuildWar(Guild opponent, Guild guild, int kills, boolean firstInit, LocalDateTime end){
        this.opponent = opponent;
        this.kills = kills;
        this.end = end;
        this.guild = guild;

        if (firstInit){
            opponent.broadcast(Messages.WAR_STARTED.toString().replaceAll("<guild>", guild.getName()));
        }

        new BukkitRunnable(){
            @Override
            public void run() {
                if (LocalDateTime.now().isAfter(end)){

                    int elo = new Random().nextInt(5)+11;

                    if (guild.getKills() > opponent.getKills()){
                        opponent.broadcast(Messages.GUILD_LOSER.toString()
                        .replaceAll("<kills>", String.valueOf(getKills())));
                        opponent.playSound(Sound.ENTITY_VILLAGER_NO);
                        opponent.setEloPoints(opponent.getEloPoints() - elo);
                        opponent.broadcast(API.colorize("&c-" + elo + " rating."));


                    } else if (guild.getKills() < opponent.getKills()){
                        opponent.broadcast(Messages.GUILD_WINNER.toString()
                                .replaceAll("<kills>", String.valueOf(getKills())));
                        opponent.playSound(Sound.ENTITY_EXPERIENCE_BOTTLE_THROW);

                        new BukkitRunnable(){
                            @Override
                            public void run() {
                                opponent.playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
                            }
                        }.runTaskLater(Guilds.getInstance(), 15L);

                        opponent.setMonthlyWarWins(opponent.getMonthlyWarWins()+1);
                        opponent.setEloPoints(opponent.getEloPoints() + elo);
                        opponent.broadcast(API.colorize("&a+" + elo + " rating."));
                        opponent.setWarWins(opponent.getWarWins() + 1);


                    } else {
                        opponent.broadcast(Messages.GUILD_DRAW.toString()
                                .replaceAll("<kills>", String.valueOf(getKills())));
                        opponent.playSound(Sound.ENTITY_VILLAGER_NO);
                        opponent.broadcast(API.colorize("&7-0 rating."));
                    }

                    opponent.setGuildWar(null);
                    opponent.setWarEnd(null);

                    opponent.save();

                    this.cancel();
                }
            }
        }.runTaskTimer(Guilds.getInstance(), 0L, 20*5L);
    }

    public Guild getGuild() {
        return guild;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public Guild getOpponent() {
        return opponent;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }
}
