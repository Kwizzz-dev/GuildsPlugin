package me.straggly.guilds.data;

import com.google.common.io.Files;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class DataFile extends File
{
    private JavaPlugin plugin;
    private FileConfiguration configuration;
    private boolean hasDefault;
    private String fileName;

    public DataFile(JavaPlugin plugin, String fileName, boolean hasDefault) {
        super(plugin.getDataFolder() + File.separator + fileName + ".yml");
        this.plugin = plugin;
        this.hasDefault = hasDefault;
        this.fileName = fileName;
        reload();
    }

    public void reload() {
        if (!this.exists()) {
            plugin.getDataFolder().mkdirs();
            try {
                if (hasDefault) {
                    plugin.saveResource(fileName + ".yml", false);
                } else {
                    this.createNewFile();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        loadConfig();
    }

    public void loadConfig() {
        configuration = new YamlConfiguration();
        try {
            configuration.loadFromString(Files.toString(this, Charset.forName("UTF-8")));

        } catch (IOException | InvalidConfigurationException ex) {
            ex.printStackTrace();
        }
    }

    public FileConfiguration getConfig() {
        return configuration;
    }

    public String getString(String key) {
        return configuration.getString(key);
    }

    public double getDouble(String key) {
        return configuration.getDouble(key);
    }

    public void saveConfig() {
        try {
            configuration.save(this);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public File getFile() {
        return this;
    }
}
