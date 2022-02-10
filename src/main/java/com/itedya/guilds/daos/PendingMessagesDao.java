package com.itedya.guilds.daos;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.itedya.guilds.Guilds;
import com.itedya.guilds.models.MessageItem;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class PendingMessagesDao {
    private static PendingMessagesDao instance;

    public static PendingMessagesDao getInstance() {
        if (instance == null) instance = new PendingMessagesDao();
        return instance;
    }

    private final Guilds plugin = Guilds.getPlugin();
    private final Path path = Paths.get(plugin.getDataFolder().toString(), "pending-messages.json");
    private final Gson gson = new Gson();
    private Map<String, List<MessageItem>> data = new ConcurrentHashMap<>();

    private PendingMessagesDao() {
        try {
            plugin.saveResource("pending-messages.json", false);

            FileReader fileReader = new FileReader(path.toString());

            Type empMapType = new TypeToken<Map<String, List<MessageItem>>>(){}.getType();
            data = new ConcurrentHashMap<>(gson.fromJson(fileReader, empMapType));

            fileReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() throws IOException {
        FileWriter fileWriter = new FileWriter(path.toString(), false);

        gson.toJson(data, fileWriter);

        fileWriter.flush();
        fileWriter.close();
    }

    public List<MessageItem> getByPlayerUuid(String playerUuid) {
        return data.get(playerUuid);
    }

    public void addMessage(String playerUuid, MessageItem messageItem) {
        List<MessageItem> messages = data.get(playerUuid);

        if (messages == null) {
            messages = new CopyOnWriteArrayList<>();
        }

        messages.add(messageItem);
        data.put(playerUuid, messages);
    }

    public void removeByPlayerUuid(String playerUuid) {
        data.remove(playerUuid);
    }
}
