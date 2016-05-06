package net.von_gagern.martin.classfile;

import java.util.Locale;

public class LocalVarOp extends Op {

    int varIndex;

    LocalVariableInfo var;

    boolean wide;

    public LocalVarOp(OpCode code, int varIndex) {
        super(code);
        this.varIndex = varIndex;
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

    public LocalVariableInfo getVariableInfo() {
        if (var == null)
            var = new LocalVariableInfo.IndexOnly(varIndex);
        return var;
    }

    public boolean isWide() {
        return wide;
    }

}
