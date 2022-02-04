package com.itedya.guilds.commands;

import com.itedya.guilds.Guilds;
import com.itedya.guilds.commands.handlers.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CommandsHandler implements CommandExecutor {
    private final Map<String, CommandHandler> commandHandlers = new HashMap<>();

    public CommandsHandler(Guilds plugin) {
        commandHandlers.put("stworz", new CreateGuild(plugin));
        commandHandlers.put("usun", new DeleteGuild(plugin));
        commandHandlers.put("zapros", new InviteToGuild(plugin));
        commandHandlers.put("akceptuj", new AcceptInviteToGuild(plugin));
        commandHandlers.put("pomoc", new SendHelp(plugin));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("You have to be in game to send commands to this plugin!");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Zle uzycie komendy!");
            player.sendMessage(ChatColor.YELLOW + "Zobacz: " + ChatColor.GREEN + "/g pomoc");
            return true;
        }

        var commandHandler = commandHandlers.get(args[0]);

        if (commandHandler == null) {
            player.sendMessage(ChatColor.RED + "Taka komenda nie istnieje!");
            player.sendMessage(ChatColor.YELLOW + "Zobacz: " + ChatColor.GREEN + "/g pomoc");
            return true;
        }

        args = Arrays.copyOfRange(args, 1, args.length);

        commandHandler.handle(player, args);

        return true;
    }
}
