package net.von_gagern.martin.classfile;

public class ConstantOp extends OpWithArgs {

    int cpIndex;

    Constant constant;

    public ConstantOp(OpCode code, int numBytes, int cpIndex, ClassFile cf) {
        super(code, numBytes);
        this.cpIndex = cpIndex;
        constant = cf.getConstant(cpIndex);
    }

    public String formatArgs(String indent) {
        return "#" + cpIndex;
    }

    public Constant getConstant() {
        return constant;
    }

}
