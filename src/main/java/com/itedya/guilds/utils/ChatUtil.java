package com.itedya.guilds.utils;

import com.itedya.guilds.models.Guild;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.util.UUID;

public class ChatUtil {
    public static final String CHAT_PREFIX = ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "ItedyaGuilds" + ChatColor.DARK_GRAY + "]";

    public static @NotNull String prepareGuildHeartBreakMessage(Guild attackerGuild,
                                                                String attackerUUID,
                                                                Guild attackedGuild,
                                                                long expiresAt) {
        StringBuilder messageBuilder = new StringBuilder();

        messageBuilder.append(ChatUtil.CHAT_PREFIX)
                .append(" &7Gildia [&e")
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

        messageBuilder.append(ts).append(". ");

        return ChatColor.translateAlternateColorCodes('&', messageBuilder.toString());
    }
}
