package com.itedya.guilds.commands.handlers;

import com.itedya.guilds.Guilds;
import com.itedya.guilds.daos.GuildDao;
import com.itedya.guilds.daos.MemberDao;
import com.itedya.guilds.daos.WorldGuardDao;
import com.itedya.guilds.middlewares.PlayerHasPermission;
import com.itedya.guilds.middlewares.PlayerIsInGuild;
import com.itedya.guilds.middlewares.PlayerIsNotOwnerOfGuild;
import com.itedya.guilds.middlewares.PlayerIsOwnerOfGuild;
import com.itedya.guilds.models.Guild;
import com.itedya.guilds.models.Member;
import com.itedya.guilds.utils.ChatUtil;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class LeaveGuild implements CommandHandler {
    private Guilds plugin = Guilds.getPlugin();

    @Override
    public void handle(Player player, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> validate(player, args));
    }

    public void validate(Player player, String[] args) {
        try {
            var permissionMiddleware = new PlayerHasPermission(player, "itedya-guilds.leave");
            var guildMiddleware = new PlayerIsInGuild(player);
            var playerIsNotOwner = new PlayerIsNotOwnerOfGuild(player);

            guildMiddleware.setNext(playerIsNotOwner);
            permissionMiddleware.setNext(playerIsNotOwner);

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
        MemberDao memberDao = MemberDao.getInstance();
        GuildDao guildDao = GuildDao.getInstance();

        World world = Bukkit.getWorld("world");
        assert world != null;

        WorldGuardDao worldGuardDao = new WorldGuardDao(BukkitAdapter.adapt(world));

        Member member = memberDao.getByPlayerUuid(player.getUniqueId().toString());
        Guild guild = guildDao.getGuildById(member.getGuildId());

        String regionName = "guild_" + guild.getShortName() + "_" + guild.getId().toString();

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rg removemember -w world " + regionName + " " + player.getName());
        });

        memberDao.deleteByPlayerUuid(player.getUniqueId().toString());

        player.sendMessage(ChatUtil.CHAT_PREFIX + " " + ChatColor.translateAlternateColorCodes('&', "&7Wyszedles z gildii &a" + guild.getShortName() + "&7!"));
    }
}
