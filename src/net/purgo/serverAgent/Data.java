package net.purgo.serverAgent;

import java.util.*;

/** Data being collected by the server agent */
public class Data {
    public static List<String> responses = new ArrayList<>();

    static class IntThreadLocal extends ThreadLocal<int[]>{
        @Override
        public int[] initialValue() {
            return new int[1];
        }
    }

    static class LongThreadLocal extends ThreadLocal<long[]>{
        @Override
        public long[] initialValue() {
            return new long[1];
        }

        public void set(long l) {
            get()[0] = l;
        }
        public long value() {
            return get()[0];
        }

    }


    /** Counts each time call is called */
    static IntThreadLocal counter = new IntThreadLocal();

    /** Reset each time begin is called. */
    static IntThreadLocal startCount = new IntThreadLocal();

    static LongThreadLocal lastMemoryMark = new LongThreadLocal();


    static LongThreadLocal startTime = new LongThreadLocal();

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
    public static void begin(String source) {
        startCount.get()[0] = counter.get()[0];
        startTime.set(System.currentTimeMillis());
        lastMemoryMark.set(Runtime.getRuntime().freeMemory());
    }

    /** Call this to end a counting session */
    public static void end(String source) {
        int total = counter.get()[0] - startCount.get()[0];
        long time = (System.currentTimeMillis() - startTime.value());
        long mem = lastMemoryMark.value() - Runtime.getRuntime().freeMemory();
        responses.add(source + ": " + total + " strings in " +
                time + " with memory " + mem + " from thread " + Thread.currentThread() );
    }
}
