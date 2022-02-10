package com.itedya.guilds.commands.handlers;

import com.itedya.guilds.Guilds;
import com.itedya.guilds.daos.GuildDao;
import com.itedya.guilds.daos.MemberDao;
import com.itedya.guilds.daos.WorldGuardDao;
import com.itedya.guilds.dtos.KickOutOfGuildDto;
import com.itedya.guilds.middlewares.*;
import com.itedya.guilds.utils.ChatUtil;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.logging.Level;

public class KickOutOfGuild implements CommandHandler {
    private final Guilds plugin = Guilds.getPlugin();

    @Override
    public void handle(Player player, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> validate(player, args));
    }

    public void validate(Player player, String[] args) {
        try {
            var dto = KickOutOfGuildDto.fromCommandArgs(args);

            var permissionMiddleware = new PlayerHasPermission(player, "itedya-guilds.kick-out");
            var guildMiddleware = new PlayerIsInGuild(player);
            var playerIsOwnerOfGuild = new PlayerIsOwnerOfGuild(player);
            var commandArgsMiddleware = new CommandArgumentsAreValid(dto);

            playerIsOwnerOfGuild.setNext(commandArgsMiddleware);
            guildMiddleware.setNext(playerIsOwnerOfGuild);
            permissionMiddleware.setNext(guildMiddleware);

            var middlewareResult = permissionMiddleware.handle();

            if (middlewareResult != null) {
                player.sendMessage(ChatUtil.CHAT_PREFIX + " " + middlewareResult);
                return;
            }

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> main(player, dto));
        } catch (Exception e) {
            player.sendMessage(ChatUtil.CHAT_PREFIX + ChatColor.RED + " " + "Wystapil blad serwera! Sprobuj ponownie lub skontaktuj sie z administratorem.");
            plugin.getLogger().log(Level.SEVERE, "Server error!", e);
        }
    }

    public void main(Player player, KickOutOfGuildDto dto) {
        MemberDao memberDao = MemberDao.getInstance();
        GuildDao guildDao = GuildDao.getInstance();

        var requestingMember = memberDao.getByPlayerUuid(player.getUniqueId().toString());
        var memberToKick = memberDao.getByPlayerUuid(dto.getPlayer().getUniqueId().toString());

        if (memberToKick == null || !Objects.equals(requestingMember.getGuildId(), memberToKick.getGuildId())) {
            player.sendMessage(ChatUtil.CHAT_PREFIX + " " + ChatColor.RED + "Ten gracz nie jest w twojej gildii!");
            return;
        }

        var guild = guildDao.getGuildById(requestingMember.getGuildId());

        try {
            World world = Bukkit.getWorld("world");
            assert world != null;

            WorldGuardDao worldGuardDao = new WorldGuardDao(BukkitAdapter.adapt(world));

            String regionName = "guild_" + guild.getShortName() + "_" + guild.getId();

            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rg removemember -w world " + regionName + " " + dto.getPlayer().getName());
            });

            memberDao.deleteByPlayerUuid(dto.getPlayer().getUniqueId().toString());

            player.sendMessage(ChatUtil.CHAT_PREFIX + " " + ChatColor.GRAY + "Wyrzuciles gracza " + ChatColor.GREEN + dto.getPlayer().getName() + "!");
        } catch (Exception e) {
            player.sendMessage(ChatUtil.CHAT_PREFIX + ChatColor.RED + " " + "Wystapil blad serwera! Sprobuj ponownie lub skontaktuj sie z administratorem.");
            plugin.getLogger().log(Level.SEVERE, "Server error!", e);
        }
    }
}
