package com.itedya.guilds.dtos;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KickOutOfGuildDto extends Dto {
    private Player player;

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String validate() {
        if (getPlayer() == null) {
            return ChatColor.RED + "Musisz podac poprawny nick gracza!";
        }

        return null;
    }

    public static KickOutOfGuildDto fromCommandArgs(String[] args) {
        var dto = new KickOutOfGuildDto();

        if (args.length == 1) {
            dto.setPlayer(Bukkit.getPlayer(args[0]));
        }

        return dto;
    }
}
