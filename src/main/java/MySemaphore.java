/**
 * Created by Leshka on 24.02.18.
 */
public interface MySemaphore {
    void acquire() throws InterruptedException;
    void release();
    boolean tryAcquire();
}
