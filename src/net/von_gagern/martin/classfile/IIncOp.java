package net.von_gagern.martin.classfile;

public class IIncOp extends LocalVarOp {

    int increment;

    public IIncOp(int varIndex, int increment) {
        super(OpCode.IINC, varIndex);
        this.increment = increment;
    }

    public int getIncrement() {
        return increment;
    }

    public void writeTo(ClassWriter w) {
        if (wide) {
            w.write(OpCode.WIDE);
            w.write(OpCode.IINC);
            w.writeU2(varIndex);
            w.writeI2(increment);
        } else {
            w.write(OpCode.IINC);
            w.writeU1(varIndex);
            w.writeI1(increment);
        }
    }

}
