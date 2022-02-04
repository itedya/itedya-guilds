package com.itedya.guilds.dtos;

import com.itedya.guilds.daos.GuildDao;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.regex.Pattern;

public class CreateGuildDto extends Dto {

    private String name;

    private String shortName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String validate() {
        Pattern p = Pattern.compile("^[a-zA-Z0-9]{2,6}$");
        if (getShortName() == null || !p.matcher(getShortName()).matches()) {
            return ChatColor.YELLOW + "Krotka nazwa gildii musi miec od 2 do 6 znakow i moze zawierac cyfry lub male i duze litery";
        }

        p = Pattern.compile("^([a-zA-Z0-9 ]){6,64}$");
        if (getName() == null || !p.matcher(getName()).matches()) {
            return ChatColor.YELLOW + "Nazwa gildii musi miec od 6 do 64 znakow i moze zawierac cyfry lub male i duze litery";
        }

        GuildDao dao = GuildDao.getInstance();

        if (dao.getGuildByName(getName()) != null) return ChatColor.YELLOW + "Ta nazwa gildii jest juz zajeta!";
        if (dao.getGuildByShortName(getShortName()) != null) return ChatColor.YELLOW + "Ta krotka nazwa gildii jest zajeta!";

        return null;
    }

    public static CreateGuildDto fromCommandArgs(String[] args) {
        CreateGuildDto dto = new CreateGuildDto();

        if (1 <= args.length) {
            dto.setShortName(args[0]);
        }

        if (2 <= args.length) {
            dto.setName(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
        }

        return dto;
    }
}
