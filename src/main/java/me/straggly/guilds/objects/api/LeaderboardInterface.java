package me.straggly.guilds.objects.api;

import me.straggly.guilds.objects.Guild;

import java.util.ArrayList;

public interface LeaderboardInterface
{
    Guild[] calculate(ArrayList<Guild> guilds);
}
