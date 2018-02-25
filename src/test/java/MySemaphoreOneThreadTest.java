import org.junit.*;
import semaphore.MySemaphoreImpl;
import threads.synchronize.IncrementThread;

import static org.junit.Assert.*;

/**
 * Created by Leshka on 25.02.18.
 */
public class MySemaphoreOneThreadTest {

    MySemaphoreImpl sem;
    IncrementThread incrementThread;

    @BeforeClass
    public static void beforeClass() {
        System.out.println("Before MySemaphoreOneThreadTest.class");
    }

    @AfterClass
    public static void afterClass() {
        System.out.println("After MySemaphoreOneThreadTest.class");
    }

    @Before
    public void setUp() throws Exception {
        sem = new MySemaphoreImpl(1);
        incrementThread = new IncrementThread(sem, 10, 100);
    }

    @After
    public void tearDown() throws Exception {
        sem = null;
    }

    @Test
    public void tryAcquireTest() throws InterruptedException {
        assertTrue(sem.tryAcquire());
        new Thread(incrementThread, "incrementThread").start();
        Thread.sleep(10);
        assertFalse(sem.tryAcquire());
    }

}