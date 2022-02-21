package com.itedya.guilds.utils;

import com.itedya.guilds.daos.MemberDao;
import com.itedya.guilds.daos.WorldGuardDao;
import com.itedya.guilds.enums.MemberRole;
import com.itedya.guilds.exceptions.ValidationException;
import com.itedya.guilds.models.Member;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class ValidationUtil {
    public static Player senderIsPlayer(CommandSender sender) throws ValidationException {
        if (!(sender instanceof Player player)) {
            throw new ValidationException("Musisz byc w grze zeby uzywac tej komendy!");
        }

        return player;
    }

    public static void senderHasPermission(CommandSender sender, String permission) throws ValidationException {
        if (!(sender.hasPermission(permission))) {
            throw new ValidationException("Brak permisji");
        }
    }

    public static void playerIsNotInGuild(Player player) throws ValidationException {
        MemberDao memberDao = MemberDao.getInstance();

        Member member = memberDao.getByPlayerUuid(player.getUniqueId().toString());

        if (member != null) {
            throw new ValidationException("Jestes juz w gildii!");
        }
    }

    public static void playerIsInGuild(Player player) throws ValidationException {
        MemberDao memberDao = MemberDao.getInstance();

        Member member = memberDao.getByPlayerUuid(player.getUniqueId().toString());

        if (member == null) {
            throw new ValidationException("Musisz byc w gildii!");
        }
    }

    public static void playerIsOwnerOfGuild(Player player) throws ValidationException {
        MemberDao memberDao = MemberDao.getInstance();

        Member member = memberDao.getByPlayerUuid(player.getUniqueId().toString());

        if (member.getRole() != MemberRole.OWNER) {
            throw new ValidationException("Musisz byc wlascicielem gildii zeby to zrobic!");
        }
    }

    public static World playerIsInWorld(Player player, World world) throws ValidationException {
        World playersWorld = player.getWorld();

        if (!world.getName().equals(playersWorld.getName())) {
            throw new ValidationException("Nie jestes w dobrym swiecie!");
        }

        return world;
    }

    public static void thereIsEnoughPlaceForCuboid(Location location) throws ValidationException {
        WorldGuardDao worldGuardDao = new WorldGuardDao(BukkitAdapter.adapt(Objects.requireNonNull(Bukkit.getWorld("world"))));

        ProtectedCuboidRegion region = new ProtectedCuboidRegion(
                "itedya_guilds_cuboid_test",
                BlockVector3.at(location.getX() + 50, -500, location.getZ() + 50),
                BlockVector3.at(location.getX() - 50, 500, location.getZ() - 50)
        );

        if (worldGuardDao.doesCuboidIntersect(region)) {
            throw new ValidationException("Jakis cuboid juz istnieje na tym terenie!");
        }
    }
}
