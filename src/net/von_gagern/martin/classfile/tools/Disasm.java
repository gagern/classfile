package net.von_gagern.martin.classfile.tools;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import net.von_gagern.martin.classfile.*;

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

    private Formatter fmt;

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
        fmt = new Formatter(out);
        String name = cf.getClassName();
        int i = name.lastIndexOf('.');
        if (i > 0) {
            pkg = name.substring(0, i);
            name = name.substring(i + 1);
            println("package " + pkg + ";");
        }
        AccessFlags acc = cf.getAccessFlags();
        if (acc.contains(AccessFlag.SYNTHETIC))
            out.append("/*synthetic*/ ");
        if (acc.contains(AccessFlag.PUBLIC))
            out.append("public ");
        if (acc.contains(AccessFlag.FINAL) &&
            !acc.contains(AccessFlag.ENUM))
            out.append("final ");
        if (acc.contains(AccessFlag.ABSTRACT) &&
            !acc.contains(AccessFlag.INTERFACE))
            out.append("abstract ");
        if (acc.contains(AccessFlag.ANNOTATION))
            out.append("@");
        if (acc.contains(AccessFlag.ENUM) &&
            acc.contains(AccessFlag.FINAL))
            out.append("enum ");
        else if (acc.contains(AccessFlag.INTERFACE) &&
                 acc.contains(AccessFlag.ABSTRACT))
            out.append("interface ");
        else
            out.append("class ");
        out.append(name);
        String sup = cf.getSuperClassName();
        if (!"java.lang.Object".equals(sup)) {
            out.append("\n    extends ").append(relativeClassName(sup));
        }
        String sep = "\n    implements ";
        if (acc.contains(AccessFlag.INTERFACE))
            sep = "\n    extends ";
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
        for (CodeElement elt: code.getCode()) {
            if (elt instanceof Op) {
                visit((Op)elt);
            } else if (elt instanceof LineNumber) {
                visit((LineNumber)elt);
            }
        }
        indent = oldIndent;
        out.append(indent).append("}\n\n");
    }

    private static final String EMPTYADDR = "       ";

    private void visit(Op op) throws IOException {
        out.append(indent);
        fmt.format("%5d: ", op.getAddress());
        String code = op.getCode().toString().toLowerCase(Locale.ENGLISH);
        if (op instanceof LocalVarOp && ((LocalVarOp)op).isWide())
            fmt.format(" wide %-10s", code);
        else
            fmt.format(" %-15s", code);
        if (!(op instanceof NoArgsOp)) {
            out.append(' ');
            if (op instanceof SwitchOp) {
                formatArgs((SwitchOp)op);
            } else if (op instanceof BranchOp) {
                formatArgs((BranchOp)op);
            } else if (op instanceof MultiANewArrayOp) {
                formatArgs((MultiANewArrayOp)op);
            } else if (op instanceof NewArrayOp) {
                formatArgs((NewArrayOp)op);
            } else if (op instanceof IIncOp) {
                formatArgs((IIncOp)op);
            } else if (op instanceof LocalVarOp) {
                formatArgs((LocalVarOp)op);
            } else if (op instanceof ImmediateOp) {
                formatArgs((ImmediateOp)op);
            } else if (op instanceof ConstantOp) {
                formatArgs((ConstantOp)op);
            } else {
                out.append("(argument formatting unsupported)");
            }
        }
        out.append("\n");
    }

    private void visit(LineNumber ln) throws IOException {
        out.append(indent)
            .append(String.format((Locale)null, "#L%-5d", ln.getLine()))
            .append("\n");
    }

    public void formatArgs(BranchOp op) throws IOException {
        out.append(Integer.toString(op.getTarget().getAddress()));
    }

    private void formatArgs(ConstantOp op) throws IOException {
        Constant c = op.getConstant();
        if (c instanceof Constant.String) {
            out.append(Util.quote(c.toString()));
        } else if (c instanceof Constant.Long) {
            out.append(c.toString()).append('L');
        } else if (c instanceof Constant.Float) {
            out.append(c.toString()).append('f');
        } else if (c instanceof Constant.Class) {
            out.append(c.toString()).append(".class");
        } else {
            out.append(c.toString());
        }
    }

    private void formatArgs(IIncOp op) throws IOException {
        formatArgs((LocalVarOp)op);
        out.append(' ').append(Integer.toString(op.getIncrement()));
    }

    private void formatArgs(ImmediateOp op) throws IOException {
        out.append(Integer.toString(op.getImmediateValue()));
    }

    private void formatArgs(LocalVarOp op) throws IOException {
        out.append("LV")
            .append(Integer.toString(op.getVariableInfo().getIndex()));
    }

    private void formatArgs(MultiANewArrayOp op) throws IOException {
        formatArgs((ConstantOp)op);
        out.append(' ').append(Integer.toString(op.getDimension()));
    }

    private void formatArgs(NewArrayOp op) throws IOException {
        out.append(op.getTypeName());
    }

    private void formatArgs(SwitchOp op) throws IOException {
        out.append("{\n");
        int numCases = op.getNumCases();
        for (int i = 0; i < numCases; ++i) {
            int match = op.getMatch(i);
            CodeLabel target = op.getTarget(i);
            fmt.format("%s      %11d: %5d\n", indent,
                       op.getMatch(i), op.getTarget(i).getAddress());
        }
        fmt.format("%s          default: %5d\n%s}", indent,
                   op.getTarget().getAddress(), indent);
    }

}
