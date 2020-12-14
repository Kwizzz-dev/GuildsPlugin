package me.straggly.guilds.objects;

import com.sun.org.apache.xpath.internal.operations.Bool;
import me.straggly.guilds.API;
import me.straggly.guilds.Guilds;
import me.straggly.guilds.Messages;
import me.straggly.guilds.data.DataFile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;

public class Guild
{
    private UUID leader;
    private String name, leaderTitle;
    private int level, funds, monthKills, kills, warWins, monthlyWarWins, levelPoints;
    private double interestModifier;
    private final HashMap<UUID, GuildRank> members = new HashMap<>();
    private final HashMap<GuildBuff, Integer> buffs = new HashMap<>();
    private final HashMap<UUID, Integer> foreigners = new HashMap<>();
    private final HashMap<UUID, Integer> promotionVotes = new HashMap<>();
    private final HashMap<String, Boolean> quests = new HashMap<>();
    private final ArrayList<GuildAward> awards = new ArrayList<>();
    private final ArrayList<UUID> muteList = new ArrayList<>();
    private final ArrayList<UUID> banList = new ArrayList<>();
    private GuildPoll guildPoll = null;
    private GuildWar guildWar = null;
    private LocalDateTime warEnd = null;
    private int eloPoints;

    private final DataFile file;

    public Guild(File file){
        this.file = (DataFile) file;

        this.leader = UUID.fromString(getFile().getString("leader"));
        this.name = getFile().getString("name");
        this.level = Integer.parseInt(getFile().getString("level"));
        this.levelPoints = (Math.min(Guilds.getCfg().getConfig().getInt("max-level-points"),
                Integer.parseInt(getFile().getString("levelPoints"))));
        this.interestModifier = Double.parseDouble(getFile().getString("interestRate"));
        this.funds = Integer.parseInt(getFile().getString("funds"));
        this.kills = Integer.parseInt(getFile().getString("kills"));
        this.monthKills = Integer.parseInt(getFile().getString("monthlyKills"));
        this.warWins = Integer.parseInt(getFile().getString("warWins"));
        this.monthlyWarWins = Integer.parseInt(getFile().getString("monthlyWarWins"));
        this.leaderTitle = getFile().getString("leaderTitle");
        this.eloPoints = getFile().getConfig().getInt("eloPoints");

        for (String s : getFile().getConfig().getStringList("buffs")){
            buffs.put(GuildBuff.valueOf(s.split(":")[0]), Integer.parseInt(s.split(":")[1]));
        }

        for (String s : getFile().getConfig().getStringList("members")){
            members.put(UUID.fromString(s.split(":")[0]), GuildRank.valueOf(s.split(":")[1]));

            if (GuildRank.valueOf(s.split(":")[1]).equals(GuildRank.FOREIGNER) && s.split(":").length == 3){
                foreigners.put(UUID.fromString(s.split(":")[0]), Integer.parseInt(s.split(":")[2]));
            }
        }

        for (String s : getFile().getConfig().getStringList("promotion-votes")){
            promotionVotes.put(UUID.fromString(s.split(";")[0]), Integer.valueOf(s.split(";")[1]));
        }

        for (String s : getFile().getConfig().getStringList("awards")){
            awards.add(GuildAward.valueOf(s));
        }

        for (String s : getFile().getConfig().getStringList("muted")){
            muteList.add(UUID.fromString(s));
        }

        for (String s : getFile().getConfig().getStringList("banned")){
            banList.add(UUID.fromString(s));
        }

        for (String s : getFile().getConfig().getStringList("quests")){
            quests.put(s.split(":")[0], Boolean.valueOf(s.split(":")[1]));
        }

        if (getFile().getString("poll") != null){
            this.guildPoll = new GuildPoll(getFile().getString("poll"));
        }

        if (getFile().getString("war") != null && getFile().getString("warEnd") != null){
            String[] arr = getFile().getString("war").split(":");
            this.guildWar = new GuildWar(API.getGuildForName(arr[0]), this
                    , Integer.parseInt(arr[1]), false, LocalDateTime.parse(getFile().getString("warEnd")));
        }

        if (getFile().getString("warEnd") != null){
            this.warEnd = LocalDateTime.parse(getFile().getString("warEnd"));
        }

        Guilds.getGuilds().add(this);
    }

