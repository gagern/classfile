package net.von_gagern.martin.classfile;

class ImmediateOp extends Op {

    int immediateValue;

    public ImmediateOp(OpCode code, int immediateValue) {
        super(code);
        this.immediateValue = immediateValue;
    }

    public String formatArgs(String indent) {
        return Integer.toString(immediateValue);
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
