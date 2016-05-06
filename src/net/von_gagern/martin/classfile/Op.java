package net.von_gagern.martin.classfile;

import java.util.Locale;

public abstract class Op extends CodeElement implements ClassWriter.Writable {

    OpCode code;

    protected Op(OpCode code) {
        this.code = code;
    }

    public OpCode getCode() {
        return code;
    }

    public String asmFormat(String indent) {
        String str = code.toString().toLowerCase(Locale.ENGLISH);
        str = String.format((Locale)null, "%-15s", str);
        String args = formatArgs(indent);
        if (args != null)
            str = str  + " " + args;
        return str;
    }

    public abstract String formatArgs(String indent);

    public void writeCode(ClassWriter w) {
        w.write(this);
    }

}
