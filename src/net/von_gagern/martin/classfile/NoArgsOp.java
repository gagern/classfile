package net.von_gagern.martin.classfile;

public class NoArgsOp extends Op {

    public NoArgsOp(OpCode code) {
        super(code);
    }

    public void writeTo(ClassWriter w) {
        w.write(code);
    }

}
