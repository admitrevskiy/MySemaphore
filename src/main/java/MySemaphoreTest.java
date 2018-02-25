/**
 * Created by Leshka on 22.02.18.
 */
public class MySemaphoreTest {

    public static void main(String[] args) {
        MySemaphoreImpl semaphore = new MySemaphoreImpl(2);
        System.out.println("Semaphore with 2 permits was created");
        System.out.println("Shared: " + SharedValue.count);
        //IncrementThread thread1 = new IncrementThread(semaphore, "First increment thread", 13);
        //IncrementThread thread2 = new IncrementThread(semaphore, "Second increment thread", 14);
        DecrementThread thread3 = new DecrementThread(semaphore, "First decrement thread", 12);
        DecrementThread thread4 = new DecrementThread(semaphore, "Second decrement thread", 15);
        DecrementThread thread5 = new DecrementThread(semaphore, "Second decrement thread", 1);
        DecrementThread thread6 = new DecrementThread(semaphore, "Second decrement thread", 10);
        //IncrementThread thread7 = new IncrementThread(semaphore, "First decrement thread", 11);

        //new Thread(thread1, "firstIncrementThread").start();
        //new Thread(thread2, "secondIncrementThread").start();
        new Thread(thread3, "firstDecrementThread").start();
        new Thread(thread4, "secondDecrementThread").start();
        new Thread(thread5, "thirdDecrementThread").start();
        new Thread(thread6, "fourthDecrementThread").start();
        //new Thread(thread7, "thirdDecrementThread").start();
    }
}
