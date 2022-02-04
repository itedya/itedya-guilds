package com.itedya.guilds.models;

import org.bukkit.entity.Player;

public class Invite {
    private Player player;
    private Guild guild;

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Guild getGuild() {
        return guild;
    }

    public void setGuild(Guild guild) {
        this.guild = guild;
    }
}
