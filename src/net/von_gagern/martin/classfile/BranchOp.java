package net.von_gagern.martin.classfile;

class BranchOp extends OpWithArgs {

    int opCodePos;

    int offset;

    public BranchOp(OpCode code, int numBytes, int opCodePos, int offset) {
        super(code, numBytes);
        this.opCodePos = opCodePos;
        this.offset = offset;
    }

    public String formatArgs(String indent) {
        return Integer.toString(opCodePos + offset);
    }

}
