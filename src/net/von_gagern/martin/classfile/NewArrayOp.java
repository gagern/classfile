package net.von_gagern.martin.classfile;

import java.util.Arrays;

public class NewArrayOp extends Op {

    Class<?> type;

    public NewArrayOp(Class<?> type) {
        super(OpCode.NEWARRAY);
        this.type = type;
    }

    public void writeTo(ClassWriter w) {
        w.write(code);
        w.writeU1(Arrays.asList(OpArgs.NEWARRAY).indexOf(type));
    }

    public String getTypeName() {
        return type.getName();
    }

}
