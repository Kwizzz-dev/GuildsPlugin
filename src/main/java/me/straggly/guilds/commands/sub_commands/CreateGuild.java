package me.straggly.guilds.commands.sub_commands;

import me.straggly.guilds.API;
import me.straggly.guilds.Guilds;
import me.straggly.guilds.Messages;
import me.straggly.guilds.data.DataFile;
import me.straggly.guilds.objects.Guild;
import me.straggly.guilds.objects.GuildBuff;
import me.straggly.guilds.objects.GuildRank;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateGuild
{
    public void create(Player sender, String[] args){
        String guildName = args[1];

        if (API.getGuildForName(guildName) != null){
            sender.sendMessage(Messages.GUILD_ALREADY_EXISTS.toString());
            return;
        }

        if (guildName.length() < 3 || guildName.length() > 15){
            sender.sendMessage(Messages.WRONG_LENGTH.toString());
            return;
        }

        if (Guilds.getProfiles().get(sender.getUniqueId()) != null){
            sender.sendMessage(Messages.ALREADY_IN_GUILD.toString());
            return;
        }

        File temp = new File(Guilds.getInstance().getDataFolder() + File.separator + "guilds" + File.separator, guildName + ".yml");
        if (!temp.exists()) {
            try {
                temp.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        DataFile dataFile = new DataFile(Guilds.getInstance(), "guilds" + File.separator + guildName, false);

        dataFile.getConfig().set("leader", sender.getUniqueId().toString());
        dataFile.getConfig().set("leaderTitle", API.formatEnum(GuildRank.KING.name()));
        dataFile.getConfig().set("eloPoints", Guilds.getCfg().getConfig().getInt("default-elo-points"));
        dataFile.getConfig().set("interestRate", 1.0);
        dataFile.getConfig().set("levelPoints", 0);
        dataFile.getConfig().set("name", guildName);
        dataFile.getConfig().set("level", 1);
        dataFile.getConfig().set("funds", 0);
        dataFile.getConfig().set("kills", 0);
        dataFile.getConfig().set("monthlyKills", 0);
        dataFile.getConfig().set("warWins", 0);
        dataFile.getConfig().set("monthlyWarWins", 0);

        List<String> buffs = new ArrayList<>();
        for (GuildBuff buff : GuildBuff.values()){
            if (!buff.equals(GuildBuff.INTEREST_RATE)){
                buffs.add(buff.name() + ":" + 1);
            }
        }

        List<String> quests = new ArrayList<>();
        for (String s : Guilds.getQuestStrings()){
            quests.add(s + ":false");
        }

        List<String> members = new ArrayList<>();
        members.add(sender.getUniqueId() + ":" + GuildRank.KING.name());

        List<String> muteList = new ArrayList<>();
        List<String> banList = new ArrayList<>();

        dataFile.getConfig().set("buffs", buffs);
        dataFile.getConfig().set("members", members);
        dataFile.getConfig().set("muted", muteList);
        dataFile.getConfig().set("banned", banList);
        dataFile.getConfig().set("quests", quests);
        dataFile.saveConfig();

        Guild guild = new Guild(dataFile);

        sender.sendMessage(Messages.GUILD_CREATED.toString());

        Guilds.getProfiles().put(sender.getUniqueId(), guild);
        guild.save();
    }
}
