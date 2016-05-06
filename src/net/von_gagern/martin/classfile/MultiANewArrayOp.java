package net.von_gagern.martin.classfile;

class MultiANewArrayOp extends ConstantOp {

    int dim;

    public MultiANewArrayOp(int type, int dim, ClassFile cf) {
        super(OpCode.MULTIANEWARRAY, type, cf);
        this.dim = dim;
    }

    @Override public String formatArgs(String indent) {
        return super.formatArgs(indent) + " " + dim;
    }

    @Override public void writeTo(ClassWriter w) {
        w.write(code);
        w.write2(constant);
        w.writeU1(dim);
    }

}
