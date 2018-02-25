import org.junit.*;

import static org.junit.Assert.*;

/**
 * Created by Leshka on 25.02.18.
 */
public class MySemaphoreTryAcquireTest {

    private MySemaphore semWithPermit;
    private MySemaphore semWithoutPermit;

    @BeforeClass
    public static void beforeClass() {
        System.out.println("Before MySemaphoreTryAcquireTest.class");
    }

    @AfterClass
    public static void afterClass() {
        System.out.println("After MySemaphoreTryAcquireTest.class");
    }

    @Before
    public void setUp() throws Exception {
        semWithPermit = new MySemaphoreImpl(1);
        semWithoutPermit = new MySemaphoreImpl(0);
    }

    @After
    public void tearDown() throws Exception {
        semWithPermit = null;
        semWithoutPermit = null;
    }

    @Test
    public void tryAcquire() {
        assertTrue(semWithPermit.tryAcquire());
        assertFalse(semWithoutPermit.tryAcquire());
    }

    @Test
    public void releaseTest() {
        semWithoutPermit.release();
        assertTrue(semWithoutPermit.tryAcquire());
    }

    @Test
    public void acquireTest() throws InterruptedException {
        semWithPermit.acquire();
        assertFalse(semWithPermit.tryAcquire());
    }

}