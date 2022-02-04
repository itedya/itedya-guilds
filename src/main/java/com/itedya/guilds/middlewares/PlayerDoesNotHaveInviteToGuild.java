package com.itedya.guilds.middlewares;

import com.itedya.guilds.daos.InvitesDao;
import com.itedya.guilds.models.Invite;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlayerDoesNotHaveInviteToGuild extends AbstractHandler {
    private final Player player;

    public PlayerDoesNotHaveInviteToGuild(Player player) {
        this.player = player;
    }

    @Override
    public String handle() {
        InvitesDao invitesDao = InvitesDao.getInstance();

        Invite invite = invitesDao.getPlayerInvite(player);
        if (invite != null) {
            return ChatColor.YELLOW + "Gracz juz otrzymal zaproszenie do gildii. Poczekaj 60s na przedawnienie sie zaproszenia.";
        }

        return super.handle();
    }
}
