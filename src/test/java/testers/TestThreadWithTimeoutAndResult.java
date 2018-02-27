package testers;

import semaphore.MySemaphore;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Leshka on 26.02.18.
 */
public class TestThreadWithTimeoutAndResult implements Runnable {

    private MySemaphore semaphore;
    private int timeout;
    private AtomicInteger startInt, stopInt;

    public TestThreadWithTimeoutAndResult(MySemaphore semaphore, int timeout, AtomicInteger startInt, AtomicInteger stopInt) {
        this.semaphore = semaphore;
        this.timeout = timeout;
        this.startInt = startInt;
        this.stopInt = stopInt;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " is waiting for permit");

        try {
            semaphore.acquire();
            System.out.println(Thread.currentThread().getName() + " is got for permit");
            startInt.getAndAdd(1);
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        stopInt.getAndAdd(1);
        System.out.println(Thread.currentThread().getName() + " release permit");
        semaphore.release();
    }
}
