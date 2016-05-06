package net.von_gagern.martin.classfile;

public class BranchOp extends Op {

    int opCodePos;

    int offset;

    CodeLabel target;

    public BranchOp(OpCode code, int opCodePos, int offset) {
        super(code);
        this.opCodePos = opCodePos;
        this.offset = offset;
    }

    public void writeTo(ClassWriter w) {
        int base = w.posInCode();
        w.write(code);
        switch(code.args) {
        case OFF2: w.linkOffset2(target, base); break;
        case OFF4: w.linkOffset4(target, base); break;
        default: throw new IllegalStateException
                ("Unsupported argument format " + code.args);
        }            
    }

    public CodeLabel getTarget() {
        return target;
    }

}
