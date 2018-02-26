package threads.concurrent;

import semaphore.MySemaphore;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Leshka on 26.02.18.
 */
public class AtomicIncrementThread implements Runnable {

    private MySemaphore semaphore;
    private AtomicInteger atomicInt;
    private int count;
    private int timeout;

    public AtomicIncrementThread(MySemaphore semaphore, AtomicInteger atomicInt, int count, int timeout) {
        this.semaphore = semaphore;
        this.atomicInt = atomicInt;
        this.count = count;
        this.timeout = timeout;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " is waiting for permit");

        try {
            semaphore.acquire();
            System.out.println(Thread.currentThread().getName() + " is got for permit");
            for (int i=0; i<count; i++) {
                atomicInt.getAndAdd(1);
                System.out.println(Thread.currentThread().getName() + ": Value: " + atomicInt.get());
                Thread.sleep(timeout);

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(Thread.currentThread().getName() + " has released permit");
        semaphore.release();
    }
}
