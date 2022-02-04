package com.itedya.guilds;

import com.itedya.guilds.commands.CommandsHandler;
import com.itedya.guilds.daos.*;
import com.itedya.guilds.listeners.GuildHeartBreakListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Level;

public final class Guilds extends JavaPlugin {

    @Override
    public void onEnable() {

        if (this.getDataFolder().mkdir()) {
            this.getLogger().info("Created config data folder");
        } else {
            this.getLogger().info("Config data folder already created");
        }


        this.saveResource("guilds.json", false);
        this.saveResource("members.json", false);
        this.saveResource("homes.json", false);
        this.saveResource("guildhearts.json", false);
        this.saveResource("queue.json", false);
        this.saveResource("queue/announce-guild-heart-break.json", false);
        Objects.requireNonNull(this.getCommand("g")).setExecutor(new CommandsHandler(this));

        this.getServer().getPluginManager().registerEvents(new GuildHeartBreakListener(), this);
    }

    @Override
    public void onDisable() {
        try {
            GuildDao.getInstance().close();
            MemberDao.getInstance().close();
            GuildHomeDao.getInstance().close();
            GuildHeartDao.getInstance().close();
            QueueDao.getInstance().close();
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Server error", e);
        }
    }
}
