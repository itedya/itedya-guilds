package com.itedya.guilds.models;

public class GuildHeart {
    public Integer id;
    public Integer x;
    public Integer y;
    public Integer z;
    public String worldName;

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getZ() {
        return z;
    }

    public Integer getY() {
        return y;
    }

    public Integer getX() {
        return x;
    }

    public void setZ(Integer z) {
        this.z = z;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public void setX(Integer x) {
        this.x = x;
    }
}
