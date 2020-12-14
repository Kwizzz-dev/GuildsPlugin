package me.straggly.guilds.objects;

import me.straggly.guilds.API;
import netscape.security.UserTarget;

import java.time.LocalDateTime;
import java.util.*;

public class GuildPoll
{
    private final LocalDateTime ending;
    private final UUID startedBy;
    private final String question;
    private final ArrayList<String> options = new ArrayList<>();
    private final HashMap<UUID, String> votes = new HashMap<>();

    public GuildPoll(String formattedString){
        String[] split = formattedString.split(";");

        this.ending = LocalDateTime.parse(split[0]);
        this.startedBy = UUID.fromString(split[1]);
        this.question = split[2];

        options.addAll(Arrays.asList(split).subList(3, split.length));
    }

    public HashMap<UUID, String> getVotes() {
        return votes;
    }

    public void endPoll(Guild guild){
        StringBuilder builder = new StringBuilder();
        builder.append(API.colorize("&f&l&m-------&e Guild Poll Results &f&l&m-------")).append("\n");
        builder.append(API.colorize("&eQuestion: &f" + getQuestion())).append("\n").append("\n");
        builder.append(API.colorize("&e&lResults")).append("\n");

        for (String option : options){
            int votes = 0;

            for (Map.Entry<UUID, String> map : getVotes().entrySet()){
                if (map.getValue().equalsIgnoreCase(option)){
                    votes++;
                    continue;
                }
            }

            builder.append(API.colorize("&e\"" + option + "\"&f: " + votes)).append("\n");
        }

        guild.broadcast(builder.toString());
        guild.setGuildPoll(null);
        guild.getFile().getConfig().set("poll", null);
        guild.getFile().saveConfig();
        guild.save();
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public LocalDateTime getEnding() {
        return ending;
    }

    public String getQuestion() {
        return question;
    }

    public UUID getStartedBy() {
        return startedBy;
    }
}
