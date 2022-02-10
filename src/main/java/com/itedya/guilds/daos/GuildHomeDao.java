package com.itedya.guilds.daos;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.itedya.guilds.Guilds;
import com.itedya.guilds.models.Guild;
import com.itedya.guilds.models.GuildHome;
import com.itedya.guilds.models.Member;
import org.bukkit.Bukkit;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.logging.Level;

public class GuildHomeDao {
    private static GuildHomeDao instance;

    private CopyOnWriteArrayList<GuildHome> data;

    public static GuildHomeDao getInstance() {
        if (instance == null) instance = new GuildHomeDao();
        return instance;
    }

    private final Guilds plugin = Guilds.getPlugin();
    private final Gson gson = new Gson();
    private final Path path = Paths.get(plugin.getDataFolder().getPath(), "homes.json");

    private GuildHomeDao() {
        try {
            plugin.saveResource("homes.json", false);
            FileReader fileReader = new FileReader(path.toString());

            data = new CopyOnWriteArrayList<>(Arrays.asList(gson.fromJson(fileReader, GuildHome[].class)));
            fileReader.close();

            // schedule data saver
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

    public GuildHome getById(int id) {
        return data.stream().filter(item -> item.getId() == id).findFirst().orElse(null);
    }

    public GuildHome add(GuildHome gh) {
        try {
            GuildHome lastItem = Iterables.getLast(data);
            gh.setId(lastItem.getId() + 1);
        } catch (Exception e) {
            gh.setId(1);
        }

        data.add(gh);

        return gh;
    }

    public void delete(int id) {
        if (data.size() == 0) return;
        var home = data.stream().filter(ele -> ele.getId().equals(id)).findFirst().orElse(null);
        if (home == null) return;

        data.remove(home);
    }

    public void update(GuildHome guildHome) {
        int index = Iterables.indexOf(data, g -> g.getId().equals(guildHome.getId()));
        data.set(index, guildHome);
    }
}
