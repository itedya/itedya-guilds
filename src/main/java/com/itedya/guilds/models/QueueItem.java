package com.itedya.guilds.models;

import com.itedya.guilds.enums.QueueItemResult;
import com.itedya.guilds.enums.QueueItemType;

public class QueueItem {
    private QueueItemType type;

    public QueueItemType getType() {
        return type;
    }

    public void setType(QueueItemType type) {
        this.type = type;
    }

    public QueueItemResult execute() {
        return QueueItemResult.FINISHED;
    }
}
