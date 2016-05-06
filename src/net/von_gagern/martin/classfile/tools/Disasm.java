package net.von_gagern.martin.classfile.tools;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import net.von_gagern.martin.classfile.*;

/**
 * Disassemble class files, similar to {@code javap -v}.
 *
 * If debug information (line numbers, source file name) is available,
 * and if the source file itself can be loaded
 * through the thread context class loader
 * (e.g. because the source directory is listed in the class path),
 * then the output will also contain source code lines
 * interleaved with the disassembly.
 */
public class Disasm {

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

    public Disasm(ClassFile cf, Appendable out) throws IOException {
        this.cf = cf;
        this.out = out;
        fmt = new Formatter(out);
        String name = cf.getClassName();
        String fileName = null;
        for (Attribute a: cf.getAttributes())
            if (a instanceof SourceFileAttribute)
                fileName =
                    ((SourceFileAttribute)a).getNameConstant().toString();
        int i = name.lastIndexOf('.');
        if (i > 0) {
            pkg = name.substring(0, i);
            name = name.substring(i + 1);
            if (fileName != null &&
                fileName.indexOf('/') == -1 &&
                fileName.indexOf('\\') == -1)
                fileName = pkg.replace('.', '/') + '/' + fileName;
            println("package " + pkg + ";");
        }
        if (fileName != null) readSource(fileName);
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

    private List<String> sourceLines;

    private void readSource(String name) throws IOException {
        InputStream in = Thread.currentThread().getContextClassLoader()
            .getResourceAsStream(name);
        if (in == null)
            return;
        List<String> lines = new ArrayList<>();
        BufferedReader r =
            new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String line;
        while ((line = r.readLine()) != null)
            lines.add(line);
        sourceLines = lines;
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

    private void visit(LineNumber ln) throws IOException {
        int n = ln.getLine();
        out.append(indent)
            .append(String.format((Locale)null, "#L%-5d", n));
        if (sourceLines != null && n > 0 && n <= sourceLines.size())
            out.append(sourceLines.get(n - 1));
        out.append("\n");
    }

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
        int n = op.getNumCases();
        if (op instanceof TableSwitchOp)
            fmt.format("{ // %d to %d\n", op.getMatch(0), op.getMatch(n - 1));
        else
            fmt.format("{ // %d\n", n);
        for (int i = 0; i < n; ++i) {
            int match = op.getMatch(i);
            CodeLabel target = op.getTarget(i);
            fmt.format("%s      %11d: %5d\n", indent,
                       op.getMatch(i), op.getTarget(i).getAddress());
        }
        fmt.format("%s          default: %5d\n%s}", indent,
                   op.getTarget().getAddress(), indent);
    }

}
