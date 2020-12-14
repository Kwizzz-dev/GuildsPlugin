package me.straggly.guilds;

import me.straggly.guilds.commands.CoreCommand;
import me.straggly.guilds.commands.sub_commands.misc.*;
import me.straggly.guilds.data.DataFile;
import me.straggly.guilds.events.ConnectionEvents;
import me.straggly.guilds.events.DeathEvent;
import me.straggly.guilds.events.QuestEvent;
import me.straggly.guilds.objects.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.logging.Level;

public final class Guilds extends JavaPlugin
{
    private static Guilds instance;
    public static Guilds getInstance() {
        return instance;
    }

    private static ArrayList<Guild> guilds = new ArrayList<>();
    public static ArrayList<Guild> getGuilds() {
        return guilds;
    }

    private static ArrayList<String> questStrings = new ArrayList<>();
    public static ArrayList<String> getQuestStrings() {
        return questStrings;
    }

    private static HashMap<UUID, Guild> profiles = new HashMap<>();
    public static HashMap<UUID, Guild> getProfiles() {
        return profiles;
    }

    private static DataFile quests;
    public static DataFile getQuestsFile() {
        return quests;
    }

    private static DataFile levelRequirements;
    public static DataFile getLevelRequirements() {
        return levelRequirements;
    }

    private static DataFile messages;
    public static DataFile getMessages() {
        return messages;
    }

    private static DataFile timestamps;
    public static DataFile getTimestamps() {
        return timestamps;
    }

    private static DataFile config;
    public static DataFile getCfg(){
        return config;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        this.quests = new DataFile(this, "quests", true);
        this.messages = new DataFile(this, "messages", true);
        this.timestamps = new DataFile(this, "timestamps", false);
        this.config = new DataFile(this, "config", true);
        this.levelRequirements = new DataFile(this, "level-requirements", true);

        Bukkit.getPluginManager().registerEvents(new ConnectionEvents(), this);
        Bukkit.getPluginManager().registerEvents(new TitleInventoryListeners(), this);
        Bukkit.getPluginManager().registerEvents(new CreateGuildPollListeners(), this);
        Bukkit.getPluginManager().registerEvents(new PollInventoryListeners(), this);
        Bukkit.getPluginManager().registerEvents(new PromoteInventoryListeners(), this);
        Bukkit.getPluginManager().registerEvents(new PromoteConfirmListeners(), this);
        Bukkit.getPluginManager().registerEvents(new GuildBuffInventoryListeners(), this);
        Bukkit.getPluginManager().registerEvents(new LeaderboardInventoryListeners(), this);
        Bukkit.getPluginManager().registerEvents(new GuildWarInventoryListeners(), this);
        Bukkit.getPluginManager().registerEvents(new DeathEvent(), this);
        Bukkit.getPluginManager().registerEvents(new QuestEvent(), this);

        getCommand("guild").setExecutor(new CoreCommand());

        loadGuilds();

        for (Player player : Bukkit.getOnlinePlayers()){
            Guild guild = API.getGuildFromPlayer(player);

            if (guild != null){
                guild.applyBuff(player);
                Guilds.getProfiles().put(player.getUniqueId(), guild);
            }
        }

        for (String s : getQuestsFile().getConfig().getConfigurationSection("quests").getKeys(false)){
            getQuestStrings().add(s);
        }

        for (Guild guild : getGuilds()){
            for (String s : guild.getFile().getConfig().getStringList("quest-progress")){
                if (s.equalsIgnoreCase("")) continue;

                String[] split = s.split("\\+");

                if (split[0].toUpperCase().contains("MINE")){
                    HashMap<Material, Integer> temp = new HashMap<>();

                    if (QuestEvent.blocks().containsKey(guild)){
                        HashMap<Material, Integer> current = QuestEvent.blocks().get(guild);
                        temp = current;
                    }

                    Material material = Material.matchMaterial(split[1]);
                    temp.put(material, Integer.parseInt(split[2]));

                    QuestEvent.blocks().put(guild, temp);

                } else if (split[0].toUpperCase().contains("KILL_PLAYERS")){

                    QuestEvent.kills().put(guild, Integer.parseInt(split[1]));

                } else {
                    getLogger().log(Level.WARNING, "Guild " + guild.getName() + " has the quest "
                            + API.formatEnum(split[0]) + " saved. It doesn't seem to be a valid quest.");
                }
            }
        }

        startSavingRunnable();
        startMonthlyCheck();
        startWeeklyCheck();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getGuilds().forEach(Guild::save);

        instance = null;

        if (!QuestEvent.blocks().isEmpty()) {
            QuestEvent.blocks().clear();
        }

        if (!QuestEvent.kills().isEmpty()) {
            QuestEvent.kills().clear();
        }
    }

    private void loadGuilds(){
        guilds.clear();

        File dir = new File(getDataFolder() + File.separator + "guilds" + File.separator);
        if (!dir.exists()) dir.mkdirs();

        for (File file : dir.listFiles()){
            new Guild(new DataFile(this, "guilds" + File.separator + file.getName().replaceAll(".yml", ""), false));
        }
    }

