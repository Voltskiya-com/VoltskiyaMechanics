package com.voltskiya.mechanics.tribe;

import apple.utilities.threading.service.base.create.AsyncTaskQueueStart;
import apple.utilities.threading.service.queue.AsyncTaskQueue;
import apple.utilities.threading.service.queue.TaskHandlerQueue;

public class TribeIOService {

    public static TaskHandlerQueue handler = new TaskHandlerQueue(10, 0, 0);

    public static TaskHandlerQueue get() {
        return handler;
    }

    public static AsyncTaskQueueStart<AsyncTaskQueue> taskCreator() {
        return handler.taskCreator();
    }
}
