package threads.synchronize;


import semaphore.MySemaphore;

/**
 * Created by Leshka on 22.02.18.
 */
public class DecrementThread implements Runnable {

    private MySemaphore semaphore;
    private String name;
    private int count;

    public DecrementThread(MySemaphore semaphore, String name, int count) {
        this.semaphore = semaphore;
        this.name = name;
        this.count = count;
        System.out.println(name + " was created");
    }

    @Override
    public synchronized void run() {
        System.out.println(Thread.currentThread().getName() + " is waiting for permit");

        try {
                semaphore.acquire();
                System.out.println(Thread.currentThread().getName() + " is got for permit");
                for (int i=0; i<count; i++) {
                    SharedValue.decrementCount();
                    System.out.println(Thread.currentThread().getName() + " : value = " + SharedValue.getCount());
                    Thread.sleep(200);
                }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(Thread.currentThread().getName() + " has released permit");
        semaphore.release();
    }
}
