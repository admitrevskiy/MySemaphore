package semaphore;

/**
 * Created by Leshka on 22.02.18.
 */
public class MySemaphoreImpl implements MySemaphore {
    /**
     * The number of available permissions
     * that are specified in constructor
     */
    private volatile int permits;
    private final Object lock = new Object();

    public MySemaphoreImpl(int permits) {
        this.permits = permits;
    }

    /**
     * Let the thread to do it's job if there are
     * any available permissions at the time of method calling;
     * Decrease the number of permissions per one for each thread;
     * Otherwise call the wait() method;
     * @throws InterruptedException
     */
    @Override
    public void acquire() throws InterruptedException {
        synchronized (lock) {
            System.out.println("Before acquire(): Available permits: " + permits);
            while (permits <= 0) {
                System.out.println("No available permits");
                lock.wait();
            }
            permits--;
            System.out.println("After acquire(): Available permits: " + permits);

        }
    }

    /**
     * The number of permissions increase by one when this method is called by a thread;
     * A random thread wakes up if there are available permissions;
     */
    @Override
    public void release() {
        synchronized (lock) {
            System.out.println("Before release(): Available permits: " + permits);

            permits++;

            lock.notify();
            System.out.println("release() method calls notify()");


            System.out.println("After release(): Available permits: " + permits);
        }
    }

    /**
     * Return true if number of available permissions greater than zero;
     * Otherwise return false;
     */
    @Override
    public boolean tryAcquire() {
        synchronized (lock) {
            if (permits > 0) {
                permits--;
                return true;
            }
            return false;
        }
    }
}
