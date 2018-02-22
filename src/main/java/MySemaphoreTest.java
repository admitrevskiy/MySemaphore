/**
 * Created by Leshka on 22.02.18.
 */
public class MySemaphoreTest {
    static int SharedValue=0;

    public static void main(String[] args) {
        MySemaphore semaphore = new MySemaphore(2);
        System.out.println("Semaphore with 2 permits was created");

        IncrementThread thread1 = new IncrementThread(semaphore, "First increment thread", 3);
        IncrementThread thread2 = new IncrementThread(semaphore, "Second increment thread", 4);
        DecrementThread thread3 = new DecrementThread(semaphore, "First decrement thread", 7);
        //DecrementThread thread4 = new DecrementThread(semaphore, "Second decrement thread", 5);

        new Thread(thread1, "firstIncrementThread").start();
        new Thread(thread2, "secondIncrementThread").start();
        new Thread(thread3, "firstDecrementThread").start();
        //new Thread(thread4, "secondDecrementThread").start();
    }
}
