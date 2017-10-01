package net.purgo.serverAgent;

import java.lang.instrument.*;

public class Main {
    public static void premain(String args, Instrumentation instrumentation) throws Throwable {
        instrumentation.retransformClasses(String.class);
        StringCounter transformer = new StringCounter();
        instrumentation.addTransformer(transformer);

        instrumentation.retransformClasses(String.class);
    }
}
