package com.itedya.guilds.commands.handlers.admin;

import com.itedya.guilds.Guilds;
import com.itedya.guilds.commands.handlers.CommandHandler;
import com.itedya.guilds.middlewares.PlayerHasPermission;
import com.itedya.guilds.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Level;

public class GiveGuildItemsToPlayer implements CommandHandler {
    private Guilds plugin = Guilds.getPlugin();

    @Override
    public void handle(Player player, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> validate(player));
    }

    public void validate(Player player) {
        try {
            var permissionMiddleware = new PlayerHasPermission(player, "itedya-guilds.admin.give-items");

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
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            Inventory inv = player.getInventory();

            inv.addItem(new ItemStack(Material.GOLDEN_APPLE, 16));
            inv.addItem(new ItemStack(Material.ENDER_PEARL, 16));
            inv.addItem(new ItemStack(Material.BOOK, 64));
            inv.addItem(new ItemStack(Material.DIAMOND_BLOCK, 8));
            inv.addItem(new ItemStack(Material.BROWN_MUSHROOM, 16));
            inv.addItem(new ItemStack(Material.AMETHYST_BLOCK, 32));
            inv.addItem(new ItemStack(Material.SLIME_BALL, 16));
        });
    }
}
