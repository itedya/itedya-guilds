package com.itedya.guilds.commands.handlers;

import com.itedya.guilds.Guilds;
import com.itedya.guilds.daos.InvitesDao;
import com.itedya.guilds.daos.MemberDao;
import com.itedya.guilds.daos.WorldGuardDao;
import com.itedya.guilds.enums.MemberRole;
import com.itedya.guilds.middlewares.PlayerHasPermission;
import com.itedya.guilds.middlewares.PlayerIsInWorld;
import com.itedya.guilds.middlewares.PlayerIsNotInGuild;
import com.itedya.guilds.models.Invite;
import com.itedya.guilds.models.Member;
import com.itedya.guilds.utils.ChatUtil;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class AcceptInviteToGuild implements CommandHandler {
    private final Guilds plugin = Guilds.getPlugin();

    @Override
    public void handle(Player player, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> validate(player));
    }

    public void validate(Player player) {
        try {
            var permissionMiddleware = new PlayerHasPermission(player, "itedya-guilds.accept");
            var guildMiddleware = new PlayerIsNotInGuild(plugin, player);

            permissionMiddleware.setNext(guildMiddleware);

            var middlewareResult = guildMiddleware.handle();

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

        Member member = null;

        try {
            InvitesDao invitesDao = InvitesDao.getInstance();
            Invite invite = invitesDao.getPlayerInvite(player);

            if (invite == null) {
                player.sendMessage(ChatUtil.CHAT_PREFIX + ChatColor.YELLOW + " " + "Nie masz zaproszenia do gildii!");
                return;
            }

            var guild = invite.getGuild();

            World world = Bukkit.getWorld("world");
            assert world != null;

            WorldGuardDao worldGuardDao = new WorldGuardDao(BukkitAdapter.adapt(world));
            var region = worldGuardDao.getRegion("guild_" + guild.getShortName() + "_" + guild.getId());

            member = new Member();

            member.setGuildId(guild.getId());
            member.setPlayerUuid(player.getUniqueId().toString());
            member.setRole(MemberRole.MEMBER);

            memberDao.add(member);

            region.getMembers().addPlayer(player.getName());

            player.sendMessage(ChatUtil.CHAT_PREFIX + " " + ChatColor.translateAlternateColorCodes('&', "&7Witaj w gildii &7[&e?&7] ?!"
                    .replace("?", guild.getShortName())
                    .replace("?", guild.getName())));
        } catch (Exception e) {
            if (member != null) memberDao.deleteByPlayerUuid(member.getPlayerUuid());
            player.sendMessage(ChatUtil.CHAT_PREFIX + ChatColor.RED + " " + "Wystapil blad serwera! Sprobuj ponownie lub skontaktuj sie z administratorem.");
            plugin.getLogger().log(Level.SEVERE, "Server error!", e);
        }
    }


}
