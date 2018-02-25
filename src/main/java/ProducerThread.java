import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Leshka on 26.02.18.
 */
public class ProducerThread implements Runnable {

    private MySemaphore semaphore;
    private CopyOnWriteArraySet<Integer> set;
    private int value;
    private int timeout;


    public ProducerThread(MySemaphore semaphore, CopyOnWriteArraySet<Integer> set, int value, int timeout) {
        this.semaphore = semaphore;
        this.set = set;
        this.value = value;
        this.timeout = timeout;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " is waiting for permit");

        try {
            semaphore.acquire();
            System.out.println(Thread.currentThread().getName() + " is got for permit");
            set.add(value);
            System.out.println(Thread.currentThread().getName() + ": puts value: " + value);
            Thread.sleep(timeout);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(Thread.currentThread().getName() + " has released permit");
        semaphore.release();
    }
}
