package net.von_gagern.martin.classfile.tools;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.von_gagern.martin.classfile.ClassFile;
import net.von_gagern.martin.classfile.CodeAttribute;
import net.von_gagern.martin.classfile.Constant;
import net.von_gagern.martin.classfile.Descriptor;
import net.von_gagern.martin.classfile.Field;
import net.von_gagern.martin.classfile.Method;
import net.von_gagern.martin.classfile.Op;

/**
 * Disassemble class files, similar to {@code javap -v}.
 */
class Disasm {

    public static void main(String[] args) throws Exception {
        int exitStatus = 0;
        for (String arg: args) {
            boolean filePrinted = false;
            DataInputStream din = new DataInputStream
                (new FileInputStream(new File(arg)));
            ClassFile cf = new ClassFile(din);
            din.close();
            new Disasm(cf, System.out);
        }
        System.exit(exitStatus);
    }

    private ClassFile cf;

    private Appendable out;

    private String indent = "";

    private String pkg;

    private void println(String str) throws IOException {
        out.append(indent).append(str).append('\n');
    }

    private String relativeClassName(Descriptor.Value d) {
        if (d instanceof Descriptor.Obj)
            return relativeClassName(d.getName());
        if (d instanceof Descriptor.Array)
            return relativeClassName(d.getComponentType()) + "[]";
        return d.getName();
    }
    
    private String relativeClassName(String cls) {
        if (pkg == null) return cls;
        int s = cls.lastIndexOf('.');
        if (s < 0) return cls;
        if (pkg.equals(cls.substring(0, s))) return cls.substring(s + 1);
        return cls;
    }

    private Disasm(ClassFile cf, Appendable out) throws IOException {
        this.cf = cf;
        this.out = out;
        String name = cf.getClassName();
        int i = name.lastIndexOf('.');
        if (i > 0) {
            pkg = name.substring(0, i);
            name = name.substring(i + 1);
            println("package " + pkg + ";");
        }
        out.append("class ").append(name);
        String sup = cf.getSuperClassName();
        if (!"java.lang.Object".equals(sup)) {
            out.append("\n    extends ").append(relativeClassName(sup));
        }
        String sep = "\n    implements ";
        for (String iface: cf.getInterfaceNames()) {
            out.append(sep).append(relativeClassName(iface));
            sep =   ",\n               ";
        }
        out.append("\n{\n");
        indent = "    ";
        for (Field f: cf.getFields())
            visit(f);
        out.append('\n');
        for (Method m: cf.getMethods())
            visit(m);
        out.append("}\n");
    }

    private void visit(Field f) throws IOException {
        out.append(indent)
            .append(relativeClassName(f.getDescriptor()))
            .append(' ')
            .append(f.getName())
            .append(";\n");
    }

    private void visit(Method m) throws IOException {
        out.append(indent)
            .append(relativeClassName(m.getDescriptor().getReturnType()))
            .append(' ')
            .append(m.getName())
            .append('(');
        String sep = "";
        for (Descriptor.Value arg: m.getDescriptor().getArgumentTypes()) {
            out.append(sep).append(relativeClassName(arg));
            sep = ", ";
        }
        out.append(')');
        CodeAttribute code = m.getCode();
        if (code == null) {
            out.append(";\n\n");
            return;
        }
        out.append(" {\n");
        String oldIndent = indent;
        indent = indent + "    ";
        String EMPTYADDR = "       ";
        String opIndent = indent + EMPTYADDR;
        List<Op> ops = new ArrayList<>();
        for (Op op: code)
            ops.add(op);
        int addr = 0;
        for (Op op: ops) {
            String addrStr = EMPTYADDR;
            addrStr = String.format(Locale.ENGLISH, "%5d: ", addr);
            out.append(indent)
                .append(addrStr)
                .append(' ')
                .append(op.asmFormat(opIndent))
                .append("\n");
            addr += op.getNumBytes();
        }
        indent = oldIndent;
        out.append(indent).append("}\n\n");
    }

}
