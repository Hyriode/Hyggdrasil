package fr.hyriode.hyggdrasil.api.scheduler;

import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 26/12/2021 at 12:11
 */
public class HyggScheduler {

    /** The {@link ReadWriteLock} used to synchronize threads */
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    /** The {@link Lock} used when scheduler write a thing in the tasks map */
    private final Lock writeLock = lock.writeLock();
    /** The {@link ExecutorService} used to execute all tasks */
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    /** The counter of tasks */
    private final AtomicInteger tasksCounter = new AtomicInteger();
    /** A map of tasks. In key : the id of the task. In value : the task itself */
    private final Map<Integer, HyggTask> tasks = new HashMap<>();

    /**
     * This method is used to stop the scheduler
     */
    public void stop() {
        HyggdrasilAPI.log("Stopping Hyggdrasil scheduler...");

        this.executorService.shutdown();
    }

    /**
     * Schedule a task after a given time
     *
     * @param task The task to run
     * @param delay The delay before running the task
     * @param period The period between each execution of the task
     * @param unit The unit of time value used
     * @return The created {@link HyggTask}
     */
    public HyggTask schedule(Runnable task, long delay, long period, TimeUnit unit) {
        final HyggTask hyggTask = new HyggTask(this, this.tasksCounter.getAndIncrement(), task, delay, period, unit);

        try {
            this.writeLock.lock();
            this.tasks.put(hyggTask.getId(), hyggTask);
        } finally {
            this.writeLock.unlock();
        }

        this.executorService.execute(hyggTask);

        return hyggTask;
    }

    /**
     * Schedule a task after a given time
     *
     * @param task The task to run
     * @param delay The delay before running the task
     * @param unit The unit of time value used
     * @return The created {@link HyggTask}
     */
    public HyggTask schedule(Runnable task, long delay, TimeUnit unit) {
        return this.schedule(task, delay, 0, unit);
    }

    /**
     * Execute a task asynchronously
     *
     * @param task The task to execute
     * @return The created {@link HyggTask}
     */
    public HyggTask runAsync(Runnable task) {
        return this.schedule(task, 0, TimeUnit.MILLISECONDS);
    }

    /**
     * Cancel a running task
     *
     * @param id The id of the task
     */
    public void cancel(int id) {
        final HyggTask task = this.tasks.get(id);

        if (task != null) {
            task.cancel();
        } else {
            HyggdrasilAPI.log(Level.WARNING, "Couldn't find a task with '" + id + "' as an id!");
        }
    }

    /**
     * Main method to cancel the task
     *
     * @param task {@link HyggTask} to cancel
     */
    void cancel0(HyggTask task) {
        try {
            this.writeLock.lock();
            this.tasks.remove(task.getId());
        } finally {
            this.writeLock.unlock();
        }
    }

}
