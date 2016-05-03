package net.von_gagern.martin.classfile;

class ConstantOp extends OpWithArgs {

    int cpIndex;

    public ConstantOp(OpCode code, int numBytes, int cpIndex) {
        super(code, numBytes);
        this.cpIndex = cpIndex;
    }

    public String formatArgs(String indent) {
        return "#" + cpIndex;
    }

}
