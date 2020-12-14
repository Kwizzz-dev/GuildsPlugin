package me.straggly.guilds.events;

import me.straggly.guilds.API;
import me.straggly.guilds.Messages;
import me.straggly.guilds.objects.Guild;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathEvent implements Listener
{
    @EventHandler
    public void onKill(PlayerDeathEvent event){
        Player died = event.getEntity();

        if (died.getKiller() == null) return;

        Player killer = died.getKiller();

        Guild diedGuild = API.getGuildFromPlayer(died);

        if (diedGuild == null) return;

        Guild killerGuild = API.getGuildFromPlayer(killer);

        if (killerGuild == null) return;

        if (killerGuild.getGuildWar() == null) return;
        if (!killerGuild.getGuildWar().getOpponent().equals(diedGuild)) return;

        killerGuild.broadcast(Messages.GUILD_WAR_KILL.toString()
        .replaceAll("<player>", killer.getName())
        .replaceAll("<opponent>", died.getName()));

        killerGuild.setKills(killerGuild.getKills() + 1);
        killerGuild.setMonthKills(killerGuild.getMonthKills() + 1);
        diedGuild.getGuildWar().setKills(diedGuild.getGuildWar().getKills() + 1);
        killerGuild.save();
    }
}
