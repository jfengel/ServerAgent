package net.purgo.serverAgent;

import javassist.*;
import javassist.expr.*;

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
//                    method.insertBefore(" { " +
//                            "Data.call();" +
//                            "}");
                    method.instrument(new ExprEditor() {
                        public void edit(NewExpr e) {
                            try {
                                System.out.println(e.getClassName());
                                if(e.getClassName().equals("java.lang.String")) {
                                    method.insertAt(e.getLineNumber(),
                                            "{ Data.call(\"" + e.getClassName() + "\"); }");
                                }
                            } catch (CannotCompileException t) {
                                System.out.println("Can't instrument " + className + "." + method.getName()
                                        + " " + e.getClass().getName() + " " + t.getMessage());
                            }
                        }
                    });

                }
            }

            return ct.toBytecode();
        } catch (Throwable e) {
            System.out.println("Can't instrument " + className + " " + e.getClass().getName() + " " + e.getMessage());
        }

        return classfileBuffer;
    }
}
