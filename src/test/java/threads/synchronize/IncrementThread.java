package threads.synchronize;

import semaphore.MySemaphore;

/**
 * Created by Leshka on 22.02.18.
 */
public class IncrementThread implements Runnable {

    private MySemaphore semaphore;
    private int count;
    private int timeout;

    public IncrementThread(MySemaphore semaphore, int count, int timeout) {
        this.semaphore = semaphore;
        this.count = count;
        this.timeout = timeout;
    }

    @Override
    public synchronized void run() {
        System.out.println(Thread.currentThread().getName() + " is waiting for permit");

        try {
            semaphore.acquire();
                System.out.println(Thread.currentThread().getName() + " is got for permit");
                for (int i=0; i<count; i++) {
                    SharedValue.incrementCount();
                    System.out.println(Thread.currentThread().getName() + " : value = " + SharedValue.getCount());
                    Thread.sleep(timeout);

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(Thread.currentThread().getName() + " has released permit");
        semaphore.release();
    }
}
