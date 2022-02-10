package com.itedya.guilds.listeners;

import com.itedya.guilds.Guilds;
import com.itedya.guilds.daos.PendingMessagesDao;
import com.itedya.guilds.models.MessageItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class OnJoinSendMessage implements Listener {
    public OnJoinSendMessage() {
        Guilds plugin = Guilds.getPlugin();

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        PendingMessagesDao pendingMessagesDao = PendingMessagesDao.getInstance();
        List<MessageItem> messageItems = pendingMessagesDao.getByPlayerUuid(player.getUniqueId().toString());
        if (messageItems != null) {
            messageItems.forEach(item -> player.sendMessage(item.getMessage()));

            pendingMessagesDao.removeByPlayerUuid(player.getUniqueId().toString());
        }
    }
}
