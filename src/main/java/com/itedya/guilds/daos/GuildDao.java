package com.itedya.guilds.daos;

import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.itedya.guilds.Guilds;
import com.itedya.guilds.models.Guild;
import org.bukkit.Bukkit;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

public class GuildDao {
    private static GuildDao instance;

    public static GuildDao getInstance() {
        if (instance == null) {
            instance = new GuildDao();
        }

        return instance;
    }

    private final Guilds plugin = Guilds.getPlugin();
    private final Gson gson = new Gson();
    private final Path path = Paths.get(plugin.getDataFolder().getPath(), "guilds.json");

    private CopyOnWriteArrayList<Guild> data;

    private GuildDao() {
        try {
            plugin.saveResource("guilds.json", false);

            // read data
            FileReader fileReader = new FileReader(path.toString());
            data = new CopyOnWriteArrayList<>();
            data.addAll(Arrays.asList(gson.fromJson(fileReader, Guild[].class)));
            fileReader.close();

            Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                try {
                    saveData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }, 20 * 60, 20 * 60);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Server error!", e);
        }
    }

    public void close() throws IOException {
        saveData();
    }

    private void saveData() throws IOException {
        FileWriter writer = new FileWriter(path.toString(), false);
        gson.toJson(data, writer);
        writer.flush();
        writer.close();
    }

    public Guild add(Guild guild) {
        try {
            Guild lastItem = data.get(data.size() - 1);
            guild.setId(lastItem.getId() + 1);
        } catch (Exception e) {
            guild.setId(1);
        }

        data.add(guild);

        return guild;
    }

    public void delete(int id) {
        if (data.size() == 0) return;
        var guild = data.stream().filter(ele -> ele.getId().equals(id)).findFirst().orElse(null);
        if (guild == null) return;

        data.remove(guild);
    }

    public void update(Guild guild) {
        int index = Iterables.indexOf(data, g -> g.getId().equals(guild.getId()));
        if (index == -1) return;

        data.set(index, guild);
    }

    public Guild getGuildById(int id) {
        return data.stream().filter((item) -> item.getId() == id).findFirst().orElse(null);
    }

    public Guild getGuildByName(String name) {
        return data.stream().filter((item) -> item.getName().equals(name)).findFirst().orElse(null);
    }

    public Guild getGuildByShortName(String name) {
        return data.stream().filter((item) -> item.getShortName().equals(name)).findFirst().orElse(null);
    }

    public Guild getGuildByHeartId(Integer id) {
        return data.stream().filter(item -> item.getGuildHeartId().equals(id)).findFirst().orElse(null);
    }
}
