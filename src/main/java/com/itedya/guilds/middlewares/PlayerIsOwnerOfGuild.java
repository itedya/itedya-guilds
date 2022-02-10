package com.itedya.guilds.middlewares;

import com.itedya.guilds.daos.MemberDao;
import com.itedya.guilds.enums.MemberRole;
import com.itedya.guilds.models.Member;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlayerIsOwnerOfGuild extends AbstractHandler {
    private final Player player;

    public PlayerIsOwnerOfGuild(Player player) {
        this.player = player;
    }

    @Override
    public String handle() {
        MemberDao memberDao = MemberDao.getInstance();

        Member member = memberDao.getByPlayerUuid(player.getUniqueId().toString());
        assert member != null;

        if (! member.getRole().equals(MemberRole.OWNER)) {
            return ChatColor.RED + "Musisz byc wlascicielem gildii zeby to zrobic!";
        }

        return super.handle();
    }
}
