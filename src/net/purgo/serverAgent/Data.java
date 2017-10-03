package net.purgo.serverAgent;

/** Data being collected by the server agent */
public class Data {
    /** Counts each time call is called */
    static ThreadLocal<int[]> counter = new ThreadLocal<int[]>() {
        @Override
        public int[] initialValue() {
            return new int[1];
        }
    };

    /** Reset each time begin is called. */
    static ThreadLocal<int[]> startCount = new ThreadLocal<int[]>() {
        @Override
        public int[] initialValue() {
            return new int[1];
        }
    };

    /** Increments the per-thread string count.
     */
    public static void call() {
        counter.get()[0]++;
    }


    /** Increments the per-thread string count.
     * @param s -- currently ignored
     */
    public static void call(String s) {
        counter.get()[0]++;
    }

    /** Call this to begin a counting session */
    public static void begin() {
        startCount.get()[0] = counter.get()[0];
    }

    /** Call this to end a counting session */
    public static void end() {
        int total = counter.get()[0] - startCount.get()[0];
        System.out.println("Created " + total + " strings");
    }
}
