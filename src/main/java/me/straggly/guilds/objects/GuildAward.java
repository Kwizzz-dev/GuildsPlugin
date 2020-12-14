package me.straggly.guilds.objects;

import com.sun.istack.internal.Nullable;

public enum GuildAward
{
    TOP_MONTHLY_WAR_WINS("Athena"),
    TOP_MONTHLY_KILLS("Ares"),
    TOP_TOTAL_MEMBERS("Hestia"),
    TOP_TOTAL_FUNDS("Plutus")
    ;

    private final String leaderTitle;
    GuildAward(String leaderTitle){
        this.leaderTitle = leaderTitle;
    }

    public String getLeaderTitle() {
        return leaderTitle;
    }

    @Nullable
    public static GuildAward getAwardFromName(String title){
        for (GuildAward guildAward : values()){
            if (guildAward.getLeaderTitle().equalsIgnoreCase(title)) {
                return guildAward;
            }
        }
        return null;
    }
}
