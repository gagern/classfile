package net.von_gagern.martin.classfile.tools;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashSet;
import java.util.Set;

import net.von_gagern.martin.classfile.ClassFile;
import net.von_gagern.martin.classfile.Method;
import net.von_gagern.martin.classfile.Constant;
import net.von_gagern.martin.classfile.Op;

class CheckForDefaultCharset {

    private static final Set<Constant> FORBIDDEN = new HashSet<>();

    private static void forbiddenMethod(String cls, String name, String descr) {
        FORBIDDEN.add(new Constant.Methodref(cls, name, descr));
    }

    private static void forbiddenClass(String cls) {
        FORBIDDEN.add(new Constant.Class(cls));
    }

    static {
        forbiddenMethod("java.io.ByteArrayOutputStream", "toString", "()Ljava.lang.String;");
        forbiddenMethod("java.io.InputStreamReader", "<init>", "(Ljava.io.InputStream;)V");
        forbiddenMethod("java.io.OutputStreamWriter", "<init>", "(Ljava.io.OutputStream;)V");
        forbiddenMethod("java.io.PrintStream", "<init>", "(Ljava.io.File;)V");
        forbiddenMethod("java.io.PrintStream", "<init>", "(Ljava.io.OutputStream;)V");
        forbiddenMethod("java.io.PrintStream", "<init>", "(Ljava.io.OutputStream;Z)V");
        forbiddenMethod("java.io.PrintStream", "<init>", "(Ljava.lang.String;)V");
        forbiddenMethod("java.io.PrintWriter", "<init>", "(Ljava.io.File;)V");
        forbiddenMethod("java.io.PrintWriter", "<init>", "(Ljava.io.OutputStream;)V");
        forbiddenMethod("java.io.PrintWriter", "<init>", "(Ljava.io.OutputStream;Z)V");
        forbiddenMethod("java.io.PrintWriter", "<init>", "(Ljava.lang.String;)V");
        forbiddenMethod("java.lang.String", "<init>", "([B)V");
        forbiddenMethod("java.lang.String", "<init>", "([BI)V");
        forbiddenMethod("java.lang.String", "<init>", "([BII)V");
        forbiddenMethod("java.lang.String", "<init>", "([BIII)V");
        forbiddenMethod("java.lang.String", "getBytes", "()[B");
        forbiddenMethod("java.lang.String", "getBytes", "(II[BI)V");
        forbiddenMethod("java.lang.String", "toLowerCase", "()Ljava.lang.String;");
        forbiddenMethod("java.lang.String", "toUpperCase", "()Ljava.lang.String;");
        forbiddenMethod("java.net.URLDecoder", "decode", "(Ljava.lang.String;)Ljava.lang.String;");
        forbiddenMethod("java.net.URLEncoder", "encode", "(Ljava.lang.String;)Ljava.lang.String;");
        forbiddenMethod("java.util.Formatter", "<init>", "()V");
        forbiddenMethod("java.util.Formatter", "<init>", "(Ljava.io.File;)V");
        forbiddenMethod("java.util.Formatter", "<init>", "(Ljava.io.File;Ljava.lang.String;)V");
        forbiddenMethod("java.util.Formatter", "<init>", "(Ljava.io.OutputStream;)V");
        forbiddenMethod("java.util.Formatter", "<init>", "(Ljava.io.OutputStream;Ljava.lang.String;)V");
        forbiddenMethod("java.util.Formatter", "<init>", "(Ljava.io.PrintStream;)V");
        forbiddenMethod("java.util.Formatter", "<init>", "(Ljava.lang.Appendable;)V");
        forbiddenMethod("java.util.Formatter", "<init>", "(Ljava.lang.String;)V");
        forbiddenMethod("java.util.Formatter", "<init>", "(Ljava.lang.String;Ljava.lang.String;)V");
        forbiddenMethod("java.util.Scanner", "<init>", "(Ljava.io.File;)V");
        forbiddenMethod("java.util.Scanner", "<init>", "(Ljava.io.InputStream;)V");
        forbiddenMethod("java.util.Scanner", "<init>", "(Ljava.nio.channels.ReadableByteChannel;)V");
        forbiddenMethod("java.util.Scanner", "<init>", "(Ljava.nio.file.Path;)V");
        forbiddenClass("java.io.FileWriter");
        forbiddenClass("java.io.StringBufferInputStream");
    }

    public static void main(String[] args) throws Exception {
        int exitStatus = 0;
        for (String arg: args) {
            boolean filePrinted = false;
            DataInputStream din = new DataInputStream
                (new FileInputStream(new File(arg)));
            ClassFile cf = new ClassFile(din);
            din.close();
            for (Constant c: cf.getConstantPool()) {
                if (c instanceof Constant.Methodref) {
                    Constant.Methodref m = (Constant.Methodref)c;
                    if (FORBIDDEN.contains(m) || FORBIDDEN.contains(m.getClazz())) {
                        if (!filePrinted) {
                            System.out.println(cf);
                            filePrinted = true;
                            exitStatus = 1;
                        }
                        System.out.println("  " + c);
                    }
                }
            }
        }
        System.exit(exitStatus);
    }

}
