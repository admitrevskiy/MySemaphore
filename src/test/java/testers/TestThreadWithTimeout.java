package testers;

import semaphore.MySemaphore;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Leshka on 28.02.18.
 */
public class TestThreadWithTimeout implements Runnable {

    private MySemaphore semaphore;
    private int timeout;

    public TestThreadWithTimeout(MySemaphore semaphore, int timeout) {
        this.semaphore = semaphore;
        this.timeout = timeout;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " is waiting for permit");

        try {
            semaphore.acquire();
            System.out.println(Thread.currentThread().getName() + " is got for permit");
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(Thread.currentThread().getName() + " release permit");
        semaphore.release();
    }
}
