package net.von_gagern.martin.classfile;

class ImmediateOp extends OpWithArgs {

    int immediateValue;

    public ImmediateOp(OpCode code, int numBytes, int immediateValue) {
        super(code, numBytes);
        this.immediateValue = immediateValue;
    }

    public String formatArgs(String indent) {
        return Integer.toString(immediateValue);
    }

}
