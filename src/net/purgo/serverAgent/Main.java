package net.purgo.serverAgent;

import java.io.*;
import java.lang.instrument.*;
import java.net.*;

public class Main {
    public static void premain(String args, Instrumentation instrumentation) throws Throwable {
        startHttpDaemon();
        instrumentation.retransformClasses(String.class);
        StringCounter transformer = new StringCounter();
        instrumentation.addTransformer(transformer);

        instrumentation.retransformClasses(String.class);
    }

    /** A trivial HTTP daemon to provide some data
     * TODO replace this with sparkjava or something. (I tried and ran into classloader issues.)
     */
    private static void startHttpDaemon() {
        new Thread(()->{
            try {
                ServerSocket server = new ServerSocket(1729);
                while(true) {
                    Socket s = server.accept();

                    Writer w = new OutputStreamWriter(s.getOutputStream());
                    w.append("HTTP/1.1 200 OK\n\n");
                    w.append("<body>\n");
                    w.append("<ul>\n");
                    if(Data.responses.isEmpty()) {
                        w.append("<p>Nothing here yet</p>");
                    }
                    for(String r : Data.responses) {
                        w.append("<li>\n");
                        w.append(r);        // TODO really need to sanitize this
                        w.append("</li>\n");
                    }
                    w.append("</ul>\n");
                    w.append("</body>\n");
                    w.flush();
                    s.close();
                }
            }
            catch(Throwable t) {
                t.printStackTrace();
            }
        }).start();
    }
}
