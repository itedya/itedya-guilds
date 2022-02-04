package com.itedya.guilds.commands.handlers;

import com.itedya.guilds.Guilds;
import com.itedya.guilds.daos.*;
import com.itedya.guilds.middlewares.PlayerHasPermission;
import com.itedya.guilds.middlewares.PlayerIsInGuild;
import com.itedya.guilds.models.Guild;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Level;

public class DeleteGuild implements CommandHandler {
    private final Guilds plugin;

    public DeleteGuild(Guilds plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(Player player, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> validate(player));
    }

    public void validate(Player player) {
        try {
            var permissionMiddleware = new PlayerHasPermission(player, "itedya-guilds.delete");
            var guildMiddleware = new PlayerIsInGuild(player);
            permissionMiddleware.setNext(guildMiddleware);

            var middlewareResult = permissionMiddleware.handle();

            if (middlewareResult != null) {
                player.sendMessage(middlewareResult);
                return;
            }

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> deleteGuild(player));
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Server error!", e);
            player.sendMessage("Wystapil blad serwera, skontaktuj sie z administratorem.");
        }
    }

    public void deleteGuild(Player player) {
        try {
            MemberDao memberDao = MemberDao.getInstance();
            GuildDao guildDao = GuildDao.getInstance();
            GuildHomeDao guildHomeDao = GuildHomeDao.getInstance();
            GuildHeartDao guildHeartDao = GuildHeartDao.getInstance();
            WorldGuardDao worldGuardDao = new WorldGuardDao(BukkitAdapter.adapt(player.getWorld()));

            var member = memberDao.getByPlayerUuid(player.getUniqueId().toString());
            var guild = guildDao.getGuildById(member.getGuildId());

            memberDao.deleteByGuildId(member.getGuildId());
            guildHomeDao.delete(guild.getGuildHomeId());
            guildHeartDao.delete(guild.getGuildHeartId());
            guildDao.delete(member.getGuildId());

            worldGuardDao.delete("guild_" + guild.getShortName() + "_" + guild.getId().toString());

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> sendResult(player, guild));
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Server error!", e);
            player.sendMessage("Wystapil blad serwera, skontaktuj sie z administratorem.");
        }
    }

    public void sendResult(Player player, Guild guild) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&7Usunales gildie [&e?&7] ?!"
                        .replace("?", guild.getShortName())
                        .replace("?", guild.getName())));

        plugin.getLogger().info("Player ? ? deleted guild ? [?] ?"
                .replace("?", player.getUniqueId().toString())
                .replace("?", player.getName())
                .replace("?", guild.getId().toString())
                .replace("?", guild.getShortName())
                .replace("?", guild.getName()));
    }

}
