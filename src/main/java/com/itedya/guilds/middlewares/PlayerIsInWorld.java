package com.itedya.guilds.middlewares;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class PlayerIsInWorld extends AbstractHandler {
    private final Player player;
    private final World world;

    public PlayerIsInWorld(Player player, World world) {
        this.world = world;
        this.player = player;
    }

    @Override
    public String handle() {
        if (! player.getWorld().getName().equals(world.getName())) {
            return ChatColor.YELLOW + "W tym swiecie nie mozesz tego zrobic!";
        }

        return super.handle();
    }
}
