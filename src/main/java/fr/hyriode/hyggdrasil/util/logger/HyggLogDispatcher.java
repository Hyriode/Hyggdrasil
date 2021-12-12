package fr.hyriode.hyggdrasil.util.logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.LogRecord;

public class HyggLogDispatcher extends Thread {

    private final HyggLogger logger;
    private final BlockingQueue<LogRecord> queue = new LinkedBlockingQueue<>();

    public HyggLogDispatcher(HyggLogger logger) {
        super("Hyggdrasil Logger Thread");
        this.logger = logger;
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            LogRecord record;
            try {
                record = this.queue.take();
            } catch (InterruptedException e) {
                continue;
            }

            this.logger.doLog(record);
        }

        for (LogRecord record : this.queue) {
            this.logger.doLog(record);
        }

    }

    public void queue(LogRecord record) {
        if (!this.isInterrupted())
            this.queue.add(record);
    }

    public BlockingQueue<LogRecord> getQueue() {
        return this.queue;
    }
}
