package net.von_gagern.martin.classfile;

public class NoArgsOp extends Op {

    public NoArgsOp(OpCode code) {
        super(code);
    }

    @Override public String formatArgs(String indent) {
        return null;
    }

    public void writeTo(ClassWriter w) {
        w.write(code);
    }

}
