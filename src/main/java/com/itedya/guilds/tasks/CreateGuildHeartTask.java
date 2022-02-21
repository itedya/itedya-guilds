package com.itedya.guilds.tasks;

import com.itedya.guilds.models.GuildHeart;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class CreateGuildHeartTask extends BukkitRunnable {
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
