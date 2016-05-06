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

    public void writeCode(ClassWriter w) {
        w.write(this);
    }

}
