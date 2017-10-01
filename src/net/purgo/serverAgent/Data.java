package net.purgo.serverAgent;

public class Data {
    private static int call;
    public static void call() {
        call++;
    }
    public static void init() {
        System.out.println("Instrumenting...");
    }
}
