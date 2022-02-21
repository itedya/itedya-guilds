package com.itedya.guilds.utils;

import com.itedya.guilds.Guilds;
import org.bukkit.Bukkit;

public class ThreadUtil {
    public static void sync(Runnable runnable) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Guilds.getPlugin(), runnable);
    }

    public static void async(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(Guilds.getPlugin(), runnable);
    }
}
