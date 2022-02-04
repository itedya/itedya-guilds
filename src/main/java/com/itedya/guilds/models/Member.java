package com.itedya.guilds.models;

import com.itedya.guilds.enums.MemberRole;

import java.util.Date;
import java.util.UUID;

public class Member {
    private String playerUuid;
    private Integer guildId;
    private MemberRole role;

    public String getPlayerUuid() {
        return playerUuid;
    }

    public void setPlayerUuid(String playerUuid) {
        this.playerUuid = playerUuid;
    }

    public Integer getGuildId() {
        return guildId;
    }

    public void setGuildId(Integer guildId) {
        this.guildId = guildId;
    }

    public MemberRole getRole() {
        return role;
    }

    public void setRole(MemberRole role) {
        this.role = role;
    }
}
