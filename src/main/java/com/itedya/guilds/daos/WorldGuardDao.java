package com.itedya.guilds.daos;

import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.entity.Player;

public class WorldGuardDao {
    private final World world;

    public WorldGuardDao(World world) {
        this.world = world;
    }

    public void add(ProtectedCuboidRegion region) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager manager = container.get(world);
        assert manager != null : "World manager is null";

        manager.addRegion(region);
    }

    public boolean doesCuboidIntersect(ProtectedCuboidRegion cuboid) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager manager = container.get(world);
        assert manager != null : "World manager is null";
        var intersectingRegions = cuboid.getIntersectingRegions(manager.getRegions().values());

        return intersectingRegions.size() != 0;
    }

    public ProtectedRegion getRegion(String name) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager manager = container.get(world);
        assert manager != null : "World manager is null";

        return manager.getRegion(name);
    }

    public void delete(String regionName) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager manager = container.get(world);
        assert manager != null;
        manager.removeRegion(regionName);
    }

    public void addPlayerToRegion(ProtectedCuboidRegion region, Player player) {
        region.getMembers().addPlayer(player.getUniqueId());
    }

    public void removePlayerFromRegion(ProtectedCuboidRegion region, Player player) {
        region.getMembers().removePlayer(player.getUniqueId());
    }
}














