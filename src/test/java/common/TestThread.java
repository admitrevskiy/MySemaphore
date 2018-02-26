package common;

import semaphore.MySemaphore;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Leshka on 26.02.18.
 */
public class TestThread implements Runnable {

    private MySemaphore semaphore;
    private int timeout;
    private boolean a;

    public TestThread(MySemaphore semaphore, int timeout) {
        this.semaphore = semaphore;
        this.timeout = timeout;
        a = false;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " is waiting for permit");

        try {
            semaphore.acquire();
            System.out.println(Thread.currentThread().getName() + " is got for permit");
            Thread.sleep(timeout);
            a = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(Thread.currentThread().getName() + " release permit");
        semaphore.release();
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
