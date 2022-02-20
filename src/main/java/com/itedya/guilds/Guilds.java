package com.itedya.guilds;

import com.itedya.guilds.commands.MainCommand;
import com.itedya.guilds.daos.*;
import com.itedya.guilds.listeners.GuildHeartBreakListener;
import com.itedya.guilds.listeners.OnJoinSendMessage;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class Guilds extends JavaPlugin {
    private static Guilds plugin;

    public static Guilds getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;

        MainCommand.getInstance();

        GuildDao.getInstance();
        GuildHomeDao.getInstance();
        GuildHeartDao.getInstance();
        MemberDao.getInstance();
        PendingMessagesDao.getInstance();

        new GuildHeartBreakListener();
        new OnJoinSendMessage();
    }

    @Override
    public void onDisable() {
        try {
            GuildDao.getInstance().close();
            MemberDao.getInstance().close();
            GuildHomeDao.getInstance().close();
            GuildHeartDao.getInstance().close();
            PendingMessagesDao.getInstance().close();
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Server error", e);
        }
    }
}
