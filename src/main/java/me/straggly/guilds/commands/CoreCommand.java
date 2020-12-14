package me.straggly.guilds.commands;

import me.straggly.guilds.API;
import me.straggly.guilds.Guilds;
import me.straggly.guilds.Messages;
import me.straggly.guilds.commands.sub_commands.*;
import me.straggly.guilds.objects.Guild;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CoreCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 1){
            if (args[0].equalsIgnoreCase("create")){
                if (args.length != 2){
                    sendHelpMessage(sender, label);
                    return true;
                }

                if (!(sender instanceof Player)){
                    sender.sendMessage(API.colorize("&cThis command is only executable in-game."));
                    return true;
                }

                new CreateGuild().create((Player) sender, args);
            }

            /*

            JOIN

             */
            else if (args[0].equalsIgnoreCase("join")){
                if (args.length != 2){
                    sendHelpMessage(sender, label);
                    return true;
                }

                if (!(sender instanceof Player)){
                    sender.sendMessage(API.colorize("&cThis command is only executable in-game."));
                    return true;
                }

                new JoinGuild().join((Player) sender, args);
            }

            /*

            QUIT

             */
            else if (args[0].equalsIgnoreCase("quit")
            || args[0].equalsIgnoreCase("leave")){
                if (args.length != 1){
                    sendHelpMessage(sender, label);
                    return true;
                }

                if (!(sender instanceof Player)){
                    sender.sendMessage(API.colorize("&cThis command is only executable in-game."));
                    return true;
                }

                new QuitGuild().quit((Player) sender, args);
            }

            /*

            INVITE

             */
            else if (args[0].equalsIgnoreCase("invite")){
                if (args.length != 2){
                    sendHelpMessage(sender, label);
                    return true;
                }

                new InviteGuild().invite((Player) sender, args);
            }

            /*

            INFORMATION

             */
            else if (args[0].equalsIgnoreCase("info")
            || args[0].equalsIgnoreCase("about")){
                if (args.length > 2){
                    sendHelpMessage(sender, label);
                    return true;
                }

                Guild guild;

                if (args.length == 1){
                    if (!(sender instanceof Player)){
                        sender.sendMessage(API.colorize("&cThis command is only executable in-game."));
                        return true;
                    }

                    guild = Guilds.getProfiles().get(((Player) sender).getUniqueId());

                    if (guild == null){
                        sender.sendMessage(Messages.NOT_IN_GUILD.toString());
                        sender.sendMessage(API.colorize("&7&o( Try /guild info <guild> )"));
                        return true;
                    }
                } else {
                    String guildName = args[1];

                    guild = API.getGuildForName(guildName);
                }

                if (guild == null){
                    sender.sendMessage(Messages.GUILD_DOESNT_EXIST.toString());
                    return true;
                }

                new SendInfo().info(sender, args, guild);
                return true;
            }

            /*

            STATISTICS

             */
            else if (args[0].equalsIgnoreCase("stats")){
                if (args.length > 2){
                    sendHelpMessage(sender, label);
                    return true;
                }

                Guild guild;

                if (args.length == 1){
                    if (!(sender instanceof Player)){
                        sender.sendMessage(API.colorize("&cThis command is only executable in-game."));
                        return true;
                    }

                    guild = Guilds.getProfiles().get(((Player) sender).getUniqueId());

                    if (guild == null){
                        sender.sendMessage(Messages.NOT_IN_GUILD.toString());
                        sender.sendMessage(API.colorize("&7&o( Try /guild stats <guild> )"));
                        return true;
                    }
                } else {
                    String guildName = args[1];

                    guild = API.getGuildForName(guildName);
                }

                if (guild == null){
                    sender.sendMessage(Messages.GUILD_DOESNT_EXIST.toString());
                    return true;
                }

                new SendStats().stats(sender, args, guild);
                return true;
            }

            /*

            CREATE POLL

             */
            else if (args[0].equalsIgnoreCase("createpoll")){
                if (args.length != 2){
                    sendHelpMessage(sender, label);
                    return true;
                }

                if (!(sender instanceof Player)){
                    sender.sendMessage(API.colorize("&cThis command is only executable from in-game."));
                    return true;
                }

                new CreateGuildPoll().poll((Player) sender, args);
            }

            /*

            POLL

             */
            else if (args[0].equalsIgnoreCase("poll") || args[0].equalsIgnoreCase("vote")){
                if (args.length > 1){
                    sendHelpMessage(sender, label);
                    return true;
                }

                if (!(sender instanceof Player)){
                    sender.sendMessage(API.colorize("&cThis command is only executable from in-game."));
                    return true;
                }

                new ViewGuildPoll().poll((Player) sender);
            }

            /*

            PROMOTE/DEMOTE

             */
            else if (args[0].equalsIgnoreCase("promote") || args[0].equalsIgnoreCase("demote")){
                if (args.length != 2){
                    sendHelpMessage(sender, label);
                    return true;
                }

                if (!(sender instanceof Player)){
                    sender.sendMessage(API.colorize("&cThis command is only executable from in-game."));
                    return true;
                }

                new PromoteRank().promote(((Player) sender), args);
            }

            /*

            KICK

             */
            else if (args[0].equalsIgnoreCase("kick")){
                if (args.length != 2){
                    sendHelpMessage(sender, label);
                    return true;
                }

                if (!(sender instanceof Player)){
                    sender.sendMessage(API.colorize("&cThis command is only executable from in-game."));
                    return true;
                }

                new KickPlayer().kick(((Player) sender), args);
            }

            /*

            MUTE

             */
            else if (args[0].equalsIgnoreCase("mute")){
                if (args.length != 2){
                    sendHelpMessage(sender, label);
                    return true;
                }

                if (!(sender instanceof Player)){
                    sender.sendMessage(API.colorize("&cThis command is only executable from in-game."));
                    return true;
                }

                new MutePlayer().mute(((Player) sender), args);
            }

            /*

            BAN

             */
            else if (args[0].equalsIgnoreCase("ban")){
                if (args.length != 2){
                    sendHelpMessage(sender, label);
                    return true;
                }

                if (!(sender instanceof Player)){
                    sender.sendMessage(API.colorize("&cThis command is only executable from in-game."));
                    return true;
                }

                new BanPlayer().ban(((Player) sender), args);
            }

            /*

            UNBAN

             */
            else if (args[0].equalsIgnoreCase("unban")){
                if (args.length != 2){
                    sendHelpMessage(sender, label);
                    return true;
                }

                if (!(sender instanceof Player)){
                    sender.sendMessage(API.colorize("&cThis command is only executable from in-game."));
                    return true;
                }

                new UnbanPlayer().unban(((Player) sender), args);
            }

            /*

            chat

             */
            else if (args[0].equalsIgnoreCase("chat") || args[0].equalsIgnoreCase("msg")
            || args[0].equalsIgnoreCase("message") || args[0].equalsIgnoreCase("shout")){
                if (args.length < 2){
                    sendHelpMessage(sender, label);
                    return true;
                }

                if (!(sender instanceof Player)){
                    sender.sendMessage(API.colorize("&cThis command is only executable from in-game."));
                    return true;
                }

                new GuildChatCommand().chat(((Player) sender), args);
            }

            /*

            TITLE

             */
            else if (args[0].equalsIgnoreCase("title")){
                if (args.length != 1){
                    sendHelpMessage(sender, label);
                    return true;
                }

                if (!(sender instanceof Player)){
                    sender.sendMessage(API.colorize("&cThis command is only executable from in-game."));
                    return true;
                }

                new LeaderTitle().title((Player) sender, args);
            }

            /*

            LEADERBOARD

             */
            else if (args[0].equalsIgnoreCase("leaderboard") || args[0].equalsIgnoreCase("top")){
                if (args.length != 1){
                    sendHelpMessage(sender, label);
                    return true;
                }

                if (!(sender instanceof Player)){
                    sender.sendMessage(API.colorize("&cThis command is only executable from in-game."));
                    return true;
                }

                new LeaderboardCommand().leaderboard((Player) sender, args);
            }

            /*

            BUFF

             */
            else if (args[0].equalsIgnoreCase("buff")
            || args[0].equalsIgnoreCase("buffs")){
                if (args.length != 1){
                    sendHelpMessage(sender, label);
                    return true;
                }

                if (!(sender instanceof Player)){
                    sender.sendMessage(API.colorize("&cThis command is only executable from in-game."));
                    return true;
                }

                new GuildBuffCommand().buff(((Player) sender), args);
            }

            /*

            WAR

             */
            else if (args[0].equalsIgnoreCase("war")
                    || args[0].equalsIgnoreCase("guildwar")){
                if (args.length != 1){
                    sendHelpMessage(sender, label);
                    return true;
                }

                if (!(sender instanceof Player)){
                    sender.sendMessage(API.colorize("&cThis command is only executable from in-game."));
                    return true;
                }

                new GuildWarCommand().war(((Player) sender), args);
            }

            /*

            QUESTS

             */
            else if (args[0].equalsIgnoreCase("quests")
                    || args[0].equalsIgnoreCase("missions")
            || args[0].equalsIgnoreCase("jobs")){
                if (args.length != 1){
                    sendHelpMessage(sender, label);
                    return true;
                }

                if (!(sender instanceof Player)){
                    sender.sendMessage(API.colorize("&cThis command is only executable from in-game."));
                    return true;
                }

                new QuestCommand().quest(((Player) sender), args);
            }

            /*

            HELP

             */
            else if (args[0].equalsIgnoreCase("help")){
                if (!(sender instanceof Player)){
                    sender.sendMessage(API.colorize("&cThis command is only executable from in-game."));
                    return true;
                }

                if (args.length == 2){
                    new HelpCommand().help((Player) sender, label, args);
                } else if (args.length == 1){
                    Guild guild = API.getGuildFromPlayer((Player) sender);
                    if (guild == null){
                        args = new String[]{"help", "plebeian"};
                    } else {
                        args = new String[]{"help", guild.getMembers().get(((Player) sender).getUniqueId()).name()};
                    }
                } else {
                    sendHelpMessage(sender, label);
                }
            }

            else {
                sendHelpMessage(sender, label);
                return true;
            }

            return true;
        }

        sendHelpMessage(sender, label);
        return true;
    }

    public void sendHelpMessage(CommandSender player, String commandName){
        String[] args;

        if (player instanceof Player){
            Guild guild = API.getGuildFromPlayer((Player) player);
            if (guild == null){
                args = new String[]{"help", "none"};
            } else {
                args = new String[]{"help", guild.getMembers().get(((Player) player).getUniqueId()).name()};
            }
        } else {
            args = new String[]{"help", "none"};
        }

        new HelpCommand().help((Player) player, commandName, args);
    }
}
