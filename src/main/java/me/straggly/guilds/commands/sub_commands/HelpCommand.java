package me.straggly.guilds.commands.sub_commands;

import me.straggly.guilds.API;
import me.straggly.guilds.objects.Guild;
import org.bukkit.entity.Player;

public class HelpCommand
{
    /*

    Categories:
    King - List all
    Duke - List duke
    Viscount
    Plebeian
    Foreigner

     */
    public void help(Player player, String label, String[] args){
        String category = args[1];

        StringBuilder help = new StringBuilder();
        help.append("&f&l&m-------&e Guilds &f&l&m-------").append("\n");
        switch (category.toUpperCase()){
            case "KING":
                help.append("&f- &e/").append(label).append(" title").append("\n");
            case "DUKE":
                help.append("&f- &e/").append(label).append(" kick").append("\n");
                help.append("&f- &e/").append(label).append(" ban").append("\n");
                help.append("&f- &e/").append(label).append(" unban").append("\n");
                help.append("&f- &e/").append(label).append(" war").append("\n");
                help.append("&f- &e/").append(label).append(" createpoll <time>").append("\n");
            case "VISCOUNT":
                help.append("&f- &e/").append(label).append(" promote/demote").append("\n");
                help.append("&f- &e/").append(label).append(" mute <player>").append("\n");
            case "BARON":
                help.append("&f- &e/").append(label).append(" invite <player>").append("\n");
            case "PLEBEIAN":
                help.append("&f- &e/").append(label).append(" war").append("\n");
                help.append("&f- &e/").append(label).append(" poll").append("\n");
                help.append("&f- &e/").append(label).append(" quit").append("\n");
                help.append("&f- &e/").append(label).append(" chat <message>").append("\n");
                help.append("&f- &e/").append(label).append(" buffs").append("\n");
                help.append("&f- &e/").append(label).append(" quests").append("\n");
                break;
            case "NONE":
                help.append("&f- &e/").append(label).append(" join <guild>").append("\n");
                help.append("&f- &e/").append(label).append(" info <guild>").append("\n");
                help.append("&f- &e/").append(label).append(" stats <guild>").append("\n");
                help.append("&f- &e/").append(label).append(" leaderboard").append("\n");
                help.append("&f- &e/").append(label).append(" create <name>").append("\n");
                break;
            default:
                help.append("&cUnknown rank, try /").append(label).append(" help <rank>.");
                break;
        }

        player.sendMessage(API.colorize(help.toString()));
    }
}
