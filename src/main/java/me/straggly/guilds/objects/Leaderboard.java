package me.straggly.guilds.objects;

import me.straggly.guilds.objects.api.LeaderboardInterface;

import java.util.*;

public enum Leaderboard
{
    KILLS(guilds -> {
        List<Guild> sorted = guilds;

        Comparator<Guild> killOrder = Comparator.comparingInt(Guild::getMonthKills);
        sorted.sort(killOrder);
        Collections.reverse(sorted);

        Guild[] returnVal = new Guild[3];
        returnVal[0] = sorted.size() >= 1 && sorted.get(0) != null ? sorted.get(0) : null;
        returnVal[1] = sorted.size() >= 2 && sorted.get(1) != null ? sorted.get(1) : null;
        returnVal[2] = sorted.size() >= 3 && sorted.get(2) != null ? sorted.get(2) : null;

        return returnVal;
    }, GuildAward.TOP_MONTHLY_KILLS),

    MONTHLY_WAR_WINS(guilds -> {
        List<Guild> sorted = guilds;

        Comparator<Guild> monthWins = Comparator.comparingInt(Guild::getMonthlyWarWins);
        sorted.sort(monthWins);

        Guild[] returnVal = new Guild[3];
        returnVal[0] = sorted.size() >= 1 && sorted.get(0) != null ? sorted.get(0) : null;
        returnVal[1] = sorted.size() >= 2 && sorted.get(1) != null ? sorted.get(1) : null;
        returnVal[2] = sorted.size() >= 3 && sorted.get(2) != null ? sorted.get(2) : null;

        return returnVal;
    }, GuildAward.TOP_MONTHLY_WAR_WINS),

    TOTAL_MEMBERS(guilds ->{
        List<Guild> sorted = guilds;

        Comparator<Guild> totalMembers = Comparator.comparingInt(o -> o.getMembers().size());
        sorted.sort(totalMembers);

        Guild[] returnVal = new Guild[3];
        returnVal[0] = sorted.size() >= 1 && sorted.get(0) != null ? sorted.get(0) : null;
        returnVal[1] = sorted.size() >= 2 && sorted.get(1) != null ? sorted.get(1) : null;
        returnVal[2] = sorted.size() >= 3 && sorted.get(2) != null ? sorted.get(2) : null;

        return returnVal;
    }, GuildAward.TOP_TOTAL_MEMBERS),

    TOTAL_FUNDS(guilds -> {
        List<Guild> sorted = guilds;

        Comparator<Guild> totalFunds = Comparator.comparingInt(Guild::getFunds);
        sorted.sort(totalFunds);

        Guild[] returnVal = new Guild[3];
        returnVal[0] = sorted.size() >= 1 && sorted.get(0) != null ? sorted.get(0) : null;
        returnVal[1] = sorted.size() >= 2 && sorted.get(1) != null ? sorted.get(1) : null;
        returnVal[2] = sorted.size() >= 3 && sorted.get(2) != null ? sorted.get(2) : null;

        return returnVal;
    }, GuildAward.TOP_TOTAL_FUNDS)
    ;

    private final LeaderboardInterface anInterface;
    private final GuildAward award;
    Leaderboard(LeaderboardInterface anInterface, GuildAward award){
        this.anInterface = anInterface;
        this.award = award;
    }

    public GuildAward getAward() {
        return award;
    }

    public LeaderboardInterface getAnInterface() {
        return anInterface;
    }
}