    public void playSound(Sound sound){
        for (Map.Entry<UUID, GuildRank> map : getMembers().entrySet()){
            Player player = Bukkit.getPlayer(map.getKey());

            if (player != null){
                player.playSound(
                        player.getLocation(),
                        sound,
                        10,
                        1
                );
            }
        }
    }

    public void applyBuff(Player player){
        for (PotionEffect effect : player.getActivePotionEffects()){
            player.removePotionEffect(effect.getType());
        }

        for (Map.Entry<GuildBuff, Integer> map : buffs.entrySet()){
            GuildBuff buff = map.getKey();
            int modifier = map.getValue()-1;

            if (modifier <= 0) continue;

            player.addPotionEffect(new PotionEffect(buff.getType(), Integer.MAX_VALUE, modifier-1, false, false, true));
        }
    }

    public void save(){
        getFile().getConfig().set("leader", getLeader().toString());
        getFile().getConfig().set("leaderTitle", getLeaderTitle());
        getFile().getConfig().set("levelPoints", (Math.min(Guilds.getCfg().getConfig().getInt("max-level-points"), getLevelPoints())));
        getFile().getConfig().set("interestRate", getInterestModifier());
        getFile().getConfig().set("name", getName());
        getFile().getConfig().set("level", getLevel());
        getFile().getConfig().set("funds", getFunds());
        getFile().getConfig().set("kills", getKills());
        getFile().getConfig().set("monthlyKills", getMonthKills());
        getFile().getConfig().set("warWins", getWarWins());
        getFile().getConfig().set("monthlyWarWins", getMonthlyWarWins());
        getFile().getConfig().set("eloPoints", getEloPoints());
        getFile().getConfig().set("guildWar", (getGuildWar() != null
                ? getGuildWar().getOpponent().getName()
                : null));
        getFile().getConfig().set("warEnd", (getGuildWar() != null
                ? getGuildWar().getEnd().toString()
                : null));

        List<String> buffs = new ArrayList<>();
        for (Map.Entry<GuildBuff, Integer> map : getBuffs().entrySet()){
            buffs.add(map.getKey().name() + ":" + map.getValue());
        }

        List<String> members = new ArrayList<>();
        for (Map.Entry<UUID, GuildRank> map : getMembers().entrySet()){
            if (foreigners.containsKey(map.getKey())){
                members.add(map.getKey() + ":" + map.getValue().name() + ":" + foreigners.get(map.getKey()));
            } else {
                members.add(map.getKey() + ":" + map.getValue().name());
            }
        }

        List<String> awards = new ArrayList<>();
        if (getAwards().size() > 0){
            for (GuildAward guildAward : getAwards()){
                awards.add(guildAward.name());
            }
        }

        List<String> votes = new ArrayList<>();
        for (Map.Entry<UUID, Integer> map : getPromotionVotes().entrySet()){
            votes.add(map.getKey().toString() + ";" + map.getValue());
        }

        List<String> banned = new ArrayList<>();
        for (UUID uuid : getBanList()){
            votes.add(uuid.toString());
        }

        List<String> muted = new ArrayList<>();
        for (UUID uuid : getMuteList()){
            muted.add(uuid.toString());
        }

        List<String> quests = new ArrayList<>();
        for (Map.Entry<String, Boolean> map : getQuests().entrySet()){
            quests.add(map.getKey() + ":" + map.getValue().toString());
        }

        getFile().getConfig().set("buffs", buffs);
        getFile().getConfig().set("members", members);
        getFile().getConfig().set("awards", awards);
        getFile().getConfig().set("promotion-votes", votes);
        getFile().getConfig().set("muted", muted);
        getFile().getConfig().set("banned", banned);
        getFile().getConfig().set("quests", quests);
        getFile().saveConfig();
    }

