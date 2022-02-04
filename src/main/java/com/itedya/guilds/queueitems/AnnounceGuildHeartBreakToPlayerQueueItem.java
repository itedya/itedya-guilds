package com.itedya.guilds.queueitems;

import com.itedya.guilds.enums.QueueItemResult;
import com.itedya.guilds.models.Guild;
import com.itedya.guilds.models.QueueItem;
import com.itedya.guilds.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class AnnounceGuildHeartBreakToPlayerQueueItem extends QueueItem {
    private final Guild attackerGuild;
    private final String attackerUUID;
    private final Guild attackedGuild;
    private final long expiresAt;
    private final String announceToPlayerUUID;

    public AnnounceGuildHeartBreakToPlayerQueueItem(
            Guild attackerGuild,
            String attackerUUID,
            Guild attackedGuild,
            long expiresAt,
            String announceToPlayerUUID
    ) {
        this.attackedGuild = attackedGuild;
        this.attackerUUID = attackerUUID;
        this.attackerGuild = attackerGuild;
        this.expiresAt = expiresAt;
        this.announceToPlayerUUID = announceToPlayerUUID;
    }

    @Override
    public QueueItemResult execute() {
        if (System.currentTimeMillis() >= expiresAt) {
            return QueueItemResult.EXPIRED;
        }

        if (!canExecute()) return QueueItemResult.EXCEPTION;

        StringBuilder messageBuilder = new StringBuilder()
                .append(ChatUtil.CHAT_PREFIX)
                .append(" ")
                .append("&7Gildia [&e")
                .append(attackedGuild.getShortName())
                .append("&7] ")
                .append(attackedGuild.getName())
                .append(" ")
                .append("zostala podbita przez ");

        if (attackerGuild == null) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(attackerUUID));
            messageBuilder.append("gracza &a").append(player.getName()).append("&7 ");
        } else {
            messageBuilder.append("gildie [&e").append(attackerGuild.getShortName()).append("&7] ").append(attackerGuild.getName()).append(". ");
        }

        messageBuilder.append("Gildia ma czas na odbudowe do ");

        Timestamp ts = new Timestamp(expiresAt);

        messageBuilder.append((Date) ts).append(". ");

        Player player = Bukkit.getPlayer(UUID.fromString(announceToPlayerUUID));
        assert player != null;

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', messageBuilder.toString()));

        return super.execute();
    }

    public boolean canExecute() {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(announceToPlayerUUID));
        return offlinePlayer.isOnline();
    }
}
