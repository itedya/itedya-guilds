package com.itedya.guilds.daos;

import com.itedya.guilds.Guilds;
import com.itedya.guilds.models.Invite;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InvitesDao {
    public static InvitesDao instance;

    public static InvitesDao getInstance() {
        if (instance == null) instance = new InvitesDao();
        return instance;
    }

    private final List<Invite> data = new CopyOnWriteArrayList<>();

    public void add(Invite invite) {
        data.add(invite);

        Guilds plugin = (Guilds) Bukkit.getPluginManager().getPlugin("guilds");
        assert plugin != null;

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> data.remove(invite), 20 * 60);
    }

    public Invite getPlayerInvite(Player player) {
        return data.stream().filter(ele -> ele.getPlayer().getUniqueId().toString().equals(player.getUniqueId().toString())).findFirst().orElse(null);
    }
}
