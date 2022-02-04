package com.itedya.guilds.daos;

import com.google.gson.Gson;
import com.itedya.guilds.Guilds;
import com.itedya.guilds.enums.QueueItemResult;
import com.itedya.guilds.models.QueueItem;
import com.itedya.guilds.queueitems.AnnounceGuildHeartBreakToPlayerQueueItem;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CopyOnWriteArrayList;

public class QueueDao {
    private static QueueDao instance;

    public static QueueDao getInstance() {
        if (instance == null) instance = new QueueDao();
        return instance;
    }

    private CopyOnWriteArrayList<QueueItem> data;
    private final Path path;
    private final Gson gson;

    public QueueDao() {
        Guilds plugin = (Guilds) Bukkit.getPluginManager().getPlugin("guilds");
        assert plugin != null;

        path = Paths.get(plugin.getDataFolder().getPath(), "queue");
        path.toFile().mkdirs();

        gson = new Gson();

        try {
            FileReader fileReader = new FileReader(Paths.get(path.toString(), "announce-guild-heart-break.json").toString());

            data = new CopyOnWriteArrayList<>(gson.fromJson(fileReader, AnnounceGuildHeartBreakToPlayerQueueItem[].class));

            fileReader.close();

            Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                try {
                    QueueDao queueDao = QueueDao.getInstance();
                    queueDao.saveData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }, 20 * 60, 20 * 60);

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                QueueDao queueDao = QueueDao.getInstance();

                while (true) {
                    do {
                        QueueItem item = queueDao.next();
                        QueueItemResult res = item.execute();

                        if (res == QueueItemResult.EXCEPTION) queueDao.add(item);
                    } while (queueDao.hasNext());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() throws IOException {
//        TODO: fix it
//        queueTask.cancel();
        saveData();
    }

    private void saveData() throws IOException {
        FileWriter fileWriter = new FileWriter(Paths.get(path.toString(), "announce-guild-heart-break.json").toString(), false);

        gson.toJson(data, fileWriter);

        fileWriter.flush();
        fileWriter.close();
    }

    public void add(QueueItem queueItem) {
        data.add(queueItem);
    }

    public boolean hasNext() {
        return data.size() != 0;
    }

    public QueueItem next() {
        if (data.size() == 0) return null;
        QueueItem item = data.get(0);
        data.remove(0);
        return item;
    }
}