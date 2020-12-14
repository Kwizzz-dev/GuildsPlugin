package me.straggly.guilds.objects;

import me.straggly.guilds.API;

public enum GuildAction
{
    VOTE(GuildRank.PLEBEIAN),
    VIEW_BUFFS(GuildRank.PLEBEIAN),
    JOIN_WAR(GuildRank.PLEBEIAN),
    INVITE(GuildRank.BARON),
    MUTE(GuildRank.VISCOUNT),
    PROMOTE_TO_BARON(GuildRank.VISCOUNT),
    CREATE_POLL(GuildRank.DUKE), // Time;UUID;question;str... answers
    BAN(GuildRank.DUKE),
    CHANGE_BUFFS(GuildRank.DUKE),
    KICK(GuildRank.DUKE),
    PROMOTE_TO_VISCOUNT(GuildRank.DUKE), // Needs more than 2/3 of the dukes votes
    CHANGE_LEADER_TITLE(GuildRank.KING),
    PROMOTE_TO_KING(GuildRank.KING), // The player being promoted needs to be at least Duke, king steps down in exchange
    OVERWRITE(GuildRank.KING) // Ultimate permission, overwrites dukes decisions and can promote/demote anyone
    ;

    private final GuildRank requiredRank;
    GuildAction(GuildRank requiredRank){
        this.requiredRank = requiredRank;
    }

    @Override
    public String toString() {
        return API.formatEnum(name());
    }

    public GuildRank getRequiredRank() {
        return requiredRank;
    }
}
