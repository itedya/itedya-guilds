package com.itedya.guilds.daos;

import com.itedya.guilds.Guilds;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigDao {
    private static ConfigDao instance;

    public static ConfigDao getInstance() {
        if (instance == null) instance = new ConfigDao();
        return instance;
    }

    private FileConfiguration configuration;

    private ConfigDao() {
        Guilds guilds = new Guilds();
        guilds.saveDefaultConfig();
        this.configuration = guilds.getConfig();
    }

    public static void reload() {
        instance = new ConfigDao();
    }

    public String getString(String identifier) {
        return configuration.getString(identifier);
    }

    public int getInt(String identifier) {
        return configuration.getInt(identifier);
    }
}
