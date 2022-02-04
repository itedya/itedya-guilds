package com.itedya.guilds.middlewares;

import com.itedya.guilds.Guilds;
import com.itedya.guilds.daos.MemberDao;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlayerIsNotInGuild extends AbstractHandler {
    private final Player player;

    public PlayerIsNotInGuild(Guilds plugin, Player player) {
        this.player = player;
    }

    @Override
    public String handle() {
        MemberDao memberDao = MemberDao.getInstance();

        var member = memberDao.getByPlayerUuid(player.getUniqueId().toString());
        if (member == null) {
            return super.handle();
        }

        return ChatColor.translateAlternateColorCodes('&', "&eJestes juz w gildii!");
    }
}
