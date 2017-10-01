package net.purgo.serverAgent;

import javassist.*;

import java.io.*;
import java.lang.instrument.*;
import java.security.*;

public class StringCounter implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) throws IllegalClassFormatException {
        try {
            ClassPool cp = ClassPool.getDefault();
            cp.importPackage("net.purgo.serverAgent");
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
                if(!method.isEmpty() && Modifier.isNative(method.getModifiers())){
                    method.insertBefore(" { " +
                            "Data.call();" +
                            "}");
                }
            }

            return ct.toBytecode();
        } catch (Throwable e) {
            System.out.println("Can't instrument " + className + " " + e.getClass().getName() + " " + e.getMessage());
        }

        return classfileBuffer;
    }
}
