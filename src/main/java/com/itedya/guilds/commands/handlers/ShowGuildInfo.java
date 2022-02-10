package com.itedya.guilds.commands.handlers;

import com.itedya.guilds.Guilds;
import com.itedya.guilds.daos.GuildDao;
import com.itedya.guilds.daos.MemberDao;
import com.itedya.guilds.dtos.ShowGuildInfoDto;
import com.itedya.guilds.enums.MemberRole;
import com.itedya.guilds.middlewares.CommandArgumentsAreValid;
import com.itedya.guilds.middlewares.PlayerHasPermission;
import com.itedya.guilds.models.Guild;
import com.itedya.guilds.models.Member;
import com.itedya.guilds.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class ShowGuildInfo implements CommandHandler {
    private Guilds plugin = Guilds.getPlugin();

    @Override
    public void handle(Player player, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> validate(player, args));
    }

    public void validate(Player player, String[] args) {
        try {
            var dto = ShowGuildInfoDto.fromCommandArgs(args);

            var permissionMiddleware = new PlayerHasPermission(player, "itedya-guilds.show-info");
            var commandArgsMiddleare = new CommandArgumentsAreValid(dto);

            permissionMiddleware.setNext(commandArgsMiddleare);

            var middlewareResult = permissionMiddleware.handle();

            if (middlewareResult != null) {
                player.sendMessage(ChatUtil.CHAT_PREFIX + " " + middlewareResult);
                return;
            }

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> main(player, dto));
        } catch (Exception e) {
            player.sendMessage(ChatUtil.CHAT_PREFIX + ChatColor.RED + " " + "Wystapil blad serwera! Sprobuj ponownie lub skontaktuj sie z administratorem.");
            plugin.getLogger().log(Level.SEVERE, "Server error!", e);
        }
    }

    public void main(Player player, ShowGuildInfoDto dto) {
        try {
            GuildDao guildDao = GuildDao.getInstance();

            Guild guild = guildDao.getGuildByShortName(dto.getGuildShortName());

            MemberDao memberDao = MemberDao.getInstance();

            List<Member> members = memberDao.getByGuildId(guild.getId());

            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7------------ &6Gildia " + guild.getShortName() + " &7------------"));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Nazwa gildii: &a" + guild.getName()));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Krotka nazwa gildii: &a" + guild.getShortName()));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Czlonkowie: "));

                for (var member : members) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(member.getPlayerUuid()));

                    var message = " &7- &b" + offlinePlayer.getName() + "&7 - ";
                    if (member.getRole().equals(MemberRole.OWNER)) {
                        message += "&6WLASCICIEL";
                    } else {
                        message += "&7CZLONEK";
                    }

                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                }
            });
        } catch (Exception e) {
            player.sendMessage(ChatUtil.CHAT_PREFIX + ChatColor.RED + " " + "Wystapil blad serwera! Sprobuj ponownie lub skontaktuj sie z administratorem.");
            plugin.getLogger().log(Level.SEVERE, "Server error!", e);
        }
    }
}
