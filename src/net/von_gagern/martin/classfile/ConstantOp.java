package net.von_gagern.martin.classfile;

public class ConstantOp extends Op {

    int cpIndex;

    Constant constant;

    public ConstantOp(OpCode code, int cpIndex, ClassFile cf) {
        super(code);
        this.cpIndex = cpIndex;
        constant = cf.getConstant(cpIndex);
    }

    public Constant getConstant() {
        return constant;
    }

    public void writeTo(ClassWriter w) {
        int idx = w.cpIndex(constant);
        OpCode code = this.code;
        if (code == OpCode.LDC && idx > 0xff)
            code = OpCode.LDC_W;
        if (code == OpCode.LDC_W && idx <= 0xff)
            code = OpCode.LDC;
        w.write(code);
        switch (code.args) {
        case CP1: w.writeU1(idx); break;
        case CP2: w.writeU2(idx); break;
        case INVOKEDYNAMIC: w.writeU2(idx); w.writeI2(0); break;
        default: throw new IllegalStateException
                ("Unsupported argument format " + code.args);
        }
    }

}
