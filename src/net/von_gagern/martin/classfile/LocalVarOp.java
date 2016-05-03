package net.von_gagern.martin.classfile;

class LocalVarOp extends OpWithArgs {

    int varIndex;

    public LocalVarOp(OpCode code, int numBytes, int varIndex) {
        super(code, numBytes);
        this.varIndex = varIndex;
    }

    public String formatArgs(String indent) {
        return "LV" + varIndex;
    }

}
