package net.purgo.serverAgent;

import java.lang.instrument.*;

public class Main {
    public static void premain(String args, Instrumentation instrumentation){
        StringCounter transformer = new StringCounter();
        instrumentation.addTransformer(transformer);
    }
}
