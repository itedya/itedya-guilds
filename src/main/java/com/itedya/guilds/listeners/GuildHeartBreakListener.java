package com.itedya.guilds.listeners;

import com.itedya.guilds.Guilds;
import com.itedya.guilds.daos.GuildDao;
import com.itedya.guilds.daos.GuildHeartDao;
import com.itedya.guilds.daos.MemberDao;
import com.itedya.guilds.daos.QueueDao;
import com.itedya.guilds.enums.BreakerType;
import com.itedya.guilds.models.Guild;
import com.itedya.guilds.models.GuildHeart;
import com.itedya.guilds.models.Member;
import com.itedya.guilds.queueitems.AnnounceGuildHeartBreakToPlayerQueueItem;
import com.itedya.guilds.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

public class GuildHeartBreakListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void BlockBreakEvent(BlockBreakEvent e) {
        // Get block and check if it matches guild-heart block
        Block block = e.getBlock();
        if (block.getType() != Material.REDSTONE_BLOCK) return;

        // Check if guild-heart block is an instance of guild-heart
        GuildHeartDao guildHeartDao = GuildHeartDao.getInstance();

        List<GuildHeart> guildHearts = guildHeartDao.getGuildHearts();

        Location blockLocation = block.getLocation();
        GuildHeart damagedHeart = guildHearts
                .stream()
                .filter((item) -> item.getX() == blockLocation.getX() &&
                        item.getY() == blockLocation.getY() &&
                        blockLocation.getZ() == item.getZ())
                .findFirst()
                .orElse(null);

        if (damagedHeart == null) return;

        Guild guild = GuildDao.getInstance().getGuildByHeartId(damagedHeart.getId());

        if (guild == null) {
            Guilds plugin = (Guilds) Bukkit.getPluginManager().getPlugin("guilds");
            assert plugin != null;

            plugin.getLogger().log(Level.SEVERE, "Heart break ID ? is not assigned to any guild! SOMETHING MESSY IS HAPPENING WITH DATABASE!"
                    .replace("?", damagedHeart.getId().toString()));
            return;
        }

        Player player = e.getPlayer();
        Guild breakerGuild = getBreakerGuild(player);

        if (breakerGuild != null && breakerGuild.getId().equals(guild.getId())) {
            player.sendMessage(ChatUtil.CHAT_PREFIX + " " + ChatColor.GRAY + "Nie mozesz zniszczyc serca wlasnej gildii!");
            e.setCancelled(true);
            return;
        }

        QueueDao queueDao = QueueDao.getInstance();

        Timestamp ts = new Timestamp(new Date().getTime());
        long expiresAt = ts.getTime() + 60 * 60 * 24 * 1000;

        Bukkit.getWhitelistedPlayers().forEach(ele -> {
            queueDao.add(new AnnounceGuildHeartBreakToPlayerQueueItem(breakerGuild, player.getUniqueId().toString(), guild, expiresAt, ele.getUniqueId().toString()));
        });

        e.setCancelled(true);
    }

    public Guild getBreakerGuild(Player player) {
        Member member = MemberDao.getInstance().getByPlayerUuid(player.getUniqueId().toString());
        if (member == null) return null;

        return GuildDao.getInstance().getGuildById(member.getGuildId());
    }
}

