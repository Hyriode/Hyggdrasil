package fr.hyriode.hyggdrasil.queue;

import fr.hyriode.hyggdrasil.api.queue.HyggQueueGroup;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 17/04/2022 at 09:39
 */
public class HyggPriorityQueue extends PriorityBlockingQueue<HyggQueueGroup> {

    private final ReentrantLock lock = new ReentrantLock();

    public HyggPriorityQueue() {
        super(100000, Comparator.comparingInt(HyggQueueGroup::getPriority));
    }

    public void drainGroups(Collection<HyggQueueGroup> collection, int maxElements) {
        if (collection == null) {
            throw new NullPointerException();
        }
        if (maxElements <= 0) {
            return;
        }

        final ReentrantLock lock = this.lock;
        lock.lock();

        try {
            int remainingElements = maxElements;
            int j = 0;

            for (int i = 0; i < this.size(); i++) {
                final HyggQueueGroup group = (HyggQueueGroup) this.toArray()[j];
                final int groupSize = group.getSize();

                if (remainingElements - groupSize >= 0) {
                    collection.add(group);

                    remainingElements -= groupSize;
                } else {
                    j++;
                }

                if (remainingElements <= 0) {
                    break;
                }
            }
        } finally {
            lock.unlock();
        }
    }

}
