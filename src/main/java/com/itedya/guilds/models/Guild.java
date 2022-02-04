package com.itedya.guilds.models;

import java.util.Date;

public class Guild {
    private Integer id;

    private String name;

    private String shortName;

    private Integer guildHomeId;

    private Integer guildHeartId;

    public Integer getGuildHeartId() {
        return guildHeartId;
    }

    public void setGuildHeartId(Integer guildHeartId) {
        this.guildHeartId = guildHeartId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public Integer getGuildHomeId() {
        return guildHomeId;
    }

    public void setGuildHomeId(Integer guildHomeId) {
        this.guildHomeId = guildHomeId;
    }

}
