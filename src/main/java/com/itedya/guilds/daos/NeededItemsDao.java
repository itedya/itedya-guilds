package com.itedya.guilds.daos;

import com.itedya.guilds.models.NeededItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class NeededItemsDao {
    private static NeededItemsDao instance;

    public static NeededItemsDao getInstance() {
        if (instance == null) instance = new NeededItemsDao();
        return instance;
    }

    private final List<NeededItem> neededItems = new CopyOnWriteArrayList<>();

    private NeededItemsDao() {
        var neededItem = new NeededItem();
        neededItem.setMaterial(Material.GOLDEN_APPLE);
        neededItem.setQuantity(16);
        neededItems.add(neededItem);

        neededItem = new NeededItem();
        neededItem.setMaterial(Material.ENDER_PEARL);
        neededItem.setQuantity(16);
        neededItems.add(neededItem);

        neededItem = new NeededItem();
        neededItem.setMaterial(Material.BOOK);
        neededItem.setQuantity(64);
        neededItems.add(neededItem);

        neededItem = new NeededItem();
        neededItem.setMaterial(Material.DIAMOND_BLOCK);
        neededItem.setQuantity(8);
        neededItems.add(neededItem);

        neededItem = new NeededItem();
        neededItem.setMaterial(Material.BROWN_MUSHROOM);
        neededItem.setQuantity(16);
        neededItems.add(neededItem);

        neededItem = new NeededItem();
        neededItem.setMaterial(Material.AMETHYST_BLOCK);
        neededItem.setQuantity(32);
        neededItems.add(neededItem);

        neededItem = new NeededItem();
        neededItem.setMaterial(Material.SLIME_BALL);
        neededItem.setQuantity(16);
        neededItems.add(neededItem);
    }

    public List<NeededItem> getNeededItems(Player player) {
        Inventory inventory = player.getInventory();

        return neededItems.stream().filter(item -> {
            boolean con = inventory.contains(item.material, item.quantity);

            return !con;
        }).toList();
    }

    public void takeItems(Inventory inventory) {
        inventory.removeItemAnySlot(new ItemStack(Material.GOLDEN_APPLE, 16),
                new ItemStack(Material.ENDER_PEARL, 16),
                new ItemStack(Material.BOOK, 64),
                new ItemStack(Material.DIAMOND_BLOCK, 8),
                new ItemStack(Material.BROWN_MUSHROOM, 16),
                new ItemStack(Material.AMETHYST_BLOCK, 32),
                new ItemStack(Material.SLIME_BALL, 16));
    }
}
