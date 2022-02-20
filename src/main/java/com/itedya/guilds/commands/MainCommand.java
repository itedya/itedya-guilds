package com.itedya.guilds.commands;

import com.itedya.guilds.commands.handlers.*;
import com.itedya.guilds.daos.ConfigDao;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainCommand extends Command {
    private static MainCommand instance;

    public static MainCommand getInstance() {
        if (instance == null) {
            ConfigDao configDao = ConfigDao.getInstance();
            instance = new MainCommand(configDao.getString("command.name"));
        }
        return instance;
    }

    private final Map<String, CommandHandler> commandHandlers = new HashMap<>();

    private MainCommand(String mainCommandName) {
        super(mainCommandName);

        ConfigDao configDao = ConfigDao.getInstance();

        commandHandlers.put(configDao.getString("command.sub-commands.create"), new CreateGuild());
        commandHandlers.put(configDao.getString("command.sub-commands.remove"), new DeleteGuild());
        commandHandlers.put(configDao.getString("command.sub-commands.invite-player"), new InviteToGuild());
        commandHandlers.put(configDao.getString("command.sub-commands.accept"), new AcceptInviteToGuild());
        commandHandlers.put(configDao.getString("command.sub-commands.kick-player"), new KickOutOfGuild());
        commandHandlers.put(configDao.getString("command.sub-commands.leave"), new LeaveGuild());
        commandHandlers.put(configDao.getString("command.sub-commands.help"), new SendHelp());
        commandHandlers.put(configDao.getString("command.sub-commands.set-home"), new SetGuildHome());
        commandHandlers.put(configDao.getString("command.sub-commands.info"), new SetGuildHome());
        commandHandlers.put(configDao.getString("command.sub-commands.teleport-to-home"), new SetGuildHome());

        try {
            Field f = null;
            f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            CommandMap commandMap = (CommandMap) f.get(Bukkit.getServer());
            commandMap.register("itedya-guilds", this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("You have to be in game to send commands to use this plugin!");
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
