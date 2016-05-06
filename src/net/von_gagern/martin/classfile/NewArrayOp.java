package net.von_gagern.martin.classfile;

import java.util.Arrays;

class NewArrayOp extends Op {

    Class<?> type;

    public NewArrayOp(Class<?> type) {
        super(OpCode.NEWARRAY);
        this.type = type;
    }

    public String formatArgs(String indent) {
        return type.toString();
    }

    public void writeTo(ClassWriter w) {
        w.write(code);
        w.writeU1(Arrays.asList(OpArgs.NEWARRAY).indexOf(type));
    }

}
