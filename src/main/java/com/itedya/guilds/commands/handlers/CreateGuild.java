package com.itedya.guilds.commands.handlers;

import com.itedya.guilds.Guilds;
import com.itedya.guilds.daos.*;
import com.itedya.guilds.dtos.CreateGuildDto;
import com.itedya.guilds.enums.MemberRole;
import com.itedya.guilds.exceptions.ValidationException;
import com.itedya.guilds.models.Guild;
import com.itedya.guilds.models.GuildHeart;
import com.itedya.guilds.models.GuildHome;
import com.itedya.guilds.models.Member;
import com.itedya.guilds.tasks.CreateGuildHeartTask;
import com.itedya.guilds.utils.ChatUtil;
import com.itedya.guilds.utils.ThreadUtil;
import com.itedya.guilds.utils.ValidationUtil;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.logging.Level;

public class CreateGuild implements CommandHandler {
    @Override
    public void handle(Player player, String[] args) {
        Guilds plugin = Guilds.getPlugin();

        try {
            CreateGuildDto dto = CreateGuildDto.fromCommandArgs(args);

            ValidationUtil.senderHasPermission(player, "itedya-guilds.commands.create");
            ValidationUtil.playerIsNotInGuild(player);
            World world = ValidationUtil.playerIsInWorld(player, Objects.requireNonNull(Bukkit.getWorld("world")));
            ValidationUtil.thereIsEnoughPlaceForCuboid(player.getLocation());

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> createGuild(dto, player, world));
        } catch (ValidationException e) {
            player.sendMessage(ChatUtil.wrapWithPrefix(ChatColor.YELLOW + e.getMessage()));
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Server error!", e);
            player.sendMessage(ChatUtil.wrapWithPrefix("Wystapil blad serwera. Sprobuj jeszcze raz lub skontaktuj sie z administratorem."));
        }
    }

    public void createGuild(CreateGuildDto dto, Player player, World world) {
        GuildDao guildDao = GuildDao.getInstance();
        GuildHomeDao guildHomeDao = GuildHomeDao.getInstance();
        MemberDao memberDao = MemberDao.getInstance();
        WorldGuardDao worldGuardDao = new WorldGuardDao(BukkitAdapter.adapt(world));
        GuildHeartDao guildHeartDao = GuildHeartDao.getInstance();

        GuildHome guildHome = null;
        Guild guild = null;
        Member member = null;
        GuildHeart guildHeart = null;
        ProtectedCuboidRegion region = null;

        try {
            Location position = player.getLocation();

            guildHome = createGuildHome(position);
            guildHeart = createGuildHeart(player);
            guild = createGuild(dto, guildHome, guildHeart);
            member = createMember(player, guild);

            region = new ProtectedCuboidRegion(
                    "guild_" + guild.getShortName() + "_" + guild.getId().toString(),
                    BlockVector3.at(position.getX() + 50, -500, position.getZ() + 50),
                    BlockVector3.at(position.getX() - 50, 500, position.getZ() - 50)
            );

            region.setFlag(Flags.PASSTHROUGH, StateFlag.State.DENY);
            region.setFlag(Flags.GREET_MESSAGE, ChatUtil.CHAT_PREFIX + " " + ChatColor.translateAlternateColorCodes('&', "&7Ten teren nalezy do gildii [&e" + guild.getShortName() + "&7] " + guild.getName() + "!"));
            region.setFlag(Flags.FAREWELL_MESSAGE, ChatUtil.CHAT_PREFIX + " " + ChatColor.translateAlternateColorCodes('&', "&7Wychodzisz z terenu gildii [&e" + guild.getShortName() + "&7] " + guild.getName() + "!"));

            ProtectedCuboidRegion finalRegion = region;
            ThreadUtil.sync(() -> {
                worldGuardDao.add(finalRegion);
                worldGuardDao.addPlayerToRegion(finalRegion, player);
            });

            ThreadUtil.sync(new CreateGuildHeartTask(guildHeart, player.getWorld()));

            NeededItemsDao neededItemsDao = NeededItemsDao.getInstance();
            neededItemsDao.takeItems(player.getInventory());

            Guild finalGuild = guild;
            ThreadUtil.sync(() -> sendResult(player, finalGuild));
        } catch (Exception ex) {
            Guilds.getPlugin().getLogger().log(Level.SEVERE, "Server error!", ex);
            player.sendMessage(ChatColor.RED + "Wystapil blad serwera. Sprobuj jeszcze raz lub skontaktuj sie z administratorem.");

            if (guild != null) guildDao.delete(guild.getId());
            if (guildHome != null) guildHomeDao.delete(guildHome.getId());
            if (member != null) memberDao.deleteByPlayerUuid(member.getPlayerUuid());
            if (region != null) worldGuardDao.delete(region.getId());
            if (guildHeart != null) guildHeartDao.delete(guildHeart.getId());
        }
    }

    private GuildHome createGuildHome(Location position) {
        GuildHomeDao guildHomeDao = GuildHomeDao.getInstance();

        GuildHome guildHome = new GuildHome();

        guildHome.setX((int) position.getX());
        guildHome.setY((int) position.getY());
        guildHome.setZ((int) position.getZ());

        return guildHomeDao.add(guildHome);
    }

    private Guild createGuild(CreateGuildDto dto, GuildHome guildHome, GuildHeart guildHeart) {
        GuildDao guildDao = GuildDao.getInstance();

        Guild guild = new Guild();

        guild.setName(dto.getName());
        guild.setShortName(dto.getShortName());
        guild.setGuildHomeId(guildHome.getId());
        guild.setGuildHeartId(guildHeart.getId());

        return guildDao.add(guild);
    }

    private Member createMember(Player player, Guild guild) {
        MemberDao memberDao = MemberDao.getInstance();

        Member member = new Member();

        member.setGuildId(guild.getId());
        member.setRole(MemberRole.OWNER);
        member.setPlayerUuid(player.getUniqueId().toString());

        return memberDao.add(member);
    }

    private GuildHeart createGuildHeart(Player player) {
        GuildHeartDao guildHeartDao = GuildHeartDao.getInstance();

        Location loc = player.getLocation();

        GuildHeart guildHeart = new GuildHeart();

        guildHeart.setX((int) loc.getX());
        guildHeart.setY((int) loc.getY());
        guildHeart.setZ((int) loc.getZ());

        return guildHeartDao.add(guildHeart);
    }

    public void sendResult(Player player, Guild guild) {
        player.sendMessage(ChatUtil.CHAT_PREFIX + " " + ChatColor.translateAlternateColorCodes('&',
                "&7Stworzyles gildie &7[&e" + guild.getShortName() + "&7] " + guild.getName() + "!"));

        Guilds.getPlugin().getLogger().info("Player ? ? created guild ? [?] ?"
                .replace("?", player.getUniqueId().toString())
                .replace("?", player.getName())
                .replace("?", guild.getId().toString())
                .replace("?", guild.getShortName())
                .replace("?", guild.getName()));
    }
}
