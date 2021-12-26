package fr.hyriode.hyggdrasil.api.scheduler;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 26/12/2021 at 12:21
 */
public class HyggTask implements Runnable {

    private Runnable then;

    private final AtomicBoolean running = new AtomicBoolean(true);

    private final HyggScheduler scheduler;
    private final int id;
    private final Runnable task;
    private final long delay;
    private final long period;

    public HyggTask(HyggScheduler scheduler, int id, Runnable task, long delay, long period, TimeUnit unit) {
        this.scheduler = scheduler;
        this.id = id;
        this.task = task;
        this.delay = unit.toMillis(delay);
        this.period = unit.toMillis(period);
    }

    @Override
    public void run() {
        if (delay > 0) {
            try {
                Thread.sleep(this.delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        while (this.running.get()) {
            this.task.run();

            if (this.period <= 0) {
                break;
            }

            try {
                Thread.sleep(this.period);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        this.cancel();
    }

    public void andThen(Runnable then) {
        this.then = then;
    }

    public void cancel() {
        this.scheduler.cancel0(this);

        this.running.set(false);

        if (this.then != null) {
            this.then.run();
        }
    }

    public int getId() {
        return this.id;
    }

    public boolean isRunning() {
        return this.running.get();
    }

}
