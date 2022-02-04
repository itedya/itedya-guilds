package com.itedya.guilds.daos;

import com.google.common.collect.Iterables;
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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

public class MemberDao {
    private static MemberDao instance;

    public static MemberDao getInstance() {
        if (instance == null) instance = new MemberDao();

        return instance;
    }

    private final Gson gson;

    private List<Member> data;
    private final Path path;

    private MemberDao() {
        // get plugin
        Guilds plugin = (Guilds) Bukkit.getPluginManager().getPlugin("guilds");

        // get path of data
        path = Paths.get(plugin.getDataFolder().getPath(), "members.json");

        // get google json instance
        gson = new Gson();

        try {
            // read data
            FileReader fileReader = new FileReader(path.toString());
            data = new CopyOnWriteArrayList<>();
            data.addAll(Arrays.asList(gson.fromJson(fileReader, Member[].class)));
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

    public Member add(Member member) {
        data.add(member);

        return member;
    }

    public void deleteByPlayerUuid(String playerUuid) {
        if (data.size() == 0) return;
        var member = data.stream().filter(ele -> ele.getPlayerUuid().equals(playerUuid)).findFirst().orElse(null);
        if (member == null) return;

        data.remove(member);
    }

    public void deleteByGuildId(Integer guildId) {
        data.removeIf(ele -> ele.getGuildId().equals(guildId));
    }

    public Member getByPlayerUuid(String playerUuid) {
        return data.stream().filter(item -> item.getPlayerUuid().equals(playerUuid)).findFirst().orElse(null);
    }
}
