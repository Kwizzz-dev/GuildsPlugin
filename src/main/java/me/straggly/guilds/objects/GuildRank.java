package me.straggly.guilds.objects;

import me.straggly.guilds.API;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public enum GuildRank
{
    KING(1), // Called <godOfCategory> if they're the #1 of any leaderboard category.
    DUKE(2), // Called "demigod" if they're the #1 of any leaderboard category.
    VISCOUNT(3),
    BARON(4),
    PLEBEIAN(5),
    FOREIGNER(6)
    ;

    private final int ranking;
    GuildRank(int ranking){
        this.ranking = ranking;
    }

    @Override
    public String toString() {
        return API.formatEnum(name());
    }

    public int getRanking() {
        return ranking;
    }

    public ArrayList<GuildRank> getInheritance(){
        return Arrays.stream(values())
                .filter(guildRank -> guildRank.getRanking() >= getRanking()).collect(Collectors.toCollection(ArrayList::new));
    }
}
