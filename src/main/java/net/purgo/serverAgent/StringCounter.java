package net.purgo.serverAgent;

import javassist.*;
import javassist.expr.*;

import java.io.*;
import java.lang.instrument.*;
import java.security.*;
import java.util.*;

public class StringCounter implements ClassFileTransformer {
    private static ClassPool cp = ClassPool.getDefault();
    StringCounter() {
        cp.importPackage("net.purgo.serverAgent");

    }
    /** Class loaders we've already stuck on the class path. */
    Set<ClassLoader> seenClassLoaders = new HashSet<>();

    @Override
    public byte[] transform(ClassLoader loader, String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) throws IllegalClassFormatException {
        if(loader != null && seenClassLoaders.add(loader))
            cp.appendClassPath(new LoaderClassPath(loader));
        try {
            if ("java/lang/Thread".equals(className)) {
                return classfileBuffer;
            }
            if ("java/lang/String".equals(className)) {
                return classfileBuffer;
            }
            if (className == null
                    || className.startsWith("net/purgo/serverAgent")        // Don't instrument ourselves
                    || className.startsWith("java/")
                    || className.startsWith("sun/")
                    || className.startsWith("com/sun")
                    || className.startsWith("jdk")
                    )
            {
                    return classfileBuffer;
            }

            CtClass ct = cp.makeClass(new ByteArrayInputStream(classfileBuffer));

            CtMethod[] declaredMethods = ct.getDeclaredMethods();
            for (CtMethod method : declaredMethods) {
                if(!method.isEmpty() && !Modifier.isNative(method.getModifiers())){
                    method.instrument(new ExprEditor() {
                        public void edit(NewExpr e) {
                            try {
                                if(e.getClassName().equals("java.lang.String")) {
                                    method.insertAt(e.getLineNumber(),
                                            "{ Data.call(); }");
                                }
                            } catch (CannotCompileException t) {
                                System.out.println("Can't instrument " + className + "." + method.getName()
                                        + " " + e.getClass().getName() + " " + t.getMessage());
                            }
                        }
                    });
                    // There are other ways to identify servlet requests, but this does for the moment
                    if(method.getName().equals("service")) {
                        System.out.println("Instrumenting " + className + "." + method.getName());

                        method.insertBefore("{ Data.begin(\"" + className + "." + method.getName() + "\"); }");
                        // TODO Need to handle exceptions
                        method.insertAfter("{ Data.end(\"" + className + "." + method.getName() + "\"); }");
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