    public void saveQuestProgress(String id, Optional<Material> material, int progress){
        List<String> save = new ArrayList<>();
        save.addAll(getFile().getConfig().getStringList("quest-progress"));

        String saveString = id;

        if (material.isPresent()){
            saveString = id + "+" + material.get().name() + "+" + progress;
        }

        boolean exists = false;
        for (String s : save){
            String[] strings = s.split("\\+");

            if (s.contains(id)){
                save.add(saveString);
                exists = true;
                save.remove(s);
                break;
            }
        }

        if (!exists){
            save.add(saveString);
        }

        getFile().getConfig().set("quest-progress", save);
        getFile().saveConfig();
    }

    public LocalDateTime getWarEnd() {
        return warEnd;
    }

    public void setWarEnd(LocalDateTime warEnd) {
        this.warEnd = warEnd;
    }

    public void broadcast(String message){
        for (Map.Entry<UUID, GuildRank> map : getMembers().entrySet()){
            Player player = Bukkit.getPlayer(map.getKey());

            if (player == null) continue;

            player.sendMessage(API.colorize(message));
        }
    }

    public HashMap<String, Boolean> getQuests() {
        return quests;
    }

    public void setLevelPoints(int levelPoints) {
        this.levelPoints = (Math.min(Guilds.getCfg().getConfig().getInt("max-level-points"), levelPoints));
    }

    public int getEloPoints() {
        return eloPoints;
    }

    public void setEloPoints(int eloPoints) {
        this.eloPoints = eloPoints;
    }

    public int getLevelPoints() {
        return levelPoints;
    }

    public ArrayList<UUID> getMuteList() {
        return muteList;
    }

    public ArrayList<UUID> getBanList() {
        return banList;
    }

    public HashMap<UUID, Integer> getPromotionVotes() {
        return promotionVotes;
    }

    public void setGuildPoll(GuildPoll guildPoll) {
        this.guildPoll = guildPoll;
    }

    public GuildPoll getGuildPoll() {
        return guildPoll;
    }

    public void clearAwards(){
        this.awards.clear();
    }

    public HashMap<UUID, Integer> getForeigners() {
        return foreigners;
    }

    public ArrayList<GuildAward> getAwards() {
        return awards;
    }

    public DataFile getFile() {
        return file;
    }

    public int getDukeCount(){
        return Math.toIntExact(getMembers().entrySet().size());
    }

    public int getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<GuildBuff, Integer> getBuffs() {
        return buffs;
    }

    public GuildWar getGuildWar() {
        return guildWar;
    }

    public void setGuildWar(GuildWar guildWar) {
        this.guildWar = guildWar;
    }

    public void addPlayer(UUID uuid){
        getMembers().put(uuid, GuildRank.FOREIGNER);

        if (API.getMembersLevel(this) > getLevel()){
            setLevel(API.getMembersLevel(this));
            broadcast(Messages.GUILD_LEVEL_UP.toString().replaceAll("<level>", String.valueOf(getLevel())));
            save();
        }
    }

    public HashMap<UUID, GuildRank> getMembers() {
        return members;
    }

    public int getFunds() {
        return funds;
    }

    public String getLeaderTitle() {
        return leaderTitle;
    }

    public double getInterestModifier() {
        return interestModifier;
    }

    public int getMonthKills() {
        return monthKills;
    }

    public int getKills() {
        return kills;
    }

    public int getMonthlyWarWins() {
        return monthlyWarWins;
    }

    public int getWarWins() {
        return warWins;
    }

    public UUID getLeader() {
        return leader;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setFunds(int funds) {
        this.funds = funds;
    }

    public void setInterestModifier(double interestModifier) {
        this.interestModifier = interestModifier;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void setLeader(UUID leader) {
        this.leader = leader;
    }

    public void setMonthKills(int monthKills) {
        this.monthKills = monthKills;
    }

    public void setMonthlyWarWins(int monthlyWarWins) {
        this.monthlyWarWins = monthlyWarWins;
    }

    public void setWarWins(int warWins) {
        this.warWins = warWins;
    }

    public void setLeaderTitle(String leaderTitle) {
        this.leaderTitle = leaderTitle;
    }
}
