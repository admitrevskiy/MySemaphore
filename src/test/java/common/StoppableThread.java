package common;

import semaphore.MySemaphore;

/**
 * Created by Leshka on 27.02.18.
 */
public class StoppableThread implements Runnable{

    private MySemaphore sem;
    private boolean stopped;

    public StoppableThread(MySemaphore sem) {
        this.sem = sem;
        this.stopped = false;
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
                System.out.println("waiting");
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
            sem.release();
            System.out.println(Thread.currentThread().getName() + " released permit");

    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }
}
