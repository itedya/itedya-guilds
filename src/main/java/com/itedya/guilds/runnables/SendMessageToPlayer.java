package com.itedya.guilds.runnables;

import com.itedya.guilds.Guilds;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Level;

// BukkitRunnable for sync execution (thread-safety things)
public class SendMessageToPlayer extends BukkitRunnable {
    private final Guilds plugin;
    private final Player player;
    private final String message;

    public SendMessageToPlayer(Guilds plugin, Player player, String message) {
        this.player = player;
        this.message = message;
        this.plugin = plugin;
    }

    @Override
    public void run() {
        player.sendMessage(message);
        plugin.getLogger().log(Level.INFO, "Sent message to player: " + message);
    }
}
