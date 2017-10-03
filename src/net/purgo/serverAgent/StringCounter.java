package net.purgo.serverAgent;

import javassist.*;
import javassist.expr.*;

import javax.servlet.http.*;
import java.io.*;
import java.lang.instrument.*;
import java.security.*;

public class StringCounter implements ClassFileTransformer {
    private static ClassPool cp = ClassPool.getDefault();
    StringCounter() {
        cp.importPackage("net.purgo.serverAgent");

    }

    @Override
    public byte[] transform(ClassLoader loader, String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) throws IllegalClassFormatException {
        try {
            if ("java/lang/Thread".equals(className)) {
                return classfileBuffer;
            }
            if ("java/lang/String".equals(className)) {
                return classfileBuffer;
            }
            if (className == null
                    || className.startsWith("net/purgo/serverAgent")        // Don't instrument ourselves
                    || className.startsWith("java")
                    || className.startsWith("sun")
                    || className.startsWith("com/sun")
                    || className.startsWith("jdk")
                    || className.startsWith("org/xml")
                    )
            {
                    return classfileBuffer;
            }

//            System.out.println("Instrumenting " + className);
            CtClass ct = cp.makeClass(new ByteArrayInputStream(classfileBuffer));

            CtMethod[] declaredMethods = ct.getDeclaredMethods();
            for (CtMethod method : declaredMethods) {
                if(!method.isEmpty() && !Modifier.isNative(method.getModifiers())){

                    // There are other ways to identify servlet requests, but this does for the moment
                    if(method.getName().equals("service")) {
                        Class clz = HttpServletRequest.class;   // TODO This is bogus, but I need to force the compiler to recognize its existence
                        System.out.println("Instrumenting " + className + "." + method.getName());

                        method.insertBefore("{ Data.begin(); }");
                        method.insertAfter("{ Data.end(); }");
                    }

                }
            }

            return ct.toBytecode();
        } catch (Throwable e) {
            System.out.println("Can't instrument " + className + " " + e.getClass().getName() + " " + e.getMessage());
        }

        return classfileBuffer;
    }
}
