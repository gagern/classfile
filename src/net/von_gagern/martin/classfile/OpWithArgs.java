package net.von_gagern.martin.classfile;

abstract class OpWithArgs implements Op {

    OpCode code;

    int numBytes;

    protected OpWithArgs(OpCode code, int numBytes) {
        this.code = code;
        this.numBytes = numBytes;
    }

    public String asmFormat(String indent) {
        return code.asmFormat(indent) + " " + formatArgs(indent);
    }

    public abstract String formatArgs(String indent);

    public int getNumBytes() {
        return numBytes;
    }

}
