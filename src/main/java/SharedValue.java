/**
 * Created by Leshka on 22.02.18.
 */
public class SharedValue {
    static volatile int count = 0;

    public static int getCount() {
        return count;
    }

    public static synchronized void incrementCount() {
        count++;
    }

    public static synchronized void decrementCount() {
        count--;
    }
}
