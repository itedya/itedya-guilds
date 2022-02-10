package com.itedya.guilds.commands.handlers;

import com.itedya.guilds.Guilds;
import com.itedya.guilds.daos.*;
import com.itedya.guilds.dtos.CreateGuildDto;
import com.itedya.guilds.enums.MemberRole;
import com.itedya.guilds.middlewares.*;
import com.itedya.guilds.models.Guild;
import com.itedya.guilds.models.GuildHeart;
import com.itedya.guilds.models.GuildHome;
import com.itedya.guilds.models.Member;
import com.itedya.guilds.utils.ChatUtil;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Level;

public class CreateGuild implements CommandHandler {
    private final Guilds plugin = Guilds.getPlugin();

    @Override
    public void handle(Player player, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> validate(player, args));
    }

    public void validate(Player player, String[] args) {
        try {
            CreateGuildDto dto = CreateGuildDto.fromCommandArgs(args);

            PlayerHasPermission permissionMiddleware = new PlayerHasPermission(player, "itedya-guilds.create");
            var guildMiddleware = new PlayerIsNotInGuild(plugin, player);
            var worldMiddleware = new PlayerIsInWorld(player, Bukkit.getWorld("world"));
            var commandMiddleware = new CommandArgumentsAreValid(dto);
            var neededItemsMiddleware = new PlayerHaveNeededItem(player);

            commandMiddleware.setNext(neededItemsMiddleware);
            worldMiddleware.setNext(commandMiddleware);
            guildMiddleware.setNext(worldMiddleware);
            permissionMiddleware.setNext(guildMiddleware);

            var middlewareResult = permissionMiddleware.handle();

            if (middlewareResult != null) {
                player.sendMessage(middlewareResult);
                return;
            }

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> createGuild(player, dto));
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Server error!", e);
            player.sendMessage(ChatColor.RED + "Wystapil blad serwera. Sprobuj jeszcze raz lub skontaktuj sie z administratorem.");
        }
    }

    public void createGuild(Player player, CreateGuildDto dto) {
        GuildDao guildDao = GuildDao.getInstance();
        GuildHomeDao guildHomeDao = GuildHomeDao.getInstance();
        MemberDao memberDao = MemberDao.getInstance();
        WorldGuardDao worldGuardDao = new WorldGuardDao(BukkitAdapter.adapt(player.getWorld()));
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

            if (worldGuardDao.doesCuboidIntersect(region)) {
                player.sendMessage(ChatColor.YELLOW + "Jakis cuboid juz istnieje na tym terenie (cuboid jest rozmiarow 100x100)!");
                memberDao.deleteByGuildId(guild.getId());
                guildHomeDao.delete(guildHome.getId());
                guildHeartDao.delete(guildHeart.getId());
                guildDao.delete(guild.getId());
                return;
            }

            region.setFlag(Flags.CHEST_ACCESS, StateFlag.State.ALLOW);
            region.setFlag(Flags.TNT, StateFlag.State.ALLOW);
            region.setFlag(Flags.PASSTHROUGH, StateFlag.State.DENY);

            region.setFlag(Flags.GREET_MESSAGE, ChatUtil.CHAT_PREFIX + " " + ChatColor.translateAlternateColorCodes('&', "&7Ten teren nalezy do gildii [&e" + guild.getShortName() + "&7] " + guild.getName() + "!"));

            region.setFlag(Flags.FAREWELL_MESSAGE, ChatUtil.CHAT_PREFIX + " " + ChatColor.translateAlternateColorCodes('&', "&7Wychodzisz z terenu gildii [&e" + guild.getShortName() + "&7] " + guild.getName() + "!"));

            worldGuardDao.add(region);
            worldGuardDao.addPlayerToRegion(region, player);

            new CreateGuildHeartTask(guildHeart, player.getWorld()).runTask(plugin);

            NeededItemsDao neededItemsDao = NeededItemsDao.getInstance();
            neededItemsDao.takeItems(player.getInventory());

            Guild finalGuild = guild;
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> sendResult(player, finalGuild));
        } catch (Exception ex) {
            plugin.getLogger().log(Level.SEVERE, "Server error!", ex);
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

        plugin.getLogger().info("Player ? ? created guild ? [?] ?"
                .replace("?", player.getUniqueId().toString())
                .replace("?", player.getName())
                .replace("?", guild.getId().toString())
                .replace("?", guild.getShortName())
                .replace("?", guild.getName()));
    }
}

class CreateGuildHeartTask extends BukkitRunnable {
    private final GuildHeart guildHeart;
    private final World world;

    public CreateGuildHeartTask(GuildHeart guildHeart, World world) {
        this.world = world;
        this.guildHeart = guildHeart;
    }

    @Override
    public void run() {
        Location loc = new Location(world, guildHeart.getX(), guildHeart.getY(), guildHeart.getZ());

        world.getBlockAt(loc).setType(Material.REDSTONE_BLOCK);
    }
}