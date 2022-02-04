package com.itedya.guilds.middlewares;

import com.itedya.guilds.daos.NeededItemsDao;
import com.itedya.guilds.utils.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlayerHaveNeededItem extends AbstractHandler {
    private final Player player;

    public PlayerHaveNeededItem(Player player) {
        this.player = player;
    }

    @Override
    public String handle() {
        NeededItemsDao neededItemsDao = NeededItemsDao.getInstance();

        if (neededItemsDao.getNeededItems(player).size() != 0) {
            return ChatUtil.CHAT_PREFIX + " " + ChatColor.translateAlternateColorCodes('&', "&cBrakuje ci itemkow na gildie!");
        }

        return super.handle();
    }
}
