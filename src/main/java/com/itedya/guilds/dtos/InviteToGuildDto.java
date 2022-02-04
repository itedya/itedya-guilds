package com.itedya.guilds.dtos;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class InviteToGuildDto extends Dto {
    private String playerName;

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String validate() {
        if (getPlayerName() == null) {
            return ChatColor.YELLOW + "Musisz podac gracza!";
        }

        Player player = Bukkit.getPlayer(getPlayerName());

        if (player == null) {
            return ChatColor.YELLOW + "Gracz nie istnieje!";
        }

        return null;
    }

    public static InviteToGuildDto fromCommandArgs(String[] args) {
        var dto = new InviteToGuildDto();

        if (args.length >= 1) {
            dto.setPlayerName(args[0]);
        }

        return dto;
    }
}