    private void startWeeklyCheck(){
        new BukkitRunnable(){
            @Override
            public void run() {
                LocalDateTime time = LocalDateTime.now();

                if (getTimestamps().getString("weekly-check") == null){
                    getTimestamps().getConfig().set("weekly-check", time.toString());
                    getTimestamps().saveConfig();
                } else {
                    LocalDateTime stored = LocalDateTime.parse(getTimestamps().getString("weekly-check"));
                    LocalDateTime storedCheck = stored.plusWeeks(1);

                    if (time.isAfter(storedCheck)){
                        getGuilds().forEach(guild -> guild.getQuests().clear());
                        getGuilds().forEach(guild -> guild.getFile().getConfig().set("quest-progress", null));
                        getGuilds().forEach(Guild::save);

                        loadGuilds();
                    }
                }
            }
        }.runTaskTimer(this, 0L, 20*43200);
    }

    private void startMonthlyCheck(){
        new BukkitRunnable(){
            @Override
            public void run() {
                LocalDateTime date = LocalDateTime.now();

                if (getTimestamps().getString("monthly-check") == null){
                    getTimestamps().getConfig().set("monthly-check", date.toString());
                    getTimestamps().saveConfig();
                } else {
                    LocalDateTime storedDate = LocalDateTime.parse(getTimestamps().getString("monthly-check"));
                    LocalDateTime storedDateAfterMonth = storedDate.plusDays(30);

                    if (date.isAfter(storedDateAfterMonth)){
                        File file = new File(getDataFolder(), "top-results.txt");
                        try {
                            if (!file.exists()) file.createNewFile();

                            FileWriter writer = new FileWriter(file);
                            writer.write(displayResults());

                            writer.flush();
                            writer.close();
                        } catch (Exception e){
                            e.printStackTrace();
                        }

                        getGuilds().forEach(guild -> guild.setMonthKills(0));
                        getGuilds().forEach(guild -> guild.setMonthlyWarWins(0));
                        getGuilds().forEach(Guild::clearAwards);
                        getGuilds().forEach(guild -> guild.setLeaderTitle(API.formatEnum(GuildRank.KING.name())));
                        getGuilds().forEach(Guild::save);

                        for (Guild guild : getGuilds()){
                            if (API.getKillsLevel(guild) > guild.getLevel()){
                                guild.setLevel(Math.max(API.getKillsLevel(guild), API.getMembersLevel(guild)));
                                guild.broadcast(Messages.GUILD_LEVEL_UP.toString().replaceAll("<level>", String.valueOf(guild.getLevel())));
                                guild.save();
                            }
                        }

                        Bukkit.broadcastMessage(Messages.MONTH_ENDED.toString());

                        getTimestamps().getConfig().set("monthly-check", date.plusMonths(1).toString());
                        getTimestamps().saveConfig();

                        int interestRate = getCfg().getConfig().getInt("interest-rate");
                        getGuilds().forEach(guild -> guild.setFunds((int) (guild.getFunds() * (guild.getInterestModifier() == 1.0 ? interestRate : guild.getInterestModifier()))));
                        getGuilds().forEach(Guild::save);

                        loadGuilds();
                    }
                }
            }
        }.runTaskTimer(this, 0L, 20*43200);
    }

    private String displayResults(){
        StringBuilder results = new StringBuilder();
        results.append("Hey! These are last month's top results.").append("\n");
        results.append("If an entry is \"N/A\" then, there weren't enough entries").append("\n");
        results.append("or, there was an internal error.").append("\n");
        results.append("Date: ").append(LocalDate.now().toString()).append("\n");

        for (Leaderboard leaderboard : Leaderboard.values()){
            results.append("\n");
            Guild[] values = leaderboard.getAnInterface().calculate(getGuilds());

            results.append(API.formatEnum(leaderboard.name())).append(":\n");
            for (Guild guild : values){
                if (guild != null){
                    results.append("- ").append(guild.getName()).append("\n");
                } else {
                    results.append("- N/A \n");
                }
            }

            if (values[0] != null){
                values[0].broadcast(Messages.AWARD_WON.toString()
                        .replaceAll("<award>", API.formatEnum(leaderboard.getAward().name())));
                values[0].getAwards().add(leaderboard.getAward());
                values[0].save();
            }
        }

        return results.toString();
    }

    private void startSavingRunnable(){
        new BukkitRunnable(){
            @Override
            public void run() {
                for (Guild guild : getGuilds()){
                    if (guild.getGuildPoll() != null
                    && LocalDateTime.now().isAfter(guild.getGuildPoll().getEnding())) guild.getGuildPoll().endPoll(guild);

                    for (Map.Entry<UUID, Integer> map : guild.getForeigners().entrySet()){
                        if (map.getValue() - (System.currentTimeMillis()/1000) <= 0){
                            guild.getForeigners().remove(map.getKey());
                            guild.getMembers().put(map.getKey(), GuildRank.PLEBEIAN);
                        }
                    }
                }

                getGuilds().forEach(Guild::save);
            }
        }.runTaskTimer(this, 20*60L, 20*60L);
    }
}
