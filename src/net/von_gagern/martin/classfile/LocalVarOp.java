package net.von_gagern.martin.classfile;

import java.util.Locale;

class LocalVarOp extends Op {

    int varIndex;

    boolean wide;

    public LocalVarOp(OpCode code, int varIndex) {
        super(code);
        this.varIndex = varIndex;
    }

    public String asmFormat(String indent) {
        String str = code.toString().toLowerCase(Locale.ENGLISH);
        if (wide) {
            str = String.format((Locale)null, "wide %-10s", str);
        } else {
            str = String.format((Locale)null, "%-15s", str);
        }
        return str + " " + formatArgs(indent);
    }

    public String formatArgs(String indent) {
        return "LV" + varIndex;
    }

    public void writeTo(ClassWriter w) {
        if (wide) {
            w.write(OpCode.WIDE);
            w.write(code);
            w.writeU2(varIndex);
        } else {
            w.write(code);
            w.writeU1(varIndex);
        }
    }

}
