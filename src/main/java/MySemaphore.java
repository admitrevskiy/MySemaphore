/**
 * Created by Leshka on 22.02.18.
 */
public class MySemaphore {
    private int permits;

    public MySemaphore(int permits) {
        this.permits = permits;
    }

    public synchronized void acquire() throws InterruptedException {
        if (permits > 0) {
            permits--;
        }

        else {
            this.wait();
            permits--;
        }
    }

    public synchronized void release() {
        permits++;

        if (permits > 0) {
            this.notify();
        }
    }

    public boolean tryAcquire() {
        if (permits > 0) {
            return true;
        }

        return false;
    }
}
