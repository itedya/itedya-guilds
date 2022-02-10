package com.itedya.guilds.dtos;

import com.itedya.guilds.daos.GuildDao;
import org.bukkit.ChatColor;

public class ShowGuildInfoDto extends Dto {
    private String guildShortName;

    public String getGuildShortName() {
        return guildShortName;
    }

    public void setGuildShortName(String guildShortName) {
        this.guildShortName = guildShortName;
    }

    public static ShowGuildInfoDto fromCommandArgs(String[] args) {
        var dto = new ShowGuildInfoDto();

        if (args.length >= 1) {
            dto.setGuildShortName(args[0]);
        }

        return dto;
    }

    public String validate() {
        if (this.getGuildShortName() == null) {
            return ChatColor.RED + "Musisz podac krotka nazwe gildii!";
        }

        GuildDao guildDao = GuildDao.getInstance();

        if (guildDao.getGuildByShortName(getGuildShortName()) == null) {
            return ChatColor.RED + "Gildia nie istnieje!";
        }

        return null;
    }
}
