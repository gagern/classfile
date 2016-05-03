package net.von_gagern.martin.classfile;

class IIncOp extends LocalVarOp {

    int increment;

    public IIncOp(int numBytes, int varIndex, int increment) {
        super(OpCode.IINC, numBytes, varIndex);
        this.increment = increment;
    }

    @Override public String formatArgs(String indent) {
        return super.formatArgs(indent) + " " + increment;
    }

}
