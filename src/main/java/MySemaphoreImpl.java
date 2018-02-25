/**
 * Created by Leshka on 22.02.18.
 */
public class MySemaphoreImpl implements MySemaphore {
    /**
     * The number of available permissions
     * that are specified in constructor
     */
    private volatile int permits;

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
    public synchronized void acquire() throws InterruptedException {
        System.out.println("Before acquire(): Available permits: " + permits);
        if (permits > 0) {
            permits--;
            System.out.println("After acquire(): Available permits: " + permits);
        }

        else {
            this.wait();
            System.out.println("No available permits");
        }
    }

    /**
     * The number of permissions increase by one when this method is called by a thread;
     * A random thread wakes up if there are available permissions;
     */
    @Override
    public synchronized void release() {

        System.out.println("Before release(): Available permits: " + permits);

        permits++;

        if (permits > 0) {
            this.notify();
            System.out.println("release() method calls notify()");
        }

        System.out.println("After release(): Available permits: " + permits);
    }

    /**
     * Return true if number of available permissions greater than zero;
     * Otherwise return false;
     */
    @Override
    public boolean tryAcquire() {

        if (permits > 0) {
            return true;
        }

        return false;
    }
}
