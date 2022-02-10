package com.itedya.guilds.commands.handlers;

import com.itedya.guilds.Guilds;
import com.itedya.guilds.daos.GuildDao;
import com.itedya.guilds.daos.InvitesDao;
import com.itedya.guilds.daos.MemberDao;
import com.itedya.guilds.dtos.InviteToGuildDto;
import com.itedya.guilds.middlewares.CommandArgumentsAreValid;
import com.itedya.guilds.middlewares.PlayerHasPermission;
import com.itedya.guilds.middlewares.PlayerIsInGuild;
import com.itedya.guilds.middlewares.PlayerIsOwnerOfGuild;
import com.itedya.guilds.models.Invite;
import com.itedya.guilds.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class InviteToGuild implements CommandHandler {
    private final Guilds plugin = Guilds.getPlugin();

    @Override
    public void handle(Player player, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> validate(player, args));
    }

    public void validate(Player player, String[] args) {
        try {
            var dto = InviteToGuildDto.fromCommandArgs(args);

            var permissionMiddleware = new PlayerHasPermission(player, "itedya-guilds.invite");
            var guildMiddleware = new PlayerIsInGuild(player);
            var playerIsOwnerOfGuild = new PlayerIsOwnerOfGuild(player);
            var verifyArgs = new CommandArgumentsAreValid(dto);

            playerIsOwnerOfGuild.setNext(verifyArgs);
            guildMiddleware.setNext(playerIsOwnerOfGuild);
            permissionMiddleware.setNext(guildMiddleware);

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

    public void main(Player player, InviteToGuildDto dto) {
        try {
            MemberDao memberDao = MemberDao.getInstance();
            GuildDao guildDao = GuildDao.getInstance();

            var member = memberDao.getByPlayerUuid(player.getUniqueId().toString());
            var guild = guildDao.getGuildById(member.getGuildId());

            InvitesDao invitesDao = InvitesDao.getInstance();

            Invite invite = new Invite();

            Player invitedPlayer = Bukkit.getPlayer(dto.getPlayerName());

            invite.setGuild(guild);
            invite.setPlayer(invitedPlayer);

            invitesDao.add(invite);

            player.sendMessage(ChatUtil.CHAT_PREFIX + ChatColor.translateAlternateColorCodes('&', " &7Gracz &a? &7zostal zaproszony do gildii!".replace("?", dto.getPlayerName())));
            assert invitedPlayer != null;

            invitedPlayer.sendMessage(ChatUtil.CHAT_PREFIX + ChatColor.translateAlternateColorCodes('&', " &7Dostales zaproszenie do gildii &7[&e?&7] ?! Wpisz &a/g akceptuj &7zeby zaakceptowac."
                    .replace("?", guild.getShortName())
                    .replace("?", guild.getName())));
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Server error!", e);
        }
    }
}
