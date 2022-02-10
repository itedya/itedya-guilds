package com.itedya.guilds.commands.handlers;

import com.itedya.guilds.Guilds;
import com.itedya.guilds.daos.GuildDao;
import com.itedya.guilds.daos.GuildHomeDao;
import com.itedya.guilds.daos.MemberDao;
import com.itedya.guilds.middlewares.PlayerHasPermission;
import com.itedya.guilds.middlewares.PlayerIsInGuild;
import com.itedya.guilds.models.Guild;
import com.itedya.guilds.models.GuildHome;
import com.itedya.guilds.models.Member;
import com.itedya.guilds.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class TeleportToHome implements CommandHandler {
    private final Guilds plugin = Guilds.getPlugin();

    @Override
    public void handle(Player player, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> validate(player));
    }

    public void validate(Player player) {
        try {
            var permissionMiddleware = new PlayerHasPermission(player, "itedya-guilds.teleport");
            var guildMiddleware = new PlayerIsInGuild(player);

            permissionMiddleware.setNext(guildMiddleware);

            var middlewareResult = permissionMiddleware.handle();

            if (middlewareResult != null) {
                player.sendMessage(ChatUtil.CHAT_PREFIX + " " + middlewareResult);
                return;
            }

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> main(player));
        } catch (Exception e) {
            player.sendMessage(ChatUtil.CHAT_PREFIX + ChatColor.RED + " " + "Wystapil blad serwera! Sprobuj ponownie lub skontaktuj sie z administratorem.");
            plugin.getLogger().log(Level.SEVERE, "Server error!", e);
        }
    }

    public void main(Player player) {
        try {
            MemberDao memberDao = MemberDao.getInstance();
            Member member = memberDao.getByPlayerUuid(player.getUniqueId().toString());

            GuildDao guildDao = GuildDao.getInstance();
            Guild guild = guildDao.getGuildById(member.getGuildId());

            GuildHomeDao guildHomeDao = GuildHomeDao.getInstance();
            GuildHome guildHome = guildHomeDao.getById(guild.getGuildHomeId());

            Location locationBeforeTimer = player.getLocation();

            player.sendMessage(ChatUtil.CHAT_PREFIX + " " + ChatColor.GRAY + "Teleportacja nastapi za 5 sekund, nie ruszaj sie...");

            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                Location locationAfterTimer = player.getLocation();

                if (! locationBeforeTimer.equals(locationAfterTimer)) {
                    player.sendMessage(ChatUtil.CHAT_PREFIX + " " + ChatColor.RED + "Ruszyles sie! Teleportacja anulowana!");
                    return;
                }

                teleport(player, guildHome);
                player.sendMessage(ChatUtil.CHAT_PREFIX + " " + ChatColor.GREEN + "Teleportacja...");

            }, 20 * 5);
        } catch (Exception e) {
            player.sendMessage(ChatUtil.CHAT_PREFIX + ChatColor.RED + " " + "Wystapil blad serwera! Sprobuj ponownie lub skontaktuj sie z administratorem.");
            plugin.getLogger().log(Level.SEVERE, "Server error!", e);
        }
    }

    public void teleport(Player player, GuildHome guildHome) {
        World world = Bukkit.getWorld("world");

        Location location = new Location(world, guildHome.getX(), guildHome.getY(), guildHome.getZ());

        player.teleport(location);
    }
}
