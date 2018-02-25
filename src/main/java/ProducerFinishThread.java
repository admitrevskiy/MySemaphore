import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by Leshka on 26.02.18.
 */
public class ProducerFinishThread implements Runnable {

    private MySemaphore semaphore;
    private CopyOnWriteArraySet<Integer> set;
    private CopyOnWriteArraySet<Integer> finishSet;
    private int value;
    private int timeout;

    public ProducerFinishThread(MySemaphore semaphore, CopyOnWriteArraySet<Integer> set, CopyOnWriteArraySet<Integer> finishSet, int value, int timeout) {
        this.semaphore = semaphore;
        this.set = set;
        this.finishSet = finishSet;
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
            System.out.println(Thread.currentThread().getName() + ": puts value: " + value + " into the set");
            Thread.sleep(timeout);
            finishSet.add(value);
            System.out.println(Thread.currentThread().getName() + ": puts value: " + value + " into the finishSet");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(Thread.currentThread().getName() + " has released permit");
        semaphore.release();
    }
}

