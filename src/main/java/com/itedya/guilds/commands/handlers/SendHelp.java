package com.itedya.guilds.commands.handlers;

import com.itedya.guilds.Guilds;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class SendHelp implements CommandHandler {
    private List<String> help;

    public SendHelp() {
        Guilds plugin = Guilds.getPlugin();
        InputStream is = plugin.getResource("help.txt");

        try {
            assert is != null;
            this.help = Arrays.stream(new String(is.readAllBytes()).split("\r\n")).toList();
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Server error!", e);
            e.printStackTrace();
        }
    }

    @Override
    public void handle(Player player, String[] args) {
        for (var line : help) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
        }
    }
}
