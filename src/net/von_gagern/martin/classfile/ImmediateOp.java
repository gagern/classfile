package net.von_gagern.martin.classfile;

public class ImmediateOp extends Op {

    int immediateValue;

    public ImmediateOp(OpCode code, int immediateValue) {
        super(code);
        this.immediateValue = immediateValue;
    }

    public int getImmediateValue() {
        return immediateValue;
    }

    public void writeTo(ClassWriter w) {
        w.write(code);
        switch (code.args) {
        case I8: w.writeI1(immediateValue); break;
        case I16: w.writeI2(immediateValue); break;
        default: throw new IllegalStateException
                ("Unsupported argument format " + code.args);
        }
    }

}
