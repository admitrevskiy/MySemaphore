package testers;

import semaphore.MySemaphore;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Leshka on 27.02.18.
 */
public class StoppableThreadWithResult implements Runnable {
    private MySemaphore sem;
    private boolean stopped;
    private AtomicInteger result;

    public StoppableThreadWithResult(MySemaphore sem, AtomicInteger result) {
        this.sem = sem;
        this.stopped = false;
        this.result = result;
    }

    @Override
    public void run() {
        try {
            sem.acquire();
            System.out.println(Thread.currentThread().getName() + " acquires permit");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            while (!stopped) {
                System.out.println(Thread.currentThread().getName() + " is waiting");
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        result.getAndAdd(1);
        sem.release();
        System.out.println(Thread.currentThread().getName() + " released permit");

    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }
}
