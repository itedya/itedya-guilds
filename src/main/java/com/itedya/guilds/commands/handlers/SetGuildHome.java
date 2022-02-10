package com.itedya.guilds.commands.handlers;

import com.itedya.guilds.Guilds;
import com.itedya.guilds.daos.GuildDao;
import com.itedya.guilds.daos.GuildHomeDao;
import com.itedya.guilds.daos.MemberDao;
import com.itedya.guilds.daos.WorldGuardDao;
import com.itedya.guilds.middlewares.PlayerHasPermission;
import com.itedya.guilds.middlewares.PlayerIsInGuild;
import com.itedya.guilds.middlewares.PlayerIsOwnerOfGuild;
import com.itedya.guilds.models.Guild;
import com.itedya.guilds.models.GuildHome;
import com.itedya.guilds.models.Member;
import com.itedya.guilds.utils.ChatUtil;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class SetGuildHome implements CommandHandler {
    private Guilds plugin = Guilds.getPlugin();

    @Override
    public void handle(Player player, String[] args) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> validate(player));
    }

    public void validate(Player player) {
        try {
            var permissionMiddleware = new PlayerHasPermission(player, "itedya-guilds.set-home");
            var guildMiddleware = new PlayerIsInGuild(player);
            var isOwnerMiddleware = new PlayerIsOwnerOfGuild(player);

            guildMiddleware.setNext(isOwnerMiddleware);
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

            Location location = player.getLocation();

            guildHome.setX((int) location.getX());
            guildHome.setY((int) location.getY());
            guildHome.setZ((int) location.getZ());

            World world = Bukkit.getWorld("world");
            assert world != null;

            WorldGuardDao worldGuardDao = new WorldGuardDao(BukkitAdapter.adapt(world));
            ProtectedRegion region = worldGuardDao.getRegion("guild_" + guild.getShortName() + "_" + guild.getId());

            if (! region.contains(BlockVector3.at(location.getX(), location.getY(), location.getZ()))) {
                player.sendMessage(ChatUtil.CHAT_PREFIX + " " + ChatColor.RED + "Ta lokalizacja nie jest w twoim cuboidzie!");
                return;
            }

            guildHomeDao.update(guildHome);

            player.sendMessage(ChatUtil.CHAT_PREFIX + " " + ChatColor.GREEN + "Zaktualizowano lokalizacje domu");
        } catch (Exception e) {
            player.sendMessage(ChatUtil.CHAT_PREFIX + ChatColor.RED + " " + "Wystapil blad serwera! Sprobuj ponownie lub skontaktuj sie z administratorem.");
            plugin.getLogger().log(Level.SEVERE, "Server error!", e);
        }
    }
}
