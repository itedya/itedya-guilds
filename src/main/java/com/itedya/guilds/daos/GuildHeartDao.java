package com.itedya.guilds.daos;

import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.itedya.guilds.Guilds;
import com.itedya.guilds.models.Guild;
import com.itedya.guilds.models.GuildHeart;
import org.bukkit.Bukkit;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

public class GuildHeartDao {
    // singleton stuff
    private static GuildHeartDao instance;

    /**
     * Get instance of GuildHeartDao
     *
     * @return GuildHeartDao
     */
    public static GuildHeartDao getInstance() {
        if (instance == null) instance = new GuildHeartDao();

        return instance;
    }

    // dao stuff
    private final Path path;
    private Gson gson;
    private CopyOnWriteArrayList<GuildHeart> data;

    private GuildHeartDao() {
        // Get plugin instance
        Guilds plugin = (Guilds) Bukkit.getPluginManager().getPlugin("guilds");
        assert plugin != null;

        // Get path of database file
        path = Paths.get(plugin.getDataFolder().getPath(), "guildhearts.json");

        try {
            // Assign data from file to Array
            FileReader fileReader = new FileReader(path.toString());
            gson = new Gson();

            data = new CopyOnWriteArrayList<>(gson.fromJson(fileReader, GuildHeart[].class));

            fileReader.close();

            Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                try {
                    saveData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }, 20 * 60, 20 * 60);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Server error!", e);
        }
    }

    public void close() throws IOException {
        saveData();
    }

    public void saveData() throws IOException {
        FileWriter fileWriter = new FileWriter(path.toString(), false);
        gson.toJson(data, fileWriter);
        fileWriter.close();
    }

    public GuildHeart add(GuildHeart dto) {
        try {
            try {
                GuildHeart lastItem = data.get(data.size() - 1);
                dto.setId(lastItem.getId() + 1);
            } catch (Exception e) {
                dto.setId(1);
            }

            data.add(dto);

            return dto;
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void delete(int id) {
        if (data.size() == 0) return;
        GuildHeart guildHeart = data.stream().filter(ele -> ele.getId().equals(id)).findFirst().orElse(null);
        if (guildHeart == null) return;

        data.remove(guildHeart);
    }

    public List<GuildHeart> getGuildHearts() {
        return data;
    }
}
